package main.java.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.EditDistance;
import main.java.bean.FeatureRequestOL;
import main.java.util.FeatureUtility;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class RequestAnalyzer {
	static String modelOutputDir = "resource//models//";
	static PrintWriter out = null;
	static String outputFile = "resource//precision_results_non-text.txt";
	static boolean printResult = false;
	static String[] rawAttributeNames;
	public static String[] tagNames = new String[] { "explanation", "want", "useless" ,"benefit","drawback","example"};
	static int tagSize = tagNames.length;

	// TODO
	public static boolean classifyAsUseless(String content, double verbsAllInvalid) {

		if (verbsAllInvalid == 1)
			return true;

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			return true;

		return false;
	}

	// TODO
	public static boolean classifyAsWant(Instance item, Instances data) {

		String content = item.stringValue(0);

		double isRealFirst = item.value(data.attribute("isRealFirst"));
		double matchMDGOODVB = item.value(data.attribute("matchMDGOODVB"));

		double startWithVB = item.value(data.attribute("startWithVB"));
		double question = item.value(data.attribute("question"));
		double matchMDGOOD = item.value(data.attribute("matchMDGOOD"));

		// "want to","can"
		String[] pattern = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "should", "add support", "pma may", "may want",
				"be able to", "need is", "phpmyadmin may", "would want to", "we need", "I need", "to support" };

		String[] pattern2 = new String[] { "how about", "what about" };

		String[] pattern3 = new String[] { "idea", "request", }; // "feature","option","consider",

		if (question == 1) {
			if (FeatureUtility.isContain(content, pattern2))
				return true;

			else
				return false;
		}

		if (FeatureUtility.isContain(content, pattern) || matchMDGOODVB == 1) {
			return true;
		}

		if (FeatureUtility.noSemicolonBeforePattern(content, pattern3)) {
			return true;
		}

		if (isRealFirst == 1 && startWithVB == 1)
			return true;

		if (classifyAsExp(item, data))
			return false;

		return false;
	}

	private static void createPrintWriter(String outputFile) {
		try {
			out = new PrintWriter(new FileOutputStream(new File(outputFile), true), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void predictTag(Instances data) {

		// createPrintWriter("resource//precision_results_non-text.txt");

		Attribute tag = data.attribute("tag");
		int count = 0;
		int[][] matrix = new int[tagSize][tagSize];
		ListIterator<Instance> list = data.listIterator();
		StringBuffer buffer[] = new StringBuffer[tagSize];
		
		data.setClassIndex(tag.index());

		System.out.println("Negitive predictions: ");
		
		for(int i = 0; i < tagSize; i++){
			buffer[i] = new StringBuffer();
		}
		
		while (list.hasNext()) {
			Instance item = list.next();
			int index = (int) item.classValue();
			String sentence = item.stringValue(0);

			int predict = predictTagIndex(item, data,null,-1);
			matrix[index][predict]++;
			if (index == predict) {
				continue;
			}

			count++;
			String output = String.format("%d - %s - predict to be %s : %s\n",count, tagNames[index], tagNames[predict], sentence);
			buffer[index].append(output);
			
			if (index == 1) {
				
				// if(predict == 2)
				//System.out.println(count + " - WANT - predict to be " + tagNames[predict] + ":" + sentence);
			}

			if (index == 0) {
				// if(predict==2)
				//System.out.println(count + " - EXP - predict to be " + tagNames[predict] + ":" + sentence);
			}
			if (index == 2) {
				// if(predict==1)
				//System.out.println(count + " - USELESS - predict to be " + tagNames[predict] + ":" + sentence);
			}
			//if(index >2 && (predict==0||predict>2))
				//System.out.printf("%d - %s - predict to be %s : %s\n",count, tagNames[index], tagNames[predict], sentence);
			
		
			
			
		}
		
		for(int i = 0; i < tagSize; i++){
			System.out.println("\nActual tag  = "+tagNames[i]);
			System.out.print(buffer[i]);
		}

		System.out.println(matrix);

		double[][] evaluation = new double[tagNames.length][2];
		for (int i = 0; i < tagNames.length; i++) {
			double sump=0;
			double sumr=0;
			
			for(int j = 0; j < tagNames.length; j++ ){
				sump+=matrix[j][i];
				sumr+=matrix[i][j];
			}
			
			evaluation[i][0] = matrix[i][i] /  sump;
			evaluation[i][1] = matrix[i][i] / sumr;
			System.out.printf("Class=%s precision=%.2f recall=%.2f\n", tagNames[i], evaluation[i][0], evaluation[i][1]);
		}

	}
	
	
	public static int predictTagIndex(FeatureRequestOL request, int index){
		String originContent =  request.getSentence(index);
		String subject = request.getSubjects(index);
		String action = request.getActions(index);

		double isRealFirst = request.getIsRealFirst(index);
		double matchMDGOODVB = request.getMatchMDGOODVB(index);
		
		double startWithVB = request.getStartWithVB(index);
		double question = request.getQuestion(index);
		double matchVBDGOOD = request.getMatchVBDGOODB(index);
		double numValidWords = request.getNumValidWords(index);
		double matchMDGOOD = request.getMatchMDGOOD(index);
		double containNEG = request.getContainEXP(index);
		double similarityToTitle = request.getSimilairity(index);
		double matchMDGOODIF = request.getMatchGOODIF(index);
		double matchGOODIF = request.getMatchGOODIF(index);
		double matchSYSNEED = request.getMatchSYSNEED(index);
		double isPastTense = request.getIsPastTense(index);
		double sentimentScore = request.getSentimentScore(index);
		double sentimentProbability = request.getSentimentProbability(index);
		double numValidVerbs = request.getNumValidVerbs(index);

		// "want to","can"
		String[] pattern = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", "able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] pattern2 = new String[] { "how about", "what about" };

		String[] pattern3 = new String[] { "idea", };// "feature","request","consider",
														// "option",

		String[] expPattern = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		if (originContent.contains("This suite can help us measure performance and memory hotspots in 1.2 development"))
			System.out.println();

		String content = originContent.replaceAll("[(].*[)]", "");
		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if (numValidWords == 0 || numValidVerbs == 0) {
			result = 2;
			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;
		else if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		} else if (matchVBDGOOD == 1 || containNEG == 1 || content.toLowerCase().startsWith("in addition")
				|| content.toLowerCase().startsWith("also ") || content.toLowerCase().startsWith("so ")
				|| content.toLowerCase().startsWith("perhaps") || content.toLowerCase().startsWith("by default")
				|| content.toLowerCase().startsWith("maybe") || content.toLowerCase().contains("for example")
				|| content.toLowerCase().startsWith("given") || content.toLowerCase().startsWith("unfortunately")
				|| content.toLowerCase().startsWith("similarly") || content.toLowerCase().contains("why")) {

			result = 0;
		} else if (question == 1 && FeatureUtility.isContain(content, pattern2)) {
			result = 1;

		} else if (question == 1 && content.toLowerCase().contains("reason")) {
			result = 0;
		} else if ((FeatureUtility.isContain(content, pattern) || matchMDGOODVB == 1) && numValidWords > 1) {
			result = 1;
		} else if (FeatureUtility.isContain(content, expPattern) && numValidWords > 1) {
			result = 0;
		} else if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		} else if (content.toLowerCase().contains("should") && question == 0)
			result = 1;
		else if (numValidVerbs == 0 && matchMDGOOD == 0 && containNEG == 0) {
			result = 2;
			if (numValidWords >= 2)
				result = 0;

			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		} else if (numValidWords < 2) {
			result = 2;
			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		} else if (FeatureUtility.noSemicolonBeforePattern(content, pattern3)) {
			result = 1;
		} else if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		if (content.toLowerCase().contains("there should be") || content.toLowerCase().contains("would like to")
				|| content.toLowerCase().contains("i'd like") || content.toLowerCase().contains("the request is:")) {
			if (!content.toLowerCase().contains("would like to work"))
				result = 1;
		}

		if (content.toLowerCase().startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3)
			result = 0;

		if (content.toLowerCase().contains("would help") && matchMDGOODIF == 0)
			result = 0;

		if (content.toLowerCase().contains("would like to know why") || content.toLowerCase().contains("because")
				|| content.toLowerCase().contains("since") || content.toLowerCase().startsWith("but")
				|| content.toLowerCase().startsWith("maybe") || content.toLowerCase().startsWith("like"))
			result = 0;

		// if(matchMDGOODVB == 1) result = 0;

		// if ((FeatureUtility.isContain(content, pattern) || matchMDGOODVB ==
		// 1) && validWords>1 ) { result = 1;}

		if (content.toLowerCase().contains("suggestion") && question == 0)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (isRealFirst == 1 && (content.toLowerCase().startsWith("goal")))
			result = 1;

		if (isRealFirst == 1 && (content.toLowerCase().contains("we could")))
			result = 1;

		if (content.toLowerCase().contains("we should"))
			result = 1;

		if (content.toLowerCase().contains("whether we should")
				|| content.toLowerCase().contains("if you think we should"))
			result = 0;

		if (matchGOODIF == 1 && content.toLowerCase().contains("i think"))
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;


		if (content.toLowerCase().contains("look forward") || content.toLowerCase().contains("would like to work")
				|| content.toLowerCase().contains("willing to contribute")
				|| content.toLowerCase().contains("please give your suggestion"))
			result = 2;

		if ((content.toLowerCase().contains("should") || (content.toLowerCase().startsWith("an option")))
				&& isRealFirst == 1 && !FeatureUtility.isContain(content, expPattern))
			result = 1;

		if (content.contains("<link-http>") || content.contains("<issue-link>") || content.contains("<COMMAND>")
				|| content.contains("<CODE>") || content.contains("<list>") || content.contains("<html-link>")
				|| content.contains("<PATH>") || content.contains("<FILE>") || content.contains("<http-link>")
				|| content.contains("<FILE-SYS>") || content.contains("<FILE-XML>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<file-path>"))
			result = 0;

		boolean exp1 = content.toLowerCase().contains("current") || content.toLowerCase().startsWith("to do this")
				|| content.toLowerCase().startsWith("possibly") || content.toLowerCase().contains("something like")
				|| content.toLowerCase().contains("just like") || content.toLowerCase().contains("benefits:")
				|| content.toLowerCase().endsWith(":") || content.toLowerCase().contains("i ever use")
				|| content.toLowerCase().startsWith("consequently") || content.toLowerCase().contains("limitation");
		if (exp1) {
			result = 0;

			if (matchMDGOODVB == 1 && isRealFirst == 1) {
				result = 1; 
			}

		}

		boolean want1 = content.toLowerCase().contains("add") && content.toLowerCase().contains("support")
				|| content.toLowerCase().contains("needs to support") || content.toLowerCase().startsWith("let's")
				|| content.toLowerCase().contains("i'm asking for") || content.toLowerCase().contains("asked for")
				|| content.toLowerCase().contains("basic idea") || content.toLowerCase().contains("we need");
		if (want1) {
			result = 1;

		}

		if (content.toLowerCase().contains("really") && question == 1)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		if ((content.toLowerCase().startsWith("is there")) && question == 1)
			result = 1;

		if ((content.toLowerCase().contains("i want")) && isRealFirst == 1)
			result = 1;

		if (content.toLowerCase().startsWith("there is a need") || content.toLowerCase().startsWith("would it")
				|| content.toLowerCase().startsWith("i needed this") || content.toLowerCase().startsWith("i will")
				|| content.toLowerCase().contains("i'm considering")
				|| content.toLowerCase().contains("i am considering") || content.toLowerCase().contains("i am planning")
				|| content.toLowerCase().contains("i'm planning") || content.toLowerCase().contains("why don't we")
				|| content.toLowerCase().contains("looking for a feature")
				|| content.toLowerCase().contains("need to be supported"))
			result = 1;

		if (content.toLowerCase().contains("to do this"))
			result = 0;

		if (action != null && action.length() != 0) {
			if (action.equalsIgnoreCase("mean"))
				result = 0;
			if (action.equalsIgnoreCase("propose"))
				result = 1;

			if (action.equalsIgnoreCase("support")) {
				if (subject != null && subject.length() != 0) {
					if (subject.equals("we"))
						result = 1;
				}
			}

			if (subject != null && subject.length() != 0) {
				if (subject.toLowerCase().equals("proposal"))
					result = 1;
			}

		}

		if (content.toLowerCase().contains("must")) {
			boolean prpmust = content.toLowerCase().contains("you must") || content.toLowerCase().contains("it must")
					|| content.toLowerCase().contains("that must") || content.toLowerCase().contains("which must")
					|| content.toLowerCase().contains("i must");

			if (prpmust)
				result = 0;
			else
				result = 1;
		}

		if (content.toLowerCase().startsWith("unfortunately") || content.toLowerCase().startsWith("actually"))
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;
		
		if(FeatureUtility.matchNOTONLY(content))
			result = 1;

		// if(sentimentScore<2)
		// result = 0;

		return result;
	}

	public static int predictTagIndex(Instance item, Instances data, FeatureRequestOL request, int index) {
		String originContent;
		String subject;
		String action;
		double isRealFirst;
		double matchMDGOODVB;
		double startWithVB;
		double question;
		double matchVBDGOOD;
		double numValidWords;
		double matchMDGOOD;
		double containNEG;
		double similarityToTitle;
		double matchMDGOODIF;
		double matchGOODIF;
		double matchSYSNEED;
		double isPastTense;
		double sentimentScore;
		double sentimentProbability;
		double numValidVerbs;
		double matchIsGOOD ;
		double matchIsNotGOOD ;
		double matchIsBAD ;
		double matchIsNotBAD ;
		
		if(index == -1 && request == null){
		
		originContent = item.stringValue(0);
		subject = item.stringValue(data.attribute("subjects"));
		action = item.stringValue(data.attribute("actions"));

		 isRealFirst = item.value(data.attribute("isRealFirst"));
		 matchMDGOODVB = item.value(data.attribute("matchMDGOODVB"));
		 startWithVB = item.value(data.attribute("startWithVB"));
		 question = item.value(data.attribute("question"));
		 matchVBDGOOD = item.value(data.attribute("matchVBDGOOD"));
		 numValidWords = item.value(data.attribute("numValidWords"));
		 matchMDGOOD = item.value(data.attribute("matchMDGOOD"));
		 containNEG = item.value(data.attribute("containNEG"));
		 similarityToTitle = item.value(data.attribute("similarityToTitle"));
		 matchMDGOODIF = item.value(data.attribute("matchMDGOODIF"));
		 matchGOODIF = item.value(data.attribute("matchGOODIF"));
		 matchSYSNEED = item.value(data.attribute("matchSYSNEED"));
		 isPastTense = item.value(data.attribute("isPastTense"));
		 sentimentScore = item.value(data.attribute("sentimentScore"));
		 sentimentProbability = item.value(data.attribute("sentimentProbability"));
		 numValidVerbs = item.value(data.attribute("numValidVerbs"));
		 matchIsGOOD = item.value(data.attribute("matchIsGOOD"));
		 matchIsNotGOOD = item.value(data.attribute("matchIsNotGOOD"));
		 matchIsBAD = item.value(data.attribute("matchIsBAD"));
		 matchIsNotBAD = item.value(data.attribute("matchIsNotBAD"));
		}
		
		else{
			originContent =  request.getSentence(index);
			subject = request.getSubjects(index);
			action = request.getActions(index);

			 isRealFirst = request.getIsRealFirst(index);
			 matchMDGOODVB = request.getMatchMDGOODVB(index);
			
			 startWithVB = request.getStartWithVB(index);
			 question = request.getQuestion(index);
			 matchVBDGOOD = request.getMatchVBDGOODB(index);
			 numValidWords = request.getNumValidWords(index);
			 matchMDGOOD = request.getMatchMDGOOD(index);
			 containNEG = request.getContainEXP(index);
			 similarityToTitle = request.getSimilairity(index);
			 matchMDGOODIF = request.getMatchGOODIF(index);
			 matchGOODIF = request.getMatchGOODIF(index);
			 matchSYSNEED = request.getMatchSYSNEED(index);
			 isPastTense = request.getIsPastTense(index);
			 sentimentScore = request.getSentimentScore(index);
			 sentimentProbability = request.getSentimentProbability(index);
			 numValidVerbs = request.getNumValidVerbs(index);
			 matchIsGOOD = request.getMatchIsGOOD(index);
			 matchIsNotGOOD = request.getMatchIsNotGOOD(index);
			 matchIsBAD = request.getMatchIsBAD(index);
			 matchIsNotBAD = request.getMatchIsNotBAD(index);
			 
			 
		}
		

		// "want to","can"
		String[] pattern = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", "able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] pattern2 = new String[] { "how about", "what about" };

		String[] pattern3 = new String[] { "idea", };// "feature","request","consider",
														// "option",

		String[] expPattern = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		if (originContent.contains("For compiler errors it is usually possible for the bug raiser to attach a simple testcase"))
			System.out.println();

		String content = originContent.replaceAll("[(].*[)]", "");
		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if (numValidWords == 0 || numValidVerbs == 0) {
			result = 2;
			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;
		else if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		} else if (matchVBDGOOD == 1 || containNEG == 1 || content.toLowerCase().startsWith("in addition")
				|| content.toLowerCase().startsWith("also ") || content.toLowerCase().startsWith("so ")
				|| content.toLowerCase().startsWith("perhaps") || content.toLowerCase().startsWith("by default")
				|| content.toLowerCase().startsWith("maybe") || content.toLowerCase().contains("for example")
				|| content.toLowerCase().startsWith("given") || content.toLowerCase().startsWith("unfortunately")
				|| content.toLowerCase().startsWith("similarly") || content.toLowerCase().contains("why")) {

			result = 0;
		} else if (question == 1 && FeatureUtility.isContain(content, pattern2)) {
			result = 1;

		} else if (question == 1 && content.toLowerCase().contains("reason")) {
			result = 0;
		} else if ((FeatureUtility.isContain(content, pattern) || matchMDGOODVB == 1) && numValidWords > 1) {
			result = 1;
		} else if (FeatureUtility.isContain(content, expPattern) && numValidWords > 1) {
			result = 0;
		} else if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		} else if (content.toLowerCase().contains("should") && question == 0)
			result = 1;
		else if (numValidVerbs == 0 && matchMDGOOD == 0 && containNEG == 0) {
			result = 2;
			if (numValidWords >= 2)
				result = 0;

			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		} else if (numValidWords < 2) {
			result = 2;
			if (sentimentScore < 2 && sentimentProbability > 0.6)
				result = 0;
		} else if (FeatureUtility.noSemicolonBeforePattern(content, pattern3)) {
			result = 1;
		} else if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		if (content.toLowerCase().contains("there should be") || content.toLowerCase().contains("would like to")
				|| content.toLowerCase().contains("i’d like") || content.toLowerCase().contains("the request is:")) {
			if (!content.toLowerCase().contains("would like to work"))
				result = 1;
		}

		if (content.toLowerCase().startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3)
			result = 0;

		if (content.toLowerCase().contains("would help") && matchMDGOODIF == 0)
			result = 0;

		if (content.toLowerCase().contains("would like to know why") || content.toLowerCase().contains("because")
				|| content.toLowerCase().contains("since") || content.toLowerCase().startsWith("but")
				|| content.toLowerCase().startsWith("maybe") || content.toLowerCase().startsWith("like"))
			result = 0;

		// if(matchMDGOODVB == 1) result = 0;

		// if ((FeatureUtility.isContain(content, pattern) || matchMDGOODVB ==
		// 1) && validWords>1 ) { result = 1;}

		if (content.toLowerCase().contains("suggestion") && question == 0)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (isRealFirst == 1 && (content.toLowerCase().startsWith("goal")))
			result = 1;

		if (isRealFirst == 1 && (content.toLowerCase().contains("we could")))
			result = 1;

		if (content.toLowerCase().contains("we should"))
			result = 1;

		if (content.toLowerCase().contains("whether we should")
				|| content.toLowerCase().contains("if you think we should"))
			result = 0;

		if (matchGOODIF == 1 && content.toLowerCase().contains("i think"))
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		if (content.toLowerCase().contains("look forward") || content.toLowerCase().contains("would like to work")
				|| content.toLowerCase().contains("willing to contribute")
				|| content.toLowerCase().contains("please give your suggestion"))
			result = 2;

		if ((content.toLowerCase().contains("should") || (content.toLowerCase().startsWith("an option")))
				&& isRealFirst == 1 && !FeatureUtility.isContain(content, expPattern))
			result = 1;

		if (content.contains("<link-http>") || content.contains("<issue-link>") || content.contains("<COMMAND>")
				|| content.contains("<CODE>") || content.contains("<list>") || content.contains("<html-link>")
				|| content.contains("<PATH>") || content.contains("<FILE>") || content.contains("<http-link>")
				|| content.contains("<FILE-SYS>") || content.contains("<FILE-XML>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<file-path>"))
			result = 0; 

		boolean exp1 = content.toLowerCase().contains("current") || content.toLowerCase().startsWith("to do this")
				|| content.toLowerCase().startsWith("possibly") || content.toLowerCase().contains("something like")
				|| content.toLowerCase().contains("just like") || content.toLowerCase().contains("benefits:")
				|| content.toLowerCase().endsWith(":") || content.toLowerCase().contains("i ever use")
				|| content.toLowerCase().startsWith("consequently") || content.toLowerCase().contains("limitation");
		if (exp1) {
			result = 0;

			if (matchMDGOODVB == 1 && isRealFirst == 1) {
				result = 1;
			}

		}

		boolean want1 = content.toLowerCase().contains("add") && content.toLowerCase().contains("support")
				|| content.toLowerCase().contains("needs to support") || content.toLowerCase().startsWith("let's")
				|| content.toLowerCase().contains("i'm asking for") || content.toLowerCase().contains("asked for")
				|| content.toLowerCase().contains("basic idea") || content.toLowerCase().contains("we need");
		if (want1) {
			result = 1;

		}

		if (content.toLowerCase().contains("really") && question == 1)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		if ((content.toLowerCase().startsWith("is there")) && question == 1)
			result = 1;

		if ((content.toLowerCase().contains("i want")) && isRealFirst == 1)
			result = 1;

		if (content.toLowerCase().startsWith("there is a need") || content.toLowerCase().startsWith("would it")
				|| content.toLowerCase().startsWith("i needed this") || content.toLowerCase().startsWith("i will")
				|| content.toLowerCase().contains("i'm considering")
				|| content.toLowerCase().contains("i am considering") || content.toLowerCase().contains("i am planning")
				|| content.toLowerCase().contains("i'm planning") || content.toLowerCase().contains("why don't we")
				|| content.toLowerCase().contains("looking for a feature")
				|| content.toLowerCase().contains("need to be supported"))
			result = 1;

		if (content.toLowerCase().contains("to do this"))
			result = 0;

		if (action != null && action.length() != 0) {
			if (action.equalsIgnoreCase("mean"))
				result = 0;
			if (action.equalsIgnoreCase("propose"))
				result = 1;

			if (action.equalsIgnoreCase("support")) {
				if (subject != null && subject.length() != 0) {
					if (subject.equals("we"))
						result = 1;
				}
			}

			if (subject != null && subject.length() != 0) {
				if (subject.toLowerCase().equals("proposal"))
					result = 1;
			}

		}

		if (content.toLowerCase().contains("must")) {
			boolean prpmust = content.toLowerCase().contains("you must") || content.toLowerCase().contains("it must")
					|| content.toLowerCase().contains("that must") || content.toLowerCase().contains("which must")
					|| content.toLowerCase().contains("i must");

			if (prpmust)
				result = 0;
			else
				result = 1;
		}

		if (content.toLowerCase().startsWith("unfortunately") || content.toLowerCase().startsWith("actually"))
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;
		
		if(FeatureUtility.matchNOTONLY(content))
			result = 1;
		
		if( FeatureUtility.matchMDAllow(content)){ //||	containGood matchMDGOOD == 1 ||
			result = 3; //benefit
			}

		// if(sentimentScore<2)
		// result = 0;

		//TODO
		if(result != 0)
			return result;
		
		
		
		if (originContent.contains("You can only use some standard tokens for this annotation"))
			System.out.println();
		
		if(content.toLowerCase().contains("should"))
			result = 0;
		
		boolean containGood = FeatureUtility.isContain(content, FeatureUtility.GOOD);
		
		if( FeatureUtility.matchMDAllow(content)){ //||	containGood matchMDGOOD == 1 ||
			result = 3; //benefit
			}
		
		//drawback
		//if (sentimentScore < 1 && sentimentProbability > 0.6) 
		//	result = 4;	
		
		//Sys-Name does not support
		
		//we had an issue
		
		//not a good idea
		
		if(matchIsBAD == 1 || matchIsNotGOOD ==1) 	//[20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
			result = 4;
		
		if(content.toLowerCase().contains("would like to know why"))
			result = 4;
		
		//if(content.toLowerCase().contains("only")||content.toLowerCase().contains("current"))
			//result = 4;
		
		//if(FeatureUtility.checkContains(content, FeatureUtility.BAD, true))
		//	result = 4;
		
		//example
		boolean example1 =  content.toLowerCase().contains("for example")
				||content.toLowerCase().contains("similar to")
				||content.toLowerCase().startsWith("like");
		if(example1)
			result = 5;
		
		if(content.toLowerCase().startsWith("other"))
			result = 5;
		
		if(content.toLowerCase().endsWith("following:"))
			result = 5;
		
		boolean containlink = content.contains("<link-http>")//|| content.contains("<CODE>")  || content.contains("<issue-link>")
				  || content.contains("<http-link>") || content.contains("<html-link>")|| content.contains("<EXAMPLE>")
				 || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP");
		if ( (containlink||content.toLowerCase().contains(" like "))  && (numValidVerbs <2  && numValidWords < 10))
			result = 5; 
		
		
		
		if(content.matches(".*:[\\s]*<CODE>.*"))
			result = 5;
		
		if(content.equalsIgnoreCase("<CODE>"))
			result = 5;
		
		//explanation
		if(content.toLowerCase().contains("current"))
			result = 0;
		
		
		
		//benefit 
		if( FeatureUtility.matchMDAllow(content)){  //||containGood matchMDGOOD == 1 ||
			result = 3; 
			}
		
		//[20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		if(matchIsGOOD == 1 ) //|| matchIsNotBAD == 1 && !content.toLowerCase().contains("but")
			result = 3;
		
		//example
		if(content.toLowerCase().contains("something like"))
			result = 5;
		
		//bad
		if(matchIsBAD == 1 ) 	//[20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
			result = 4;
		
		if(matchVBDGOOD == 1)
			result = 4;
		
		//if(containNEG==1&&containGood)
		//	result = 4;
		//if(question==1&&containGood)
			//result=4;
		if(content.matches(".*cause[^,.;?\"']*problem.*"))
			result=4;
		if(content.matches(".*not[^,.;?\"']*enough.*") && !content.toLowerCase().contains("not familiar enough"))
			result=4;
		
		if(content.toLowerCase().contains("without success"))
			result = 4;
		
		//example
		if(content.toLowerCase().contains("for example")
				||content.toLowerCase().contains("similar to")
				||content.toLowerCase().startsWith("like"))
			result = 5;
		
		//benefit
		
		//startswith good
		
		String startWord = content.split(" ")[0];
		if(FeatureUtility.isContain(startWord,FeatureUtility.GOOD_BENEFIT)){
			result = 3;
		}
		
		if(content.toLowerCase().contains("won't have to")||content.toLowerCase().contains("no longer need")||
				content.toLowerCase().contains("could reduce")||content.toLowerCase().contains("benefit"))
			result = 3; 
		
		if(content.matches(".*would[^,.;?\"']*allow.*"))
			result=3;
		
		if(content.matches(".*save[^,.;?\"']*time.*"))
			result=3;
		
		if(content.matches(".*save[^,.;?\"']*memory.*"))
			result=3;
		
		
		if(content.toLowerCase().startsWith("having this"))
			result = 3;
		
		if(content.toLowerCase().contains("could just")||content.toLowerCase().contains("can just")||
				content.toLowerCase().contains("a great feature")||content.toLowerCase().contains("give a value"))  
			result = 3;
		
		
		//help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if(matchHelpSystem)
			result = 3;
		
		
		if(numValidVerbs==1&&subject.equalsIgnoreCase("we")&&action.equalsIgnoreCase("work"))
			result = 0;
		
		if(content.toLowerCase().startsWith("ideally")||content.toLowerCase().startsWith("note"))
			result = 0;
		
		if(content.toLowerCase().startsWith("in order to")&&isRealFirst == 1)
			result = 0;
		
		if(content.toLowerCase().contains("goal"))
			result = 1;
		
		//example
		if(content.toLowerCase().contains("something like"))
			result = 5;
		
		if (action != null && action.length() != 0) {
			if (action.equalsIgnoreCase("mean"))
				result = 0;
			if (action.equalsIgnoreCase("propose"))
				result = 1;

			if (action.equalsIgnoreCase("support")) {
				if (subject != null && subject.length() != 0) {
					if (subject.equals("we"))
						result = 1;
				}
			}

			if (subject != null && subject.length() != 0) {
				if (subject.toLowerCase().equals("proposal"))
					result = 1;
			}

		}
		
		if(content.toLowerCase().contains("wanna"))
			result = 1;
		
		//
		if(content.toLowerCase().startsWith("for example")||content.toLowerCase().startsWith("example"))
			result = 5;
		
		//drawback
		
		if(content.toLowerCase().contains("be forced to"))
			result = 4;
		
		if(content.toLowerCase().startsWith("but")&& containNEG == 1 && matchIsNotBAD!=1 )
			result = 4;
		
		if(content.toLowerCase().contains("not possible")||content.toLowerCase().contains("not a good idea")
				||content.toLowerCase().contains("unfortunately")||content.toLowerCase().contains("not pretty")||
				content.toLowerCase().contains("again and again")||content.toLowerCase().contains("no need"))
			result = 4;
		
		if(content.toLowerCase().startsWith("care will be taken")&&matchIsNotBAD == 1)
			result = 4;
		
		if(content.toLowerCase().contains("support")&& (containNEG == 1||content.toLowerCase().contains("only"))&&!content.contains("should"))
			result = 4;
		
		if(containNEG == 1&&content.toLowerCase().contains("only"))
			result = 4;
		
		boolean match = content.matches(".*currently[^,.;?\"']*only.*");
		if( match) //containNEG == 1 &&
			result = 4;
		
		if(content.toLowerCase().contains("will only")||content.toLowerCase().contains("can only"))
			result = 4;
		
		if(content.toLowerCase().contains("don't wish")
				||content.toLowerCase().contains("won't like")
				||content.toLowerCase().contains("won't be able")
				||content.toLowerCase().contains("but i cannot") 
				||content.toLowerCase().contains("had an issue")  )
			result = 4;
		
		//if(content.toLowerCase().contains("i want"))
		//	result = 0;



		//bad
		
		//if(content.toLowerCase().contains("although"))
			//result = 4;

		if ( (containlink||content.toLowerCase().contains(" like "))  && (numValidVerbs <2  && numValidWords < 10))
			result = 5; 
		
		
		if(result == 3){
			if(content.toLowerCase().contains("only"))
				result = 0;
			
			if(content.toLowerCase().contains("would be nice"))
				result = 3;
		}
		
		if(content.toLowerCase().contains("it would be nice "))
			result = 1;
		
		return result;
	}

	private static boolean classifyAsExp(Instance item, Instances data) {
		String content = item.stringValue(0);
		double matchVBDGOOD = item.value(data.attribute("matchVBDGOOD"));
		String[] expPattern = new String[] { "have to", "unfortunately", "possible", "suggestion", "maybe", "perhaps" };

		if (FeatureUtility.isContain(content, expPattern) || matchVBDGOOD == 1
				|| content.toLowerCase().startsWith("maybe"))
			return true;

		return false;
	}

	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}

		// iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);

				// if last two chars equal
				if (c1 == c2) {
					// update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}

		return dp[len1][len2];
	}

	public static void main(String[] args) {
		String content = "I have a bit of a wishlist item that I want to tentatively propose";

		String target = "Cannot configure the load balancing count when using Message Groups";
		String object = "The next 9 messages with different JMSXGroupIDs also go to consumer";
		EditDistance ed = new EditDistance();
		double score = minDistance(object, target);

		// System.out.println((FeatureUtility.isContain("control",
		// FeatureUtility.SMART_STOP_WORDS, true)));
		System.out.println("score = " + score);

	}
}