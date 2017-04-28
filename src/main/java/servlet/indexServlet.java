package main.java.servlet;

import java.io.IOException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.bean.FeatureRequestOL;
import main.java.bean.Node;
import main.java.core.DataParser;
import main.java.core.RequestAnalyzer;
import main.java.parse.Parser;
import main.java.predictor.FuzzyPredictorPMAMopidyActivemqAspectj;
import main.java.util.FeatureUtility;
import org.json.JSONObject;

/**
 * Created by zzyo on 2017/3/16.
 */
public class indexServlet extends HttpServlet {

    ArrayList<Integer> tmp = new ArrayList<>();
    ArrayList<ArrayList<Integer>> groups = new ArrayList<>();

    public void getGroups(int pre, int start, ArrayList<Integer> target) {
        if (start == target.size() - 1) {
            if (tmp.size() == target.size() - 1)
                groups.add(tmp);
            System.out.println(tmp);
            tmp = (ArrayList<Integer>) target.clone();
            return;
        } else {
            if (1 == pre) {
                getGroups(1, start + 1, target);
                tmp.set(start, tmp.get(start) + tmp.get(start + 1));
                tmp.remove(start + 1);
                getGroups(2, start + 1, target);
            } else if (2 == pre) {
                getGroups(2, start + 1, target);
            }
        }
    }

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
        FuzzyPredictorPMAMopidyActivemqAspectj fp = new FuzzyPredictorPMAMopidyActivemqAspectj();

        for (int i = 0; i < loadedFR.getNumSentences(); i++) {
            int predict = fp.predictTagIndex(null, null, fr, i);
            String tag = RequestAnalyzer.tagNames[predict];
            tagNames.add(tag);
        }

        System.out.println(tagNames.toString());

        int countWant = 0;
        for (String tag : tagNames) {
            if (tag.equalsIgnoreCase("intent"))
                countWant++;
        }

        Node root = null;

        if (countWant == 0) {
            System.out.println("countWant--->" + countWant);
            ArrayList<String> tagSets = new ArrayList<>();
            int useless = 0;
            for (String tag : tagNames) {
                if (tag.equalsIgnoreCase("trivia")){
                    useless = 1;
                    continue;
                }
                if (tagSets.indexOf(tag) < 0)
                {
                    tagSets.add(tag);
                }
            }
            if (1 == useless){
                tagSets.add("trivia");
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
            System.out.println("countWant--->" + countWant);
            int useless = 0;
            ArrayList<String> tagSets = new ArrayList<>();
            for (String tag : tagNames) {
                if (tag.equalsIgnoreCase("trivia")){
                    useless = 1;
                    continue;
                }
                if (tagSets.indexOf(tag) < 0)
                {
                    tagSets.add(tag);
                }
            }
            if (1 == useless){
                tagSets.add("trivia");
            }
            int index = tagNames.indexOf("intent");

            root = new Node("title", loadedFR.getTitle());
            Node want = new Node("intent", loadedFR.getFullSentence(index).toString());

            Iterator<String> it = tagSets.iterator();
            while (it.hasNext()) {
                String tag = it.next();
                if (tag.equals("intent"))
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
            System.out.println("countWant--->" + countWant);
            ArrayList<ArrayList<Integer>> blocks = loadedFR.getBlocks();
            System.out.println("Previous------");
            for (ArrayList<Integer> block : blocks) {
                for (int i : block) {
                    System.out.print(i + " ");
                }
                System.out.print("\n");
            }
            // 合并无want的block
            ArrayList<Integer> wantList = FeatureUtility.getIndexList(tagNames, "intent");
            System.out.println("wantList-------");
            for (int i : wantList) {
                System.out.println(i + " ");
            }
            if (blocks.size() > 1) {
                for (int b = 0; b < blocks.size(); b++) {
                    ArrayList<Integer> block = blocks.get(b);
                    int wantIndex = -1;
                    for (int i = 0; i < block.size(); i++) {
                        if (wantList.contains(block.get(i) + 1)) {
                            System.out.println("block" + b + "have want");
                            wantIndex = i;
                            break;
                        }
                    }
                    if (wantIndex > -1) {
                        continue;
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
            System.out.println("result-------");
            for (ArrayList<Integer> block : blocks) {
                for (int i : block) {
                    System.out.print(i + " ");
                }
                System.out.print("\n");
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
                        if (tagNames.get(block.get(i)).equalsIgnoreCase("intent")) {
                            System.out.println("want start-----");
                            want.add(block.get(i));
                            // find next want
                            int next = j + 1;
                            for (; next < block.size(); next++) {
                                if (tagNames.get(block.get(next)).equalsIgnoreCase("intent")) {
                                    break;
                                }
                                want.add(block.get(next));
                            }
                            System.out.println("next is --->" + next);
                            j = next;
                            wantNode.add(want);
                        }
                        // xx[Want,$]
                        else {
                            int afterWants = 0;
                            for (int k = j; k < block.size(); k++) {
                                if (tagNames.get(block.get(k)).equalsIgnoreCase("intent")) {
                                    afterWants++;
                                }
                            }
                            if (afterWants >= 2) {
                                ArrayList<Integer> split = new ArrayList<>();
                                int num = 0;
                                for (int k = j; k < block.size(); k++) {
                                    if (tagNames.get(block.get(k)).equalsIgnoreCase("intent")) {
                                        split.add(num);
                                        num = 0;
                                        if (k == block.size() - 1) {
                                            split.add(0);
                                        }
                                    } else {
                                        num++;
                                        if (k == block.size() - 1) {
                                            split.add(num);
                                        }
                                    }
                                }
                                tmp = (ArrayList<Integer>) split.clone();
                                getGroups(1, 0, split);
                                Collections.reverse(groups);
                                System.out.println(groups);
                                ArrayList<Double> sd = new ArrayList<>();
                                double s = 0;
                                for (ArrayList<Integer> list : groups) {
                                    int sum = 0;
                                    for (int ii : list) {
                                        sum += ii;
                                    }
                                    double avg = sum / list.size(), dec;
                                    for (int ii = 0; ii < list.size(); ii++) {
                                        dec = list.get(ii) - avg;
                                        s += dec * dec;
                                    }
                                    s = s / list.size();
                                    sd.add(s);
                                    System.out.println("标准差--->" + s);
                                    s = 0;
                                }
                                int index = sd.indexOf(Collections.min(sd));
                                ArrayList<Integer> nodeList = groups.get(index);
                                System.out.println("best divide--->" + nodeList);
                                for (int wIndex = 0; wIndex < nodeList.get(0) + 1; wIndex++) {
                                    System.out.println(block.get(j));
                                    want.add(block.get(j));
                                    j++;
                                }
                                System.out.println("best--->" + want);
                                wantNode.add(want);
                            } else {
                                for (; j < block.size(); j++) {
                                    want.add(block.get(j));
                                    if (tagNames.get(block.get(j)).equalsIgnoreCase("intent")) {
                                        break;
                                    }
                                }
                                if (j == block.size() && !tagNames.get(block.get(j - 1)).equalsIgnoreCase("intent")) {
                                    ArrayList<Integer> tmp = wantNode.get(wantNode.size() - 1);
                                    tmp.addAll(want);
                                    wantNode.set(wantNode.size() - 1, tmp);
                                } else {
                                    wantNode.add(want);
                                }
                                j++;
                            }
                        }
                        //System.out.println("i&j is --->" + j);
                        i = j;
                    }
                }
            }
            for (ArrayList<Integer> want : wantNode) {
                for (int i : want) {
                    System.out.print(i + " ");
                }
                System.out.print("\n");
            }
            // 合并同类项
            root = new Node("title", loadedFR.getTitle());
            for (ArrayList<Integer> want : wantNode) {
                String wantStr = "";
                for (int w : want) {
                    if (tagNames.get(w).equalsIgnoreCase("intent")) {
                        wantStr = loadedFR.getFullSentence(w).getOrigin();
                        System.out.println("wantStr--->" + wantStr);
                    }
                }
                Node wNode = new Node("intent", wantStr);
                ArrayList<String> tagSet = new ArrayList<>();
                int useless = 0;
                ArrayList<String> tagList = new ArrayList<>();
                for (int tagIndex : want) {
                    tagList.add(tagNames.get(tagIndex));
                    if (tagNames.get(tagIndex).equalsIgnoreCase("trivia")){
                        useless = 1;
                        continue;
                    }
                    if (tagSet.indexOf(tagNames.get(tagIndex)) < 0){
                        tagSet.add(tagNames.get(tagIndex));
                    }
                }
                if (1 == useless){
                    tagSet.add("trivia");
                }
                Iterator<String> it = tagSet.iterator();
                while (it.hasNext()) {
                    String tag = it.next();
                    System.out.println(tag);
                    if (tag.equals("intent"))
                        continue;
                    Node node;
                    ArrayList<Integer> indexList = FeatureUtility.getIndexList(tagList, tag);
                    if (indexList.size() == 1) {
                        int i = indexList.get(0) - 1;
                        node = new Node(tag, loadedFR.getFullSentence(want.get(i)).getOrigin());

                    } else {
                        node = new Node(tag, "");
                        for (int i : indexList) {
                            Node ni = new Node(tag, loadedFR.getFullSentence(want.get(i - 1)).getOrigin());
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