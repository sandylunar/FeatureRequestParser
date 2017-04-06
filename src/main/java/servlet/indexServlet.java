package main.java.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.bean.FeatureRequestOL;
import main.java.bean.Node;
import main.java.core.DataParser;
import main.java.core.RequestAnalyzer;
import main.java.parse.Parser;
import main.java.util.FeatureUtility;
import org.json.JSONObject;

/**
 * Created by zzyo on 2017/3/16.
 */
@WebServlet(name = "indexServlet", urlPatterns = "/")
public class indexServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = this.getServletContext();
        String path = context.getRealPath("/");
        String name = request.getParameter("name");
        String FRTitle = request.getParameter("FRTitle");
        String FRDes = request.getParameter("FRDes");

        Parser parser = new Parser();
        System.out.println(name + "\n\n" +
                FRTitle + "\n\n" +
                FRDes
        );

        FeatureRequestOL fr = parser.getFR(name, FRTitle, FRDes);
        String result = parser.printResult(fr);
        int sNum = parser.getSenNum();
        int bNum = parser.getBlockNum();
        System.out.println(result);

        System.out.printf("source code located in : %s\n", System.getProperty("user.dir"));
        System.out.printf("servlet code located in : %s\n", path);
        if (null == this.getServletContext().getAttribute("dataParser")) {
            DataParser dataParser = new DataParser(path);
            this.getServletContext().setAttribute("dataParser", dataParser);
        }
        DataParser dataParser = (DataParser) this.getServletContext().getAttribute("dataParser");

        //TODO update
        FeatureRequestOL loadedFR = dataParser.constructSFeatureRequestOL(fr);

        System.out.println("==============Start Loading================");
        System.out.println(loadedFR);
        System.out.println("===============END Loading=================");

        ArrayList<String> tagNames = new ArrayList<>();

        for (int i = 0; i < loadedFR.getNumSentences(); i++) {
            int predict = RequestAnalyzer.predictTagIndex(null, null, fr, i);
            String tag = RequestAnalyzer.tagNames[predict];
            tagNames.add(tag);
        }

        System.out.println(tagNames.toString());

        int countWant = 0;
        for (String tag : tagNames) {
            if (tag.equalsIgnoreCase("want"))
                countWant++;
        }

        Node root = null;

        if (countWant == 0) {
            System.out.println(countWant);
            HashSet<String> tagSets = new HashSet<>();
            for (String tag : tagNames) {
                tagSets.add(tag);
            }
            root = new Node("title", loadedFR.getTitle());
            Iterator<String> it = tagSets.iterator();
            while (it.hasNext()) {
                String tag = it.next();
                Node node;
                ArrayList<Integer> indexList = FeatureUtility.getIndexList(tagNames, tag);
                if (indexList.size() == 1) {
                    int i = indexList.get(0) - 1;
                    node = new Node(tag, loadedFR.getFullSentence(i).getOrigin());
                } else {
                    node = new Node(tag, "");
                    for (int i : indexList) {
                        Node ni = new Node(tag, loadedFR.getFullSentence(i - 1).getOrigin());
                        node.addChildren(ni);
                    }
                }
                root.addChildren(node);
            }
        }

        if (countWant == 1) {
            System.out.println(countWant);
            HashSet<String> tagSets = new HashSet<>();
            for (String tag : tagNames) {
                tagSets.add(tag);
                if (tag.equalsIgnoreCase("want"))
                    countWant++;
            }

            int index = tagNames.indexOf("want");

            root = new Node("title", loadedFR.getTitle());
            Node want = new Node("want", loadedFR.getFullSentence(index).toString());

            Iterator<String> it = tagSets.iterator();
            while (it.hasNext()) {
                String tag = it.next();
                if (tag.equals("want") || tag.equals("useless"))
                    continue;

                Node node;
                ArrayList<Integer> indexList = FeatureUtility.getIndexList(tagNames, tag);
                System.out.println(tag + "    " + indexList);
                if (indexList.size() == 1) {
                    int i = indexList.get(0) - 1;
                    node = new Node(tag, loadedFR.getFullSentence(i).getOrigin());

                } else {
                    node = new Node(tag, "");
                    for (int i : indexList) {
                        Node ni = new Node(tag, loadedFR.getFullSentence(i - 1).getOrigin());
                        node.addChildren(ni);
                    }
                }
                want.addChildren(node);
            }
            root.addChildren(want);
        }

        if (countWant > 1) {
            System.out.println(countWant);
            ArrayList<ArrayList<Integer>> blocks = loadedFR.getBlocks();
            // 合并无want的block
            ArrayList<Integer> wantList = FeatureUtility.getIndexList(tagNames, "want");
            if (blocks.size() > 1) {
                for (int b = 0; b < blocks.size(); b++) {
                    ArrayList<Integer> block = blocks.get(b);
                    for (int i = 0; i < block.size(); i++) {
                        if (wantList.contains(block.get(i) + 1)) {
                            break;
                        }
                    }
                    if (b == 0) {
                        ArrayList<Integer> tmp = blocks.get(0);
                        tmp.addAll(blocks.get(1));
                        blocks.set(0, tmp);
                        blocks.remove(1);
                    } else {
                        ArrayList<Integer> tmp = blocks.get(b - 1);
                        tmp.addAll(blocks.get(b));
                        blocks.set(b - 1, tmp);
                        blocks.remove(b);
                    }
                }
            }
            // 生成want nodes
            ArrayList<ArrayList<Integer>> wantNode = new ArrayList<>();
            for (int b = 0; b < blocks.size(); b++) {
                ArrayList<Integer> block = blocks.get(b);
                System.out.println("block size is --->" + block.size());
                for (int i = 0; i < block.size(); ) {
                    for (int j = i; j < block.size(); ) {
                        ArrayList<Integer> want = new ArrayList<>();
                        // Wantxx[Want,$]
                        if (tagNames.get(i).equalsIgnoreCase("want")) {
                            System.out.println("want start-----");
                            want.add(i);
                            // find next want
                            int next = j + 1;
                            for (; next < block.size(); next++) {
                                System.out.println(tagNames.get(next));
                                if (tagNames.get(next).equalsIgnoreCase("want")) {
                                    break;
                                }
                                want.add(next);
                            }
                            j = next;
                            System.out.println("next is --->" + next);
                            wantNode.add(want);
                        }
                        // xx[Want,$]
                        else {
                            for (; j < block.size(); j++) {
                                if (tagNames.get(j).equalsIgnoreCase("want")) {
                                    break;
                                }
                                want.add(j);
                            }
                            if (j == block.size()) {
                                ArrayList<Integer> tmp = wantNode.get(wantNode.size() - 1);
                                tmp.addAll(want);
                                wantNode.set(wantNode.size() - 1, tmp);
                            } else {
                                wantNode.add(want);
                            }
                        }
                        //System.out.println("i&j is --->" + j);
                        i = j;
                    }
                }
            }
            for (ArrayList<Integer> want : wantNode) {
                for (int i : want){
                    System.out.print(i+" ");
                }
                System.out.print("\n");
            }
            // 合并同类项
            root = new Node("title", loadedFR.getTitle());
            for (ArrayList<Integer> want : wantNode) {
                String wantStr = "";
                for (int w : want) {
                    if (tagNames.get(w).equalsIgnoreCase("want")) {
                        wantStr = loadedFR.getFullSentence(w).getOrigin();
                        System.out.println("wantStr--->" + wantStr);
                    }
                }
                Node wNode = new Node("want", wantStr);
                HashSet<String> tagSet = new HashSet<>();
                ArrayList<String> tagList = new ArrayList<>();
                for (int tagIndex : want) {
                    tagSet.add(tagNames.get(tagIndex));
                    tagList.add(tagNames.get(tagIndex));
                }
                Iterator<String> it = tagSet.iterator();
                while (it.hasNext()) {
                    String tag = it.next();
                    if (tag.equals("want") || tag.equals("useless"))
                        continue;
                    Node node;
                    ArrayList<Integer> indexList = FeatureUtility.getIndexList(tagList, tag);
                    if (indexList.size() == 1) {
                        int i = indexList.get(0) - 1;
                        node = new Node(tag, loadedFR.getFullSentence(i).getOrigin());

                    } else {
                        node = new Node(tag, "");
                        for (int i : indexList) {
                            Node ni = new Node(tag, loadedFR.getFullSentence(i - 1).getOrigin());
                            node.addChildren(ni);
                        }
                    }
                    wNode.addChildren(node);
                }
                root.addChildren(wNode);
            }
        }

        String output = root.toString();
        System.out.println(output);
        String json = "{\"sNum\":" + sNum + ",\"bNum\":" + bNum + ",\"output\":\"" + output + "\"}";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sNum", sNum);
        jsonObject.put("bNum", bNum);
        jsonObject.put("output", output);
        jsonObject.put("collapsed", countWant > 3);
        System.out.println(json);
        response.setContentType("application/json");
        //response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(jsonObject.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
        rd.forward(request, response);
    }
}