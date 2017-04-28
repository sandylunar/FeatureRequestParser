package main.java.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import main.java.bean.FeatureRequestOL;
import main.java.predictor.TagPredictor;
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
	public static String[] tagNames = new String[] { "explanation", "intent", "trivia", "benefit", "drawback",
			"example" };
	public static double[] tagWeights = {5,1,6,2,3,4};
	
	static int tagSize = tagNames.length;
	public static HashMap<Integer, ArrayList<Integer>> variablesMap = new HashMap<Integer, ArrayList<Integer>>();
	public static ArrayList<String> variableNames = new ArrayList<String>();

	public static double[] numTags; //count size of each tag
	public static double[][] sum;
	public static String classLabel = "tag";

	public static void createPrintWriter(String outputFile) {
		try {
			out = new PrintWriter(new FileOutputStream(new File(outputFile), true), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void predictTag(Instances data, TagPredictor tp) {

		// createPrintWriter("resource//precision_results_non-text.txt");

		Attribute tag = data.attribute(classLabel);
		int count = 0;
		int[][] matrix = new int[tagSize][tagSize];
		ListIterator<Instance> list = data.listIterator();
		StringBuffer buffer[] = new StringBuffer[tagSize];
		ArrayList<String> allNegitives = new ArrayList<String>();

		data.setClassIndex(tag.index());

		System.out.println("Negitive predictions: ");

		for (int i = 0; i < tagSize; i++) {
			buffer[i] = new StringBuffer();
		}

		while (list.hasNext()) {
			Instance item = list.next();
			int index = (int) item.classValue();
			String sentence = item.stringValue(0);

			// TODO
			int predict = tp.predictTagIndex(item, data, null, -1);

			matrix[index][predict]++;
			if (index == predict) {
				continue;
			}

			count++;
			String output = String.format("%d - %s - predict to be %s : %s\n", count, tagNames[index],
					tagNames[predict], sentence);
			buffer[index].append(output);
			allNegitives
					.add(String.format("%s - predict to be %s : %s\n", tagNames[index], tagNames[predict], sentence));
		}

		for (int i = 0; i < tagSize; i++) {
			System.out.println("\nActual tag  = " + tagNames[i]);
			System.out.print(buffer[i]);
		}

		
		String[] allOutputs = allNegitives.toArray(new String[] {});
		Arrays.sort(allOutputs);
		System.out.println("\nSorted output:");
		for (String s : allOutputs) {
			System.out.print(s);
		}

		double[] errorRate = new double [matrix.length];
		double averageFM = 0;
		double errorRateSum = 0;
		double weightSum = 0;
		for(int i = 0; i < tagWeights.length;i++){
			weightSum+=tagWeights[i];
		}
		System.out.println("classification matrix: \n");
		
		for(int i = 0; i < tagNames.length; i++){
			System.out.print(i+1+"\t");
		}
		System.out.print("<-- classified as\n");
		
		for(int i = 0; i < matrix.length; i++){
			double errSum = 0;
			for(int j = 0; j < matrix[i].length; j++){
				errSum += Math.abs(tagWeights[i]-tagWeights[j])*matrix[i][j];
				System.out.print(matrix[i][j]+"\t");
			}
			errorRate[i] = errSum/(errSum+matrix[i][i]);
			errorRateSum+=errorRate[i];
			System.out.println(" |\t"+(i+1)+" = "+tagNames[i]);
		}  
		
		System.out.println();

		double[][] evaluation = new double[tagNames.length][2];
		
		for (int i = 0; i < tagNames.length; i++) {
			double sump = 0;
			double sumr = 0;

			for (int j = 0; j < tagNames.length; j++) {
				sump += matrix[j][i];
				sumr += matrix[i][j];
			}

			evaluation[i][0] = matrix[i][i] / sump;
			evaluation[i][1] = matrix[i][i] / sumr;
			double tmp=((7-tagWeights[i])/weightSum)*2*evaluation[i][0]*evaluation[i][1]/(evaluation[i][1]+evaluation[i][0]);
			averageFM += tmp;
			System.out.printf("Class=%s precision=%.2f recall=%.2f, f-measure=%.2f, errRate = %.2f, weighted f-measure = %.2f\n",tagNames[i], evaluation[i][0], evaluation[i][1], 2*evaluation[i][0]*evaluation[i][1]/(evaluation[i][1]+evaluation[i][0]),errorRate[i],tmp);
		}
		System.out.println("Average misclassification rate = " +(errorRateSum/tagNames.length));
		System.out.println("Average f-measure = " +averageFM);

	}

	// pma+actmq+aspectj+mopidy
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
		double matchIsGOOD;
		double matchIsNotGOOD;
		double matchIsBAD;
		double matchIsNotBAD;

		if (index == -1 && request == null) {

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

		else {
			originContent = request.getSentence(index);
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
		String[] wantPatterns = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", "able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
															// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>", "<COMMAND>", "<CODE>", "<list>",
				"<html-link>", "<html-link>", "<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP", "<file-path>", "<LINK>", "<LIST>" };

		String[] wantPatterns4 = new String[] { "there should be", "would like to", "i’d like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work" };

		String content = originContent.replaceAll("[(].*[)]", "");
		boolean trimed = originContent.length() == content.length() ? false : true;
		String contentLower = content.toLowerCase();
		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if ((numValidWords == 0 || numValidVerbs == 0) && !trimed) {
			result = 2;
		}

		if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (numValidWords < 2 && !trimed) {
			result = 2;
		}

		if (numValidWords >= 2)
			result = 0;

		if (sentimentScore < 2 && sentimentProbability > 0.6)
			result = 0;

		if (contentLower.contains("should") && question == 0)
			result = 1;

		if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		}

		if (FeatureUtility.isContain(content, expPatterns) && numValidWords > 1) {
			result = 0;
		}

		if ((FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1) { // ||
																						// matchMDGOODVB
																						// ==
																						// 1
			result = 1;
		}

		if (question == 1 && contentLower.contains("reason")) {
			result = 0;
		}

		if (question == 1 && FeatureUtility.isContain(content, wantPatterns2)) {
			result = 1;

		}

		if (matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
				|| contentLower.contains("why") || contentLower.contains("for example")
				|| contentLower.startsWith("more detail") || contentLower.startsWith("whereas")) {

			result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		boolean containwant = FeatureUtility.isContain(contentLower, wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower, uselessPattern1);

		if (containwant && !useless) {
			result = 1;
		}

		boolean however = contentLower.startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3;
		if (however)
			result = 0;

		if (contentLower.contains("would help") && matchMDGOODIF == 0)
			result = 0;

		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like", "sometime" };// basically
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		if (exp2 || exp3)
			result = 0;

		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
		if (questionSugg)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		if (isRealFirst == 1 && first)
			result = 1;

		boolean weshould = contentLower.contains("we should");
		if (weshould)
			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
		if (ithinkgoodif)
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
		if (useless2)
			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
				|| contentLower.contains("just like") || contentLower.contains("benefit")// benefits:
				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
				|| contentLower.contains("current") || contentLower.endsWith(":");
		if (exp1) {
			result = 0;
		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
				|| contentLower.contains("basic idea") || contentLower.contains("we need");
		if (want1) {
			result = 1;

		}

		boolean quesReally = contentLower.contains("really") && question == 1;
		if (quesReally)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		boolean isThere = (contentLower.startsWith("is there")) && question == 1;
		if (isThere)
			result = 1;

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		if (iwant)
			result = 1;

		boolean want3 = contentLower.startsWith("there is a need") || contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this") || contentLower.startsWith("i need this")
				|| contentLower.startsWith("i will") || contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering") || contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning") || contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature") || contentLower.contains("need to be supported")
				|| contentLower.matches("either[^,.;?\"']*or.*");
		if (want3)
			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");
		if (act1)
			result = 0;

		if (act2)
			result = 1;

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
				|| contentLower.contains("that must") || contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		if (must && prp)
			result = 0;
		if (must && !prp)
			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
		if (expstart)
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;

		if (FeatureUtility.matchNOTONLY(content))
			result = 1;

		// TODO

		if (result != 0)
			return result;

		if (result != 1 && FeatureUtility.matchMDAllow(content)) {
			result = 3;
		}

		if (matchIsBAD == 1 || matchIsNotGOOD == 1) // [20]matchIsGOOD
													// [21]matchIsNotGOOD
													// [22]matchIsBAD
													// [23]matchIsNotBAD
			result = 4;

		boolean knowhy = contentLower.contains("would like to know why");

		if (knowhy)
			result = 4;

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<LINK>");

		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10);
		if (shortS)
			result = 5;

		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
				&& numValidWords < 10;

		if (see)
			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
		if (code)
			result = 5;

		// explanation
		if (contentLower.contains("current"))
			result = 0;

		// benefit
		if (FeatureUtility.matchMDAllow(content)) { // ||containGood matchMDGOOD
													// == 1 ||
			result = 3;
		}

		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		if (matchIsGOOD == 1) // || matchIsNotBAD == 1 &&
								// !contentLower.contains("but")
			result = 3;

		// example
		if (contentLower.contains("something like"))
			result = 5;

		// bad
		if (matchIsBAD == 1) // [20]matchIsGOOD [21]matchIsNotGOOD
								// [22]matchIsBAD [23]matchIsNotBAD
			result = 4;

		if (matchVBDGOOD == 1)
			result = 4;

		// if(containNEG==1&&containGood)
		// result = 4;
		// if(question==1&&containGood)
		// result=4;

		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
				|| contentLower.contains("without success");
		if (bad)
			result = 4;

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
			result = 3;
		}

		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
				|| contentLower.contains("could reduce") || contentLower.contains("benefit")
				|| content.matches(".*save[^,.;?\"']*time.*") || content.matches(".*save[^,.;?\"']*memory.*")// todo
																												// avoid
																												// extra
				|| contentLower.startsWith("having this") || contentLower.contains("could just")
				|| contentLower.contains("can just") || contentLower.contains("a great feature")
				|| contentLower.contains("give a value");

		if (good1)
			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if (matchHelpSystem)
			result = 3;

		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") || contentLower.matches(".*if.*then.*") && isRealFirst == 1);

		if (exp || contentLower.contains("by default") || contentLower.startsWith("while")
				|| contentLower.startsWith("sometimes"))
			result = 0;

		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna")|| contentLower.contains("aims to")|| contentLower.contains("aim to");
		if (goal)
			result = 1;

		if (act1)
			result = 0;

		if (act2)
			result = 1;

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		// if+isBAD not drawback
		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
				|| contentLower.contains("unfortunately") || contentLower.contains("not pretty")
				|| contentLower.contains("again and again") || contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
						&& !content.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only")) || content.matches(".*currently[^,.;?\"']*only.*")
				// ||(containNEG == 1 && contentLower.contains("current"))
				|| contentLower.contains("will only") || contentLower.contains("can only")
				|| contentLower.contains("don't wish") || contentLower.contains("won't like")
				|| contentLower.contains("won't be able") || contentLower.contains("but i cannot")
				|| contentLower.contains("user cannot") || contentLower.contains("had an issue")
				|| contentLower.contains("drawback") || contentLower.contains("nowhere")
				|| contentLower.contains("at all") || contentLower.contains("a lot of work")
				|| contentLower.contains("more work");// Nowhere a lot of work

		if (drawback)
			result = 4;

		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10);
		if (eg3)
			result = 5;

		if (result == 3) {
			if (contentLower.contains("only"))
				result = 0;

			if (contentLower.contains("would be nice"))
				result = 3;
		}

		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better");

		if (wouldbenice)
			result = 1;

		return result;
	}

	// import hibernate, swt
	public static int predictTagIndex_v3(Instance item, Instances data, FeatureRequestOL request, int index) {
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
		double matchIsGOOD;
		double matchIsNotGOOD;
		double matchIsBAD;
		double matchIsNotBAD;
		double numNNP;

		if (index == -1 && request == null) {

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
			numNNP = item.value(data.attribute("numNNP"));
		}

		else {
			originContent = request.getSentence(index);
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
			numNNP = request.getNumNNP(index);

		}

		// "want to","can"
		String[] wantPatterns = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", " able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
															// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>", "<COMMAND>", "<CODE>", "<list>",
				"<html-link>", "<html-link>", "<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP", "<file-path>", "<LINK>", "<LIST>",
				"<LINK-HTML>" };

		String[] wantPatterns4 = new String[] { "there should be", "would like to", "i’d like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work", "let me know" };

		String content = originContent.replaceAll("[(].*[)]", "");
		boolean trimed = originContent.length() == content.length() ? false : true;
		String contentLower = content.toLowerCase();

		if (content.contains("Isuru Madhushankha"))
			System.out.println();

		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if ((numValidWords == 0 || numValidVerbs == 0) && !trimed) {
			result = 2;
		}

		if (isRealFirst == 1 && startWithVB == 1 && !contentLower.startsWith("see")) {
			result = 1;
		}

		if (numValidWords < 2 && !trimed) {
			result = 2;
		}

		if (numValidWords >= 2)
			result = 0;

		if (numNNP >= 0.5)
			result = 2;

		if (sentimentScore < 2 && sentimentProbability > 0.6)
			result = 0;

		if (contentLower.contains("should") && question == 0)
			result = 1;

		if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		}

		if (FeatureUtility.isContain(content, expPatterns) && numValidWords > 1) {
			result = 0;
		}

		if ((FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1) { // ||
																						// matchMDGOODVB
																						// ==
																						// 1
			result = 1;
		}

		if (question == 1 && contentLower.contains("reason")) {
			result = 0;
		}

		if (question == 1 && FeatureUtility.isContain(content, wantPatterns2)) {
			result = 1;

		}

		if (matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
				|| contentLower.contains("why") || contentLower.contains("for example")
				|| contentLower.startsWith("more detail") || contentLower.startsWith("whereas")) {

			result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		boolean containwant = FeatureUtility.isContain(contentLower, wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower, uselessPattern1);

		if (useless)
			result = 2;

		if (containwant && !useless) {
			result = 1;
		}

		boolean however = contentLower.startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3;
		if (however)
			result = 0;

		if (contentLower.contains("would help") && matchMDGOODIF == 0)
			result = 0;

		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like", "sometime" };// basically
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		if (exp2 || exp3)
			result = 0;

		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
		if (questionSugg)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		if (isRealFirst == 1 && first)
			result = 1;

		boolean weshould = contentLower.contains("we should");
		if (weshould)
			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
		if (ithinkgoodif)
			result = 1;

		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
		if (useless2)
			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
				|| contentLower.contains("just like") || contentLower.contains("benefit")// benefits:
				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
				|| contentLower.contains("current") || contentLower.endsWith(":");
		if (exp1) {
			result = 0;
		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
				|| contentLower.contains("basic idea") || contentLower.contains("we need");
		if (want1) {
			result = 1;

		}

		boolean quesReally = contentLower.contains("really") && question == 1;
		if (quesReally)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		boolean isThere = (contentLower.startsWith("is there")) && question == 1;
		if (isThere)
			result = 1;

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		if (iwant)
			result = 1;

		boolean want3 = contentLower.startsWith("there is a need") || contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this") || contentLower.startsWith("i need this")
				|| contentLower.startsWith("i will") || contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering") || contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning") || contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature") || contentLower.contains("i am looking for")
				|| contentLower.contains("need to be supported") || contentLower.matches("either[^,.;?\"']*or.*");
		if (want3)
			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we")
				&& !contentLower.contains("only support") && !contentLower.contains("support only");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
				|| contentLower.contains("that must") || contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		if (must && prp)
			result = 0;
		if (must && !prp)
			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
		if (expstart)
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;

		if (FeatureUtility.matchNOTONLY(content))
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		boolean want4 = contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we would like ")
				|| contentLower.contains("we may") || contentLower.contains("we should ")
				|| contentLower.contains("should we") || contentLower.contains("we'd need")
				|| contentLower.contains("OGM has to") || contentLower.contains("i want ")
				|| contentLower.contains("would like to");

		if (want4 && containNEG == 0) // subject=OGM
			result = 1;

		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");
		if (act1)
			result = 0;

		if (act2)
			result = 1;

		// TODO

		if (result != 0)
			return result;

		if (content.contains("this is a very useful strategy for at least Datomic"))
			System.out.println();

		if (result != 1 && FeatureUtility.matchMDAllow(content)) {
			result = 3;
		}

		if (matchIsBAD == 1 || matchIsNotGOOD == 1) // [20]matchIsGOOD
													// [21]matchIsNotGOOD
													// [22]matchIsBAD
													// [23]matchIsNotBAD
			result = 4;

		boolean knowhy = contentLower.contains("would like to know why");

		if (knowhy)
			result = 4;

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<LINK>");

		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		if (shortS)
			result = 5;

		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
				&& numValidWords < 10;

		if (see)
			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
		if (code)
			result = 5;

		// explanation
		if (contentLower.contains("current"))
			result = 0;

		// benefit
		if (FeatureUtility.matchMDAllow(content)) { // ||containGood matchMDGOOD
													// == 1 ||
			result = 3;
		}

		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		if (matchIsGOOD == 1 && !contentLower.contains("should be")) // ||
																		// matchIsNotBAD
																		// == 1
																		// &&
			// !contentLower.contains("but")should be
			result = 3;

		// example
		if (contentLower.contains("something like"))
			result = 5;

		// bad
		if (matchIsBAD == 1) // [20]matchIsGOOD [21]matchIsNotGOOD
								// [22]matchIsBAD [23]matchIsNotBAD
			result = 4;

		if (matchVBDGOOD == 1)
			result = 4;

		// if(containNEG==1&&containGood)
		// result = 4;
		// if(question==1&&containGood)
		// result=4;

		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
				|| contentLower.contains("without success") || contentLower.contains("has no functionality");
		if (bad)
			result = 4;

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
			result = 3;
		}

		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
				|| contentLower.contains("could reduce") || content.matches(".*save[^,.;?\"']*time.*")
				|| content.matches(".*save[^,.;?\"']*memory.*") || content.matches(".*avoid[^,.;?\"']*extra.*")// todo
																												// avoid
																												// extra
				|| contentLower.startsWith("having this") || contentLower.contains("could just")
				|| contentLower.contains("can just") || contentLower.contains("a great feature")
				|| contentLower.contains("give a value") || contentLower.contains("would also help");

		if (good1)
			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if (matchHelpSystem)
			result = 3;

		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") || contentLower.matches(".*if.*then.*") && isRealFirst == 1);

		if (exp || contentLower.contains("by default") || contentLower.startsWith("while")
				|| contentLower.startsWith("sometimes"))
			result = 0;

		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna");
		if (goal)
			result = 1;

		if (contentLower.startsWith("let me know"))
			result = 2;

		if (act1)
			result = 0;

		if (act2)
			result = 1;

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		// if+isBAD not drawback
		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
				|| contentLower.contains("there is not a way") || contentLower.contains("unfortunately")
				|| contentLower.contains("not pretty") || contentLower.contains("again and again")
				|| contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
						&& !content.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only"))
				|| contentLower.matches(".*currently[^.;?\"']*only.*")
				// ||(containNEG == 1 && contentLower.contains("current"))
				|| contentLower.contains("will only") || contentLower.contains("can only")
				|| contentLower.contains("don't wish") || contentLower.contains("won't like")
				|| contentLower.contains("won't be able") || contentLower.contains("but i cannot")
				|| contentLower.contains("user cannot") || contentLower.contains("had an issue")
				|| contentLower.contains("may be an issue") || contentLower.contains("drawback")
				|| contentLower.contains("nowhere") || contentLower.contains("at all")
				|| contentLower.contains("a lot of work") || contentLower.contains("more work")
				|| content.matches(".*why[^,.;?\"']*only.*");// Nowhere a lot of
																// work

		if (drawback)
			result = 4;

		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		if (eg3)
			result = 5;

		if (result == 3) {
			if (contentLower.contains("only"))
				result = 0;

			if (contentLower.contains("would be nice"))
				result = 3;
		}

		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better") || contentLower.contains("it would be great")
				|| contentLower.contains("we would like to");

		if (wouldbenice)
			result = 1;

		if (contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we may ")
				|| contentLower.contains("we should ") || contentLower.contains("should we")
				|| contentLower.contains("we'd need") || contentLower.contains("OGM has to")) // subject=OGM

			result = 1;

		if (contentLower.contains("i like to"))
			result = 1;

		if (subject.equalsIgnoreCase("candidate"))
			result = 0;

		return result;
	}

	public static int predictTagIndex_v4(Instance item, Instances data, FeatureRequestOL request, int index) {
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
		double matchIsGOOD;
		double matchIsNotGOOD;
		double matchIsBAD;
		double matchIsNotBAD;
		double numNNP;

		if (index == -1 && request == null) {

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
			numNNP = item.value(data.attribute("numNNP"));
		}

		else {
			originContent = request.getSentence(index);
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
			numNNP = request.getNumNNP(index);

		}

		// "want to","can"
		String[] wantPatterns = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", " able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
															// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>", "<COMMAND>", "<CODE>", "<list>",
				"<html-link>", "<html-link>", "<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP", "<file-path>", "<LINK>", "<LIST>",
				"<LINK-HTML>" };

		String[] wantPatterns4 = new String[] { "there should be", "would like to", "i’d like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work", "let me know" };

		String content = originContent.replaceAll("[(].*[)]", "");
		boolean trimed = originContent.length() == content.length() ? false : true;
		String contentLower = content.toLowerCase();

		if (content.contains("Isuru Madhushankha"))
			System.out.println();

		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if ((numValidWords == 0 || numValidVerbs == 0) && !trimed) {
			result = 2;
		}

		if (isRealFirst == 1 && startWithVB == 1 && !contentLower.startsWith("see")) {
			result = 1;
		}

		if (numValidWords < 2 && !trimed) {
			result = 2;
		}

		if (numValidWords >= 2)
			result = 0;

		if (numNNP >= 0.5)
			result = 2;

		if (sentimentScore < 2 && sentimentProbability > 0.6)
			result = 0;

		if (contentLower.contains("should") && question == 0)
			result = 1;

		if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		}

		if (FeatureUtility.isContain(content, expPatterns) && numValidWords > 1) {
			result = 0;
		}

		if ((FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1) { // ||
																						// matchMDGOODVB
																						// ==
																						// 1
			result = 1;
		}

		if (question == 1 && contentLower.contains("reason")) {
			result = 0;
		}

		if (question == 1 && FeatureUtility.isContain(content, wantPatterns2)) {
			result = 1;

		}

		if (matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
				|| contentLower.contains("why") || contentLower.contains("for example")
				|| contentLower.startsWith("more detail") || contentLower.startsWith("whereas")) {

			result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		boolean containwant = FeatureUtility.isContain(contentLower, wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower, uselessPattern1);

		if (useless)
			result = 2;

		if (containwant && !useless) {
			result = 1;
		}

		boolean however = contentLower.startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3;
		if (however)
			result = 0;

		if (contentLower.contains("would help") && matchMDGOODIF == 0)
			result = 0;

		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like", "sometime" };// basically
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		if (exp2 || exp3)
			result = 0;

		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
		if (questionSugg)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		if (isRealFirst == 1 && first)
			result = 1;

		boolean weshould = contentLower.contains("we should");
		if (weshould)
			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
		if (ithinkgoodif)
			result = 1;

		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
		if (useless2)
			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
				|| contentLower.contains("just like") || contentLower.contains("benefit")// benefits:
				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
				|| contentLower.contains("current") || contentLower.endsWith(":");
		if (exp1) {
			result = 0;
		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
				|| contentLower.contains("basic idea") || contentLower.contains("we need");
		if (want1) {
			result = 1;

		}

		boolean quesReally = contentLower.contains("really") && question == 1;
		if (quesReally)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		boolean isThere = (contentLower.startsWith("is there")) && question == 1;
		if (isThere)
			result = 1;

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		if (iwant)
			result = 1;

		boolean want3 = contentLower.startsWith("there is a need") || contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this") || contentLower.startsWith("i need this")
				|| contentLower.startsWith("i will") || contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering") || contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning") || contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature") || contentLower.contains("i am looking for")
				|| contentLower.contains("need to be supported") || contentLower.matches("either[^,.;?\"']*or.*");
		if (want3)
			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we")
				&& !contentLower.contains("only support") && !contentLower.contains("support only");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
				|| contentLower.contains("that must") || contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		if (must && prp)
			result = 0;
		if (must && !prp)
			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
		if (expstart)
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;

		if (FeatureUtility.matchNOTONLY(content))
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		boolean want4 = contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we would like ")
				|| contentLower.contains("we may") || contentLower.contains("we should ")
				|| contentLower.contains("should we") || contentLower.contains("we'd need")
				|| contentLower.contains("OGM has to") || contentLower.contains("i want ")
				|| contentLower.contains("would like to");

		if (want4 && containNEG == 0) // subject=OGM
			result = 1;

		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");
		if (act1)
			result = 0;

		if (act2)
			result = 1;

		// TODO

		if (result != 0)
			return result;

		if (content.contains("this is a very useful strategy for at least Datomic"))
			System.out.println();

		if (result != 1 && FeatureUtility.matchMDAllow(content)) {
			result = 3;
		}

		if (matchIsBAD == 1 || matchIsNotGOOD == 1) // [20]matchIsGOOD
													// [21]matchIsNotGOOD
													// [22]matchIsBAD
													// [23]matchIsNotBAD
			result = 4;

		boolean knowhy = contentLower.contains("would like to know why");

		if (knowhy)
			result = 4;

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<LINK>");

		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		if (shortS)
			result = 5;

		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
				&& numValidWords < 10;

		if (see)
			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
		if (code)
			result = 5;

		// explanation
		if (contentLower.contains("current"))
			result = 0;

		// benefit
		if (FeatureUtility.matchMDAllow(content)) { // ||containGood matchMDGOOD
													// == 1 ||
			result = 3;
		}

		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		if (matchIsGOOD == 1 && !contentLower.contains("should be")) // ||
																		// matchIsNotBAD
																		// == 1
																		// &&
			// !contentLower.contains("but")should be
			result = 3;

		// example
		if (contentLower.contains("something like"))
			result = 5;

		// bad
		if (matchIsBAD == 1) // [20]matchIsGOOD [21]matchIsNotGOOD
								// [22]matchIsBAD [23]matchIsNotBAD
			result = 4;

		if (matchVBDGOOD == 1)
			result = 4;

		// if(containNEG==1&&containGood)
		// result = 4;
		// if(question==1&&containGood)
		// result=4;

		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
				|| contentLower.contains("without success") || contentLower.contains("has no functionality");
		if (bad)
			result = 4;

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
			result = 3;
		}

		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
				|| contentLower.contains("could reduce") || content.matches(".*save[^,.;?\"']*time.*")
				|| content.matches(".*save[^,.;?\"']*memory.*") || content.matches(".*avoid[^,.;?\"']*extra.*")// todo
																												// avoid
																												// extra
				|| contentLower.startsWith("having this") || contentLower.contains("could just")
				|| contentLower.contains("can just") || contentLower.contains("a great feature")
				|| contentLower.contains("give a value") || contentLower.contains("would also help");

		if (good1)
			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if (matchHelpSystem)
			result = 3;

		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") || contentLower.matches(".*if.*then.*") && isRealFirst == 1);

		if (exp || contentLower.contains("by default") || contentLower.startsWith("while")
				|| contentLower.startsWith("sometimes"))
			result = 0;

		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna");
		if (goal)
			result = 1;

		if (contentLower.startsWith("let me know"))
			result = 2;

		if (act1)
			result = 0;

		if (act2)
			result = 1;

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		// if+isBAD not drawback
		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
				|| contentLower.contains("there is not a way") || contentLower.contains("unfortunately")
				|| contentLower.contains("not pretty") || contentLower.contains("again and again")
				|| contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
						&& !content.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only"))
				|| contentLower.matches(".*currently[^.;?\"']*only.*")
				// ||(containNEG == 1 && contentLower.contains("current"))
				|| contentLower.contains("will only") || contentLower.contains("can only")
				|| contentLower.contains("don't wish") || contentLower.contains("won't like")
				|| contentLower.contains("won't be able") || contentLower.contains("but i cannot")
				|| contentLower.contains("user cannot") || contentLower.contains("had an issue")
				|| contentLower.contains("may be an issue") || contentLower.contains("drawback")
				|| contentLower.contains("nowhere") || contentLower.contains("at all")
				|| contentLower.contains("a lot of work") || contentLower.contains("more work")
				|| content.matches(".*why[^,.;?\"']*only.*");// Nowhere a lot of
																// work

		if (drawback)
			result = 4;

		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		if (eg3)
			result = 5;

		if (result == 3) {
			if (contentLower.contains("only"))
				result = 0;

			if (contentLower.contains("would be nice"))
				result = 3;
		}

		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better") || contentLower.contains("it would be great")
				|| contentLower.contains("we would like to");

		if (wouldbenice)
			result = 1;

		if (contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we may ")
				|| contentLower.contains("we should ") || contentLower.contains("should we")
				|| contentLower.contains("we'd need") || contentLower.contains("OGM has to")) // subject=OGM

			result = 1;

		if (contentLower.contains("i like to"))
			result = 1;

		if (subject.equalsIgnoreCase("candidate"))
			result = 0;

		return result;
	}

	public static int predictTagIndex2(Instance item, Instances data, FeatureRequestOL request, int index) {
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
		double matchIsGOOD;
		double matchIsNotGOOD;
		double matchIsBAD;
		double matchIsNotBAD;

		if (index == -1 && request == null) {

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

		else {
			originContent = request.getSentence(index);
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
		String[] wantPatterns = new String[] { "would like", "\'d like", "’d like", "would love to", "\'d love to",
				"’d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", "able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
															// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>", "<COMMAND>", "<CODE>", "<list>",
				"<html-link>", "<html-link>", "<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP", "<file-path>" };

		String[] wantPatterns4 = new String[] { "there should be", "would like to", "i’d like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work" };

		String content = originContent.replaceAll("[(].*[)]", "");
		String contentLower = content.toLowerCase();
		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if (numValidWords == 0 || numValidVerbs == 0) {
			result = 2;
		}

		if (numValidWords < 2) {
			result = 2;
		}

		if (numValidWords >= 2)
			result = 0;

		if (sentimentScore < 2 && sentimentProbability > 0.6)
			result = 0;

		if (matchMDGOOD == 1 || containNEG == 1) {
			result = 0;
		}

		boolean containwant = FeatureUtility.isContain(contentLower, wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower, uselessPattern1);
		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
				|| contentLower.contains("basic idea") || contentLower.contains("we need");
		if (want1 || (containwant && !useless) || (contentLower.contains("should") && question == 0)
				|| ((FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1)
				|| (question == 1 && FeatureUtility.isContain(content, wantPatterns2))) { // ||
																							// matchMDGOODVB
																							// ==
																							// 1
			result = 1;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;

		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like" };
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		if ((exp2 || exp3 || FeatureUtility.isContain(content, expPatterns) || contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
				|| contentLower.contains("why") || contentLower.contains("for example")) && numValidWords > 1) {
			result = 0;
		}

		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
		if (questionSugg)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		boolean weshould = contentLower.contains("we should");
		if (weshould)
			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
		if (ithinkgoodif)
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
		if (useless2)
			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
				|| contentLower.contains("just like") || contentLower.contains("benefit")
				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
				|| contentLower.contains("current") || contentLower.endsWith(":");
		if (exp1) {
			result = 0;
		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		boolean quesReally = contentLower.contains("really") && question == 1;
		if (quesReally)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		boolean isThere = (contentLower.startsWith("is there")) && question == 1;
		if (isThere)
			result = 1;

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		if (iwant)
			result = 1;

		boolean want3 = contentLower.startsWith("there is a need") || contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this") || contentLower.startsWith("i will")
				|| contentLower.contains("i'm considering") || contentLower.contains("i am considering")
				|| contentLower.contains("i am planning") || contentLower.contains("i'm planning")
				|| contentLower.contains("why don't we") || contentLower.contains("looking for a feature")
				|| contentLower.contains("need to be supported") || contentLower.matches("either[^,.;?\"']*or.*");
		if (want3)
			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");

		if (act2)
			result = 1;

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
				|| contentLower.contains("that must") || contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		if (must && prp)
			result = 0;
		if (must && !prp)
			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
		if (expstart)
			result = 0;

		if (FeatureUtility.matchShouldBePossible(content))
			result = 0;

		if (FeatureUtility.matchNOTONLY(content))
			result = 1;

		// TODO

		if (result != 0)
			return result;

		if (result != 1 && FeatureUtility.matchMDAllow(content)) {
			result = 3;
		}

		if (matchIsBAD == 1 || matchIsNotGOOD == 1) // [20]matchIsGOOD
													// [21]matchIsNotGOOD
													// [22]matchIsBAD
													// [23]matchIsNotBAD
			result = 4;

		boolean knowhy = contentLower.contains("would like to know why");

		if (knowhy)
			result = 4;

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP");

		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10);
		if (shortS)
			result = 5;

		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
				&& numValidWords < 10;

		if (see)
			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
		if (code)
			result = 5;

		// explanation
		if (contentLower.contains("current"))
			result = 0;

		// benefit
		if (FeatureUtility.matchMDAllow(content)) { // ||containGood matchMDGOOD
													// == 1 ||
			result = 3;
		}

		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		if (matchIsGOOD == 1) // || matchIsNotBAD == 1 &&
								// !contentLower.contains("but")
			result = 3;

		// example
		if (contentLower.contains("something like"))
			result = 5;

		// bad
		if (matchIsBAD == 1) // [20]matchIsGOOD [21]matchIsNotGOOD
								// [22]matchIsBAD [23]matchIsNotBAD
			result = 4;

		if (matchVBDGOOD == 1)
			result = 4;

		// if(containNEG==1&&containGood)
		// result = 4;
		// if(question==1&&containGood)
		// result=4;

		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
				|| contentLower.contains("without success");
		if (bad)
			result = 4;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
			result = 3;
		}

		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
				|| contentLower.contains("could reduce") || contentLower.contains("benefit")
				|| content.matches(".*save[^,.;?\"']*time.*") || content.matches(".*save[^,.;?\"']*memory.*")
				|| contentLower.startsWith("having this") || contentLower.contains("could just")
				|| contentLower.contains("can just") || contentLower.contains("a great feature")
				|| contentLower.contains("give a value");

		if (good1)
			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if (matchHelpSystem)
			result = 3;

		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") && isRealFirst == 1);

		if (exp)
			result = 0;

		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna");
		if (goal)
			result = 1;

		if (act1)
			result = 0;

		if (act2)
			result = 1;

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
				|| contentLower.contains("unfortunately") || contentLower.contains("not pretty")
				|| contentLower.contains("again and again") || contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
						&& !content.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only")) || content.matches(".*currently[^,.;?\"']*only.*")
				|| contentLower.contains("will only") || contentLower.contains("can only")
				|| contentLower.contains("don't wish") || contentLower.contains("won't like")
				|| contentLower.contains("won't be able") || contentLower.contains("but i cannot")
				|| contentLower.contains("had an issue") || contentLower.contains("drawback")
				|| (contentLower.startsWith("the problem is"));// todo The
																// problem is

		if (drawback)
			result = 4;

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;
		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		if (eg3)
			result = 5;

		if (result == 3) {
			if (contentLower.contains("only"))
				result = 0;

			if (contentLower.contains("would be nice"))
				result = 3;
		}

		if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better");

		if (wouldbenice || (isRealFirst == 1 && first))
			result = 1;

		return result;
	}

	public static double[] getVariables(Instance item, Instances data, boolean count) {

		ArrayList<Double> dvalues = new ArrayList<Double>();
		ArrayList<Boolean> bvalues = new ArrayList<Boolean>();
		variableNames = new ArrayList<String>();
		variablesMap = new HashMap<Integer, ArrayList<Integer>>();

		ArrayList<String> bool = new ArrayList<String>();
		bool.add("false");
		bool.add("true");

		int index = (int) item.classValue();

		// attributeList.add(new Attribute("tag", labelArray));
		// vals[31] = labelArray.indexOf(fr.getLabel(i));

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
		double matchIsGOOD;
		double matchIsNotGOOD;
		double matchIsBAD;
		double matchIsNotBAD;
		double numNNP;

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
		numNNP = item.value(data.attribute("numNNP"));

		// "want to","can"
		String[] wantPatterns = new String[] { "would like", "\'d like", "\'d like", "would love to", "\'d love to",
				"\'d love to", "appreciate", "suggest", "propose", "add support", "pma may", "phpmyadmin may",
				"may wish", "able to", "wish for", "there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
															// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately", "possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>", "<COMMAND>", "<CODE>", "<list>",
				"<html-link>", "<html-link>", "<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP", "<file-path>" };

		String[] wantPatterns4 = new String[] { "there should be", "would like to", "i'd like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work" };

		String content = originContent.replaceAll("[(].*[)]", "");
		String contentLower = content.toLowerCase();
		boolean trimed = originContent.length() == content.length() ? false : true;
		int result = 0;

		bvalues.add((numValidWords == 0 || numValidVerbs == 0) && !trimed);
		variableNames.add("(numValidWords == 0 || numValidVerbs == 0)&&!trimed");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(isRealFirst == 1 && startWithVB == 1 && !contentLower.startsWith("see"));
		variableNames.add("isRealFirst == 1 && startWithVB == 1 && !contentLower.startsWith(\"see\")");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(numValidWords < 2 && !trimed);
		variableNames.add("numValidWords < 2 &&!trimed ");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(numValidWords >= 2);
		variableNames.add("numValidWords >= 2");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(numNNP >= 0.5);
		variableNames.add("numNNP >= 0.5");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(sentimentScore < 2 && sentimentProbability > 0.6);
		variableNames.add("sentimentScore < 2 && sentimentProbability > 0.6");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.contains("should") && question == 0);
		variableNames.add("contentLower.contains(\"should\") && question == 0");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchMDGOOD == 1 || containNEG == 1);
		variableNames.add("matchMDGOOD == 1 || containNEG == 1");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(FeatureUtility.isContain(content, expPatterns) && numValidWords > 1);
		variableNames.add("FeatureUtility.isContain(content, expPatterns) && numValidWords > 1");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add((FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1);
		variableNames.add("FeatureUtility.isContain(content, wantPatterns)) && numValidWords > 1");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(question == 1 && contentLower.contains("reason"));
		variableNames.add("question == 1 && contentLower.contains(\"reason\")");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(question == 1 && FeatureUtility.isContain(content, wantPatterns2));
		variableNames.add("question == 1 && FeatureUtility.isContain(content, wantPatterns2)");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
				|| contentLower.contains("why") || contentLower.contains("for example")
				|| contentLower.startsWith("more detail") || contentLower.startsWith("whereas"));
		variableNames.add("matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith");

		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(FeatureUtility.checkContains(content, FeatureUtility.USELESS));
		variableNames.add("FeatureUtility.checkContains(content, FeatureUtility.USELESS)");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(similarityToTitle <= 0.1);
		variableNames.add("similarityToTitle <= 0.1");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean containwant = FeatureUtility.isContain(contentLower, wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower, uselessPattern1);

		bvalues.add(useless);
		variableNames.add("useless");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(containwant && !useless);
		variableNames.add("containwant && !useless");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean however = contentLower.startsWith("however") && isRealFirst == 1 && similarityToTitle <= 0.3;
		bvalues.add(however);
		variableNames.add("however");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.contains("would help") && matchMDGOODIF == 0);
		variableNames.add("contentLower.contains(\"would help\") && matchMDGOODIF == 0");
		result = 0;
		updateVariablesMap(result, bvalues);

		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like", "sometime" };// basically
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		bvalues.add(exp2 || exp3);
		variableNames.add("exp2 || exp3");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
		bvalues.add(questionSugg);
		variableNames.add("questionSugg");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchMDGOODIF == 1);
		variableNames.add("matchMDGOODIF == 1");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		bvalues.add(isRealFirst == 1 && first);
		variableNames.add("isRealFirst == 1 && first");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean weshould = contentLower.contains("we should");
		bvalues.add(weshould);
		variableNames.add("weshould");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		bvalues.add(questionweshould);
		variableNames.add("questionweshould");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
		bvalues.add(ithinkgoodif);
		variableNames.add("ithinkgoodif");
		result = 1;
		updateVariablesMap(result, bvalues);

		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
		bvalues.add(useless2);
		variableNames.add("useless2");
		result = 2;
		updateVariablesMap(result, bvalues);

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		bvalues.add(what);
		variableNames.add("what");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		bvalues.add(ref);
		variableNames.add("ref");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
				|| contentLower.contains("just like") || contentLower.contains("benefit")// benefits:
				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
				|| contentLower.contains("current") || contentLower.endsWith(":");
		bvalues.add(exp1);
		variableNames.add("exp1");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchMDGOODVB == 1 && isRealFirst == 1);
		variableNames.add("matchMDGOODVB == 1 && isRealFirst == 1");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
				|| contentLower.contains("basic idea") || contentLower.contains("we need");
		bvalues.add(want1);
		variableNames.add("want1");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean quesReally = contentLower.contains("really") && question == 1;
		bvalues.add(quesReally);
		variableNames.add("quesReally");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(isPastTense == 1);
		variableNames.add("isPastTense == 1");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean isThere = (contentLower.startsWith("is there")) && question == 1;
		bvalues.add(isThere);
		variableNames.add("isThere");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		bvalues.add(iwant);
		variableNames.add("iwant");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean want3 = contentLower.startsWith("there is a need") || contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this") || contentLower.startsWith("i need this")
				|| contentLower.startsWith("i will") || contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering") || contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning") || contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature") || contentLower.contains("i am looking for")
				|| contentLower.contains("need to be supported") || contentLower.matches("either[^,.;?\"']*or.*");
		bvalues.add(want3);
		variableNames.add("want3");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean toodthis = contentLower.contains("to do this");
		bvalues.add(toodthis);
		variableNames.add("toodthis");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we")
				&& !contentLower.contains("only support") && !contentLower.contains("support only");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		bvalues.add(wesupport);
		variableNames.add("wesupport");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(proposal);
		variableNames.add("proposal");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
				|| contentLower.contains("that must") || contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		bvalues.add(must && prp);
		variableNames.add("must && prp");
		result = 0;
		updateVariablesMap(result, bvalues);
		bvalues.add(must && !prp);
		variableNames.add("must && !prp");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
		bvalues.add(expstart);
		variableNames.add("expstart");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(FeatureUtility.matchShouldBePossible(content));
		variableNames.add("FeatureUtility.matchShouldBePossible(content)");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(FeatureUtility.matchNOTONLY(content));
		variableNames.add("FeatureUtility.matchNOTONLY(content)");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(FeatureUtility.matchFeature(content) || matchSYSNEED == 1);
		variableNames.add("FeatureUtility.matchFeature(content) || matchSYSNEED == 1");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean want4 = contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we would like ")
				|| contentLower.contains("we may") || contentLower.contains("we should ")
				|| contentLower.contains("should we") || contentLower.contains("we'd need")
				|| contentLower.contains("OGM has to") || contentLower.contains("i want ")
				|| contentLower.contains("would like to");

		bvalues.add(want4 && containNEG == 0);
		variableNames.add("want4 && containNEG == 0");
		result = 1;
		updateVariablesMap(result, bvalues);

		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");
		bvalues.add(act1);
		variableNames.add("act1");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(act2);
		variableNames.add("act2");
		result = 1;
		updateVariablesMap(result, bvalues);

		// TODO


		bvalues.add(FeatureUtility.matchMDAllow(content));
		variableNames.add(" FeatureUtility.matchMDAllow(content)");
		result = 3;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchIsBAD == 1 || matchIsNotGOOD == 1);
		variableNames.add("matchIsBAD == 1 || matchIsNotGOOD == 1");
		result = 4;
		updateVariablesMap(result, bvalues);

		boolean knowhy = contentLower.contains("would like to know why");

		bvalues.add(knowhy);
		variableNames.add("knowhy");
		result = 4;
		updateVariablesMap(result, bvalues);

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		bvalues.add(example1);
		variableNames.add("example1");
		result = 5;
		updateVariablesMap(result, bvalues);

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<LINK>");

		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		bvalues.add(shortS);
		variableNames.add("shortS");
		result = 5;
		updateVariablesMap(result, bvalues);

		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
				&& numValidWords < 10;

		bvalues.add(see);
		variableNames.add("see");
		result = 5;
		updateVariablesMap(result, bvalues);

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
		bvalues.add(code);
		variableNames.add("code");
		result = 5;
		updateVariablesMap(result, bvalues);

		// explanation
		bvalues.add(contentLower.contains("current"));
		variableNames.add("contentLower.contains(\"current\")");
		result = 0;
		updateVariablesMap(result, bvalues);

		// benefit
		bvalues.add(FeatureUtility.matchMDAllow(content));
		variableNames.add("FeatureUtility.matchMDAllow(content)");
		
		// == 1 ||
		result = 3;
		updateVariablesMap(result, bvalues);

		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		bvalues.add(matchIsGOOD == 1 && !contentLower.contains("should be"));
		variableNames.add("matchIsGOOD == 1 && !contentLower.contains(\"should be\")");
		result = 3;
		updateVariablesMap(result, bvalues);

		// example
		bvalues.add(contentLower.contains("something like"));
		variableNames.add("contentLower.contains(\"something like\")");
		result = 5;
		updateVariablesMap(result, bvalues);

		// bad
		bvalues.add(matchIsBAD == 1);
		variableNames.add("matchIsBAD == 1");
		result = 4;
		updateVariablesMap(result, bvalues);

		bvalues.add(matchVBDGOOD == 1);
		variableNames.add("matchVBDGOOD == 1");
		result = 4;
		updateVariablesMap(result, bvalues);


		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
				|| contentLower.contains("without success") || contentLower.contains("has no functionality");
		bvalues.add(bad);
		variableNames.add("bad");
		result = 4;
		updateVariablesMap(result, bvalues);

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		bvalues.add(eg);
		variableNames.add("eg");
		result = 5;
		updateVariablesMap(result, bvalues);

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		bvalues.add(FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT));
		variableNames.add("FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)");
		result = 3;
		updateVariablesMap(result, bvalues);

		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
				|| contentLower.contains("could reduce") || content.matches(".*save[^,.;?\"']*time.*")
				|| content.matches(".*save[^,.;?\"']*memory.*") || content.matches(".*avoid[^,.;?\"']*extra.*")// todo
																												// avoid
																												// extra
				|| contentLower.startsWith("having this") || contentLower.contains("could just")
				|| contentLower.contains("can just") || contentLower.contains("a great feature")
				|| contentLower.contains("give a value") || contentLower.contains("would also help");

		bvalues.add(good1);
		variableNames.add("good1");
		result = 3;
		updateVariablesMap(result, bvalues);

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		bvalues.add(matchHelpSystem);
		variableNames.add("matchHelpSystem");
		result = 3;
		updateVariablesMap(result, bvalues);

		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		bvalues.add(eg2);
		variableNames.add("eg2");
		result = 5;
		updateVariablesMap(result, bvalues);

		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") || contentLower.matches(".*if.*then.*") && isRealFirst == 1);

		bvalues.add(exp || contentLower.contains("by default") || contentLower.startsWith("while")
				|| contentLower.startsWith("sometimes"));
		variableNames.add(
				"exp ||contentLower.contains(\"by default\")||contentLower.startsWith(\"while\")||contentLower.startsWith(\"sometimes\")");
		result = 0;
		updateVariablesMap(result, bvalues);

		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna");
		bvalues.add(goal);
		variableNames.add("goal");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.startsWith("let me know"));
		variableNames.add("contentLower.startsWith(\"let me know\")");
		result = 2;
		updateVariablesMap(result, bvalues);

		bvalues.add(act1);
		variableNames.add("act1");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(act2);
		variableNames.add("act2");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(wesupport);
		variableNames.add("wesupport");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(proposal);
		variableNames.add("proposal");
		result = 1;
		updateVariablesMap(result, bvalues);

		// if+isBAD not drawback
		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
				|| contentLower.contains("there is not a way") || contentLower.contains("unfortunately")
				|| contentLower.contains("not pretty") || contentLower.contains("again and again")
				|| contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
						&& !content.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only"))
				|| contentLower.matches(".*currently[^.;?\"']*only.*") || contentLower.contains("will only")
				|| contentLower.contains("can only") || contentLower.contains("don't wish")
				|| contentLower.contains("won't like") || contentLower.contains("won't be able")
				|| contentLower.contains("but i cannot") || contentLower.contains("user cannot")
				|| contentLower.contains("had an issue") || contentLower.contains("may be an issue")
				|| contentLower.contains("drawback") || contentLower.contains("nowhere")
				|| contentLower.contains("at all") || contentLower.contains("a lot of work")
				|| contentLower.contains("more work") || content.matches(".*why[^,.;?\"']*only.*");

		bvalues.add(drawback);
		variableNames.add("drawback");
		result = 4;
		updateVariablesMap(result, bvalues);

		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
		bvalues.add(eg3);
		variableNames.add("eg3");
		result = 5;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.contains("only"));
		variableNames.add("contentLower.contains(\"only\")");
		result = 0;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.contains("would be nice"));
		variableNames.add("contentLower.contains(\"would be nice\")");
		result = 3;
		updateVariablesMap(result, bvalues);

		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better") || contentLower.contains("it would be great")
				|| contentLower.contains("we would like to");

		bvalues.add(wouldbenice);
		variableNames.add("wouldbenice");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we may ")
				|| contentLower.contains("we should ") || contentLower.contains("should we")
				|| contentLower.contains("we'd need") || contentLower.contains("OGM has to"));

		variableNames.add("please, we could");
		result = 1;
		updateVariablesMap(result, bvalues);

		bvalues.add(contentLower.contains("i like to"));
		variableNames.add("contentLower.contains(\"i like to\")");
		result = 1;
		updateVariablesMap(result, bvalues);
		

		bvalues.add(subject.equalsIgnoreCase("candidate"));
		variableNames.add("subject.equalsIgnoreCase(\"candidate\")");
		result = 0;
		updateVariablesMap(result, bvalues);

		// attributeList.add(new Attribute("tag", labelArray));
		// vals[31] = labelArray.indexOf(fr.getLabel(i));
		// public static String[] tagNames = new String[] { "explanation",
		// "want",
		// "useless", "benefit", "drawback", "example" };

		// attributeList.add(new Attribute("tag", labelArray));
		// vals[31] = labelArray.indexOf(fr.getLabel(i));

		int size = bvalues.size();

		double[] vals = new double[size + 1];
		for (int i = 0; i < size; i++) {
			if (bvalues.get(i))
				vals[i] = 1;
			else
				vals[i] = 0;
		}

		vals[size] = index;

		if (count) {

			Set<Integer> keyset = variablesMap.keySet();

			Iterator<Integer> it = keyset.iterator();

			while (it.hasNext()) {
				Integer key = it.next();

				ArrayList<Integer> list = variablesMap.get(key);
				//System.out.println("For key = " + key);
				for (Integer i : list) {
					//System.out.print(i + ",");
				}

				//System.out.println();
			}

			//System.out.println("Size of bvalues = " + bvalues.size());
			//System.out.println("Size of variableNames = " + variableNames.size());

			for (int i = 0; i < variableNames.size(); i++) {
			//	System.out.println(i + " - " + variableNames.get(i));
			}

		}

		return vals;

	}

	private static void updateVariablesMap(int result, ArrayList<Boolean> bvalues) {
		if (variablesMap.containsKey(new Integer(result))) {
			ArrayList<Integer> list = variablesMap.get(result);
			list.add(bvalues.size() - 1);
			variablesMap.remove(result);
			variablesMap.put(result, list);
		} else {
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(bvalues.size() - 1);
			variablesMap.put(result, list);
		}

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
		// String content =
		// "I have a bit of a wishlist item that I want to tentatively propose";
		String target = "Cannot configure the load balancing count when using Message Groups";
		String object = "The next 9 messages with different JMSXGroupIDs also go to consumer";
		// EditDistance ed = new EditDistance();
		double score = minDistance(object, target);

		// System.out.println((FeatureUtility.isContain("control",
		// FeatureUtility.SMART_STOP_WORDS, true)));
		System.out.println("score = " + score);

	}

	public static void showItemsByTag(String outputDir, String targetFileName)
			throws FileNotFoundException, UnsupportedEncodingException {

		BufferedReader reader = null;
		try {

			InputStreamReader fReader = new InputStreamReader(new FileInputStream(targetFileName), "UTF-8");
			reader = new BufferedReader(fReader);

			String tempString = null;
			int line = 1;
			StringBuffer[] buffers = new StringBuffer[tagNames.length];
			ArrayList<String> tagNamesList = new ArrayList<String>();
			for (int i = 0; i < tagNames.length; i++) {
				buffers[i] = new StringBuffer();
				tagNamesList.add(tagNames[i]);
			}

			while ((tempString = reader.readLine()) != null) {

				String[] result = FeatureUtility.splitByEqual(tempString, line, true, false);

				if (result == null) {
					continue;
				}
				String tag = result[0];
				int index = tagNamesList.indexOf(tag);
				if (index < 0)
					continue;
				String sentence = result[1];
				buffers[index].append(sentence + "\n");

				line++;
			}

			for (int i = 0; i < tagNames.length; i++) {

				System.out.println("\n" + tagNames[i]);
				System.out.println("============================");
				System.out.println(buffers[i].toString());
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// TODO
	public static double[][] calculateSuppConf(Instances data) {

		int numAttributes = data.numAttributes();
		double[][] result = new double[numAttributes - 1][2];
		numTags = new double[tagNames.length];
		sum = new double[numAttributes - 1][2];

		Iterator<Instance> it = data.iterator();
		while (it.hasNext()) {
			Instance item = it.next();
			int classIndex = (int) item.value(numAttributes - 1);

			for (int i = 0; i < numAttributes - 1; i++) {
				double value = item.value(i);
				double predictIndex = getVariableIndex(i);
				if (value == 1)
					sum[i][1]++;
				if (value == 1 && classIndex == predictIndex)
					sum[i][0]++;
			}

			numTags[classIndex]++;
		}

		for (int i = 0; i < numAttributes - 1; i++) {
			result[i][0] = sum[i][0] / sum[i][1];
			result[i][1] = sum[i][0] / numTags[(int) getVariableIndex(i)];
		}

		for (int i = 0; i < numTags.length; i++) {
			System.out.println(tagNames[i] + " : " + numTags[i]);
		}

		return result;

	}

	public static double getVariableIndex(int index) {
		Set<Integer> set = variablesMap.keySet();
		for (Integer i : set) {
			ArrayList<Integer> list = variablesMap.get(i);
			if (list.contains(index))
				return i;
		}
		System.out.println("Error - cannot find index = " + index);
		return -1;
	}
}