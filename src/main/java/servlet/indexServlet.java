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

        /*String json = "{\"sNum\":" + sNum + ",\"bNum\":" + bNum + ",\"output\":\""+"output"+"\"}";
        System.out.println(json);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        //response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(json);*/

        System.out.printf("source code located in : %s\n", System.getProperty("user.dir"));
        System.out.printf("servlet code located in : %s\n", path);
        //DataParser dataParser = new DataParser(path);
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


        StringBuffer buffer = new StringBuffer();
        ArrayList<String> tagNames = new ArrayList<>();

        for (int i = 0; i < loadedFR.getNumSentences(); i++) {
            int predict = RequestAnalyzer.predictTagIndex(null, null, fr, i);
            String tag = RequestAnalyzer.tagNames[predict];
            tagNames.add(tag);
            buffer.append("[" + tag + "]\n" + loadedFR.getFullSentence(i) + "\n");
        }

        //if tagNames only have one want
        int countWant = 0;
        HashSet<String> tagSets = new HashSet<>();
        for (String tag : tagNames) {
            tagSets.add(tag);
            if (tag.equalsIgnoreCase("want"))
                countWant++;
        }

        int index = tagNames.indexOf("want");

        Node root = null;

        if (countWant == 1) {
            root = new Node("title", loadedFR.getTitle());
            Node want = new Node("want", loadedFR.getFullSentence(index).toString());

            Iterator<String> it = tagSets.iterator();
            while (it.hasNext()) {
                String tag = it.next();
                if (tag.equals("want") || tag.equals("useless"))
                    continue;

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
                want.addChildren(node);

            }

            root.addChildren(want);
            System.out.println(root.tag);
            String output = root.toString();
            String json = "{\"sNum\":" + sNum + ",\"bNum\":" + bNum + ",\"output\":\"" + output + "\"}";
            //String json1 = "{\"sNum\":" + 1 + ",\"bNum\":" + 2 + ",\"output\":\'"+"output"+"\'}";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sNum", sNum);
            jsonObject.put("bNum", bNum);
            jsonObject.put("output", output);
            System.out.println(json);
            response.setContentType("application/json");
            //response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(jsonObject.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
        rd.forward(request, response);
    }
}