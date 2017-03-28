package main.java.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
        PrintWriter printWriter = response.getWriter();
        
        Parser parser = new Parser();
        //parser.parseCode(FRDes);
        System.out.println(name + "\n\n\n\n\n" +
                FRTitle + "\n\n\n\n\n" +
                FRDes
        );
        
        
        FeatureRequestOL fr = parser.getFR(name, FRTitle, FRDes);
        String result = parser.printResult(fr);
        
        
		System.out.printf("source code located in : %s\n",System.getProperty("user.dir"));
		System.out.printf("servlet code located in : %s\n",path);
		DataParser dataParser = new DataParser(path);
        
		//TODO update
		FeatureRequestOL loadedFR = dataParser.constructSFeatureRequestOL(fr);
        
        System.out.println("==============Start Loading================");
        System.out.println(loadedFR);
        System.out.println("===============END Loading=================");
        
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> tagNames = new ArrayList<String>();
        
        for(int i = 0; i < loadedFR.getNumSentences(); i++){
        	int predict = RequestAnalyzer.predictTagIndex(null,null,fr, i);
        	String tag = RequestAnalyzer.tagNames[predict];
        	tagNames.add(tag);
        	buffer.append("["+tag+"]\n"+loadedFR.getFullSentence(i)+"\n");
        }
        
        //if tagNames only have one want
        int countWant = 0;
        HashSet<String> tagSets = new HashSet<String>();
        for(String tag : tagNames){
        	tagSets.add(tag);
        	if(tag.equalsIgnoreCase("want"))
        		countWant++;
        }
        
        int index = tagNames.indexOf("want");
        
        Node root = null;
        if(countWant == 1){
        	root = new Node("title",loadedFR.getTitle());
        	Node want = new Node("want",loadedFR.getFullSentence(index).toString());
        	
        	
        	
        	Iterator<String> it = tagSets.iterator();
        	while(it.hasNext()){
        		String tag = it.next();
        		if(tag.equals("want")||tag.equals("useless"))
        			continue;
        		
        		Node node;
        		ArrayList<Integer> indexList = FeatureUtility.getIndexList("", tagNames, tag);
        		if(indexList.size() == 1){
        			int i = indexList.get(0)-1;
        			node = new Node(tag,loadedFR.getFullSentence(i).toString());
        			
        			
        		}else{
        			node = new Node(tag,"");
        			for(int i : indexList){
        				Node ni = new Node(tag,loadedFR.getFullSentence(i-1).toString());
        				node.addChildren(ni);
        			}
        		}
        		want.addChildren(node);
        		
        	}
        	
        	
        	root.addChildren(want);
        	String output = root.toString();
        	System.out.println(output);
        	printWriter.print("\n====================================\n"+output);
        }
        
        
        
        
        
        
        //request.setAttribute("parseResult", result);
        //RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/show.jsp");
        //rd.forward(request, response);
        
        System.out.println(result);
        printWriter.print("\n====================================\n"+buffer);
        printWriter.print("\n====================================\n"+root);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
        rd.forward(request, response);
    }
}
