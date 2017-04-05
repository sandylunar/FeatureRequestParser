package main.java.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ListIterator;

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
	public static String[] tagNames = new String[] { "explanation", "want",
			"useless", "benefit", "drawback", "example" };
	static int tagSize = tagNames.length;

	public static void createPrintWriter(String outputFile) {
		try {
			out = new PrintWriter(new FileOutputStream(new File(outputFile),
					true), true);
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

		for (int i = 0; i < tagSize; i++) {
			buffer[i] = new StringBuffer();
		}

		while (list.hasNext()) {
			Instance item = list.next();
			int index = (int) item.classValue();
			String sentence = item.stringValue(0);

			int predict = predictTagIndex(item, data, null, -1);

			matrix[index][predict]++;
			if (index == predict) {
				continue;
			}

			count++;
			String output = String.format("%d - %s - predict to be %s : %s\n",
					count, tagNames[index], tagNames[predict], sentence);
			buffer[index].append(output);
		}

		for (int i = 0; i < tagSize; i++) {
			System.out.println("\nActual tag  = " + tagNames[i]);
			System.out.print(buffer[i]);
		}

		System.out.println(matrix);

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
			System.out.printf("Class=%s precision=%.2f recall=%.2f\n",
					tagNames[i], evaluation[i][0], evaluation[i][1]);
		}

	}

	public static int predictTagIndex(Instance item, Instances data,
									  FeatureRequestOL request, int index) {
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
			sentimentProbability = item.value(data
					.attribute("sentimentProbability"));
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
		String[] wantPatterns = new String[] { "would like", "\'d like",
				"’d like", "would love to", "\'d love to", "’d love to",
				"appreciate", "suggest", "propose", "add support", "pma may",
				"phpmyadmin may", "may wish", "able to", "wish for",
				"there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
		// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately",
				"possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>",
				"<COMMAND>", "<CODE>", "<list>", "<html-link>", "<html-link>",
				"<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP",
				"<file-path>" };

		String[] wantPatterns4 = new String[] { "there should be",
				"would like to", "i’d like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work" };

		String content = originContent.replaceAll("[(].*[)]", "");
		String contentLower = content.toLowerCase();
		// content = originContent.replaceAll("[\"].*[\"]", "");
		// content = originContent.replaceAll("['].*[']", "");

		int result = 0;

		if (numValidWords == 0 || numValidVerbs == 0) {
			result = 2;
		}

		if (isRealFirst == 1 && startWithVB == 1) {
			result = 1;
		}

		if (numValidWords < 2) {
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

		if (FeatureUtility.isContain(content, expPatterns)
				&& numValidWords > 1) {
			result = 0;
		}

		if ((FeatureUtility.isContain(content, wantPatterns))
				&& numValidWords > 1) { // || matchMDGOODVB == 1
			result = 1;
		}

		if (question == 1 && contentLower.contains("reason")) {
			result = 0;
		}

		if (question == 1
				&& FeatureUtility.isContain(content, wantPatterns2)) {
			result = 1;

		}

		if (matchVBDGOOD == 1 || containNEG == 1
				|| contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ")
				|| contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps")
				|| contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe")
				|| contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately")
				|| contentLower.startsWith("similarly")
				|| contentLower.contains("why")
				|| contentLower.contains("for example")) {

			result = 0;
		}

		if (FeatureUtility.checkContains(content, FeatureUtility.USELESS))
			result = 2;



		if (result == 1) {
			if (similarityToTitle <= 0.1) {
				result = 0;
			}
		}

		boolean containwant = FeatureUtility.isContain(contentLower,
				wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower,
				uselessPattern1);

		if (containwant&&!useless) {
			result = 1;
		}

		boolean however = contentLower.startsWith("however")
				&& isRealFirst == 1 && similarityToTitle <= 0.3;
		if (however)
			result = 0;

		if (contentLower.contains("would help") && matchMDGOODIF == 0)
			result = 0;

		String[] expPatterns2 = new String[] { "would like to know why",
				"because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like" };
		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);

		if (exp2 || exp3)
			result = 0;

		boolean questionSugg = contentLower.contains("suggestion")
				&& question == 0;
		if (questionSugg)
			result = 0;

		if (matchMDGOODIF == 1)
			result = 1;

		boolean first = startWithVB == 1 || contentLower.startsWith("goal")
				|| contentLower.contains("we could") || matchMDGOODVB == 1;

		if (isRealFirst == 1 && first)
			result = 1;

		boolean weshould = contentLower.contains("we should");
		if (weshould)
			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

		boolean ithinkgoodif = matchGOODIF == 1
				&& contentLower.contains("i think");
		if (ithinkgoodif)
			result = 1;

		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
			result = 1;

		String[] uselessPatterns2 = new String[] { "look forward",
				"would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower,
				uselessPatterns2);
		if (useless2)
			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower
				.startsWith("an option")))
				&& isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

		boolean exp1 = contentLower.startsWith("to do this")
				|| contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently")
				|| contentLower.contains("something like")
				|| contentLower.contains("just like")
				|| contentLower.contains("benefits:")
				|| contentLower.contains("i ever use")
				|| contentLower.contains("limitation")
				|| contentLower.contains("current")
				|| contentLower.endsWith(":");
		if (exp1) {
			result = 0;
		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

		boolean want1 = (contentLower.contains("add") && contentLower
				.contains("support"))
				|| contentLower.contains("needs to support")
				|| contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for")
				|| contentLower.contains("asked for")
				|| contentLower.contains("basic idea")
				|| contentLower.contains("we need");
		if (want1) {
			result = 1;

		}

		boolean quesReally = contentLower.contains("really") && question == 1;
		if (quesReally)
			result = 0;

		if (isPastTense == 1)
			result = 0;

		boolean isThere = (contentLower.startsWith("is there"))
				&& question == 1;
		if (isThere)
			result = 1;

		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		if (iwant)
			result = 1;

		boolean want3 = contentLower.startsWith("there is a need")
				|| contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this")
				|| contentLower.startsWith("i will")
				|| contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering")
				|| contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning")
				|| contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature")
				|| contentLower.contains("need to be supported");
		if (want3)
			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean act1 = action != null && action.length() != 0
				&& action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0
				&& action.equalsIgnoreCase("propose");
		if (act1)
			result = 0;

		if (act2)
			result = 1;

		boolean wesupport = action != null && action.length() != 0
				&& action.equalsIgnoreCase("support") && subject != null
				&& subject.length() != 0 && subject.equals("we");
		boolean proposal = action != null && action.length() != 0
				&& subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

		if (wesupport)
			result = 1;

		if (proposal)
			result = 1;

		boolean prp = contentLower.contains("you must")
				|| contentLower.contains("it must")
				|| contentLower.contains("that must")
				|| contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");

		if (must && prp)
			result = 0;
		if (must && !prp)
			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately")
				|| contentLower.startsWith("actually");
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
		boolean example1 = contentLower.contains("for example")
				|| contentLower.contains("similar to")
				|| contentLower.startsWith("like")
				|| contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		boolean containlink = content.contains("<link-http>")
				|| content.contains("<http-link>")
				|| content.contains("<html-link>")
				|| content.contains("<EXAMPLE>")
				|| content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP");

		boolean shortS = (containlink || contentLower.contains(" like "))
				&& (numValidVerbs < 2 && numValidWords < 10);
		if (shortS)
			result = 5;

		boolean see = (contentLower.contains("see") || contentLower
				.contains("read")) && containlink && numValidWords < 10;

		if (see)
			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*")
				|| content.equalsIgnoreCase("<CODE>");
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
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower
				.contains("not familiar enough"))
				|| contentLower.contains("without success");
		if (bad)
			result = 4;

		// example
		boolean eg = contentLower.contains("for example")
				|| contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
			result = 3;
		}

		boolean good1 = contentLower.contains("won't have to")
				|| contentLower.contains("no longer need")
				|| contentLower.contains("could reduce")
				|| contentLower.contains("benefit")
				|| content.matches(".*save[^,.;?\"']*time.*")
				|| content.matches(".*save[^,.;?\"']*memory.*")
				|| contentLower.startsWith("having this")
				|| contentLower.contains("could just")
				|| contentLower.contains("can just")
				|| contentLower.contains("a great feature")
				|| contentLower.contains("give a value");

		if (good1)
			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		if (matchHelpSystem)
			result = 3;

		boolean exp = contentLower.startsWith("ideally")
				|| contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") && isRealFirst == 1);

		if (exp)
			result = 0;

		boolean goal = contentLower.contains("goal")
				|| contentLower.contains("wanna");
		if (goal)
			result = 1;

		// example
		boolean eg2 = contentLower.startsWith("for example")
				|| contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

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
				|| contentLower.contains("not possible")
				|| contentLower.contains("not a good idea")
				|| contentLower.contains("unfortunately")
				|| contentLower.contains("not pretty")
				|| contentLower.contains("again and again")
				|| contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support")
				&& (containNEG == 1 || contentLower.contains("only")) && !content
				.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only"))
				|| content.matches(".*currently[^,.;?\"']*only.*")
				|| contentLower.contains("will only")
				|| contentLower.contains("can only")
				|| contentLower.contains("don't wish")
				|| contentLower.contains("won't like")
				|| contentLower.contains("won't be able")
				|| contentLower.contains("but i cannot")
				|| contentLower.contains("had an issue");

		if (drawback)
			result = 4;

		boolean eg3 = (containlink || contentLower.contains(" like "))
				&& (numValidVerbs < 2 && numValidWords < 10);
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

	public static double[] getVariables(Instance item, Instances data, boolean shortTagName) {

		ArrayList<Double> dvalues = new ArrayList<Double>();
		ArrayList<Boolean> bvalues = new ArrayList<Boolean>();

		ArrayList<String> bool = new ArrayList<String>();
		bool.add("true");
		bool.add("false");

		int index = (int) item.classValue();

		//attributeList.add(new Attribute("tag", labelArray));
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
		sentimentProbability = item.value(data
				.attribute("sentimentProbability"));
		numValidVerbs = item.value(data.attribute("numValidVerbs"));
		matchIsGOOD = item.value(data.attribute("matchIsGOOD"));
		matchIsNotGOOD = item.value(data.attribute("matchIsNotGOOD"));
		matchIsBAD = item.value(data.attribute("matchIsBAD"));
		matchIsNotBAD = item.value(data.attribute("matchIsNotBAD"));

		// "want to","can"
		String[] wantPatterns = new String[] { "would like", "\'d like",
				"\'d like", "would love to", "\'d love to", "\'d love to",
				"appreciate", "suggest", "propose", "add support", "pma may",
				"phpmyadmin may", "may wish", "able to", "wish for",
				"there should be", "may want", "we need", "I need",
				"I would want to", "we want" };// "should","require",

		String[] wantPatterns2 = new String[] { "how about", "what about" };

		String[] wantPatterns3 = new String[] { "idea", };// "feature","request","consider",
		// "option",

		String[] expPatterns = new String[] { "have to", "unfortunately",
				"possible", "suggestion", "only" };

		String[] refPatterns = new String[] { "<link-http>", "<issue-link>",
				"<COMMAND>", "<CODE>", "<list>", "<html-link>", "<html-link>",
				"<PATH>", "<FILE>", "<http-link>", "<FILE-SYS>", "<FILE-XML>",
				"<FILE-XML>", "web-page-link", "<http>", "LINK-HTTP",
				"<file-path>" };

		String[] wantPatterns4 = new String[] { "there should be",
				"would like to", "i'd like", "the request is:", };// "should","require",

		String[] uselessPattern1 = new String[] { "would like to work" };

		String content = originContent.replaceAll("[(].*[)]", "");
		String contentLower = content.toLowerCase();

		dvalues.add(numValidWords);
		dvalues.add(numValidVerbs);
		dvalues.add(sentimentScore);
		dvalues.add(sentimentProbability);

		bvalues.add(numValidWords == 0 || numValidVerbs == 0);
		bvalues.add(sentimentScore < 2 && sentimentProbability > 0.6); //embed
		bvalues.add(FeatureUtility.checkContains(content,
				FeatureUtility.USELESS));
		bvalues.add(matchVBDGOOD == 1 || containNEG == 1
				|| contentLower.startsWith("in addition")
				|| contentLower.startsWith("also ")
				|| contentLower.startsWith("so ")
				|| contentLower.startsWith("perhaps")
				|| contentLower.startsWith("by default")
				|| contentLower.startsWith("maybe")
				|| contentLower.startsWith("given")
				|| contentLower.startsWith("unfortunately")
				|| contentLower.startsWith("similarly")
				|| contentLower.contains("why")
				|| contentLower.contains("for example"));
		bvalues.add(question == 1
				&& FeatureUtility.isContain(content, wantPatterns2));
		bvalues.add(question == 1 && contentLower.contains("reason"));
		bvalues.add((FeatureUtility.isContain(content, wantPatterns))
				&& numValidWords > 1);
		bvalues.add(FeatureUtility.isContain(content, expPatterns)
				&& numValidWords > 1);
		bvalues.add(matchMDGOOD == 1 || containNEG == 1);
		bvalues.add(contentLower.contains("should") && question == 0);
		bvalues.add(numValidVerbs == 0 && matchMDGOOD == 0 && containNEG == 0);
		bvalues.add(numValidWords >= 2); //embed
		bvalues.add(sentimentScore < 2 && sentimentProbability > 0.6); //embed
		//bvalues.add(sentimentScore < 2 && sentimentProbability > 0.6); //embed
		bvalues.add(FeatureUtility.noSemicolonBeforePattern(content,
				wantPatterns3));
		bvalues.add(isRealFirst == 1 && startWithVB == 1);
		bvalues.add(similarityToTitle <= 0.1); // not sure
		boolean containwant = FeatureUtility.isContain(contentLower,
				wantPatterns4);
		boolean useless = FeatureUtility.isContain(contentLower,
				uselessPattern1);
		bvalues.add(containwant);
		bvalues.add(!useless);
		boolean however = contentLower.startsWith("however")
				&& isRealFirst == 1 && similarityToTitle <= 0.3;
		bvalues.add(however);
		bvalues.add(contentLower.contains("would help") && matchMDGOODIF == 0);

		String[] expPatterns2 = new String[] { "would like to know why",
				"because", "since", "to do this" };
		String[] expPatterns3 = new String[] { "but", "maybe", "like" };

		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);
		bvalues.add(exp2 || exp3);
		boolean questionSugg = contentLower.contains("suggestion")
				&& question == 0;
		bvalues.add(questionSugg);
		bvalues.add(matchMDGOODIF == 1);
		boolean first = startWithVB == 1 || contentLower.startsWith("goal")
				|| contentLower.contains("we could") || matchMDGOODVB == 1;
		bvalues.add(isRealFirst == 1 && first);
		boolean weshould = contentLower.contains("we should");
		bvalues.add(weshould);
		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("bvalues.add you think we should");
		bvalues.add(questionweshould);
		boolean ithinkgoodif = matchGOODIF == 1
				&& contentLower.contains("i think");
		bvalues.add(ithinkgoodif);
		bvalues.add(FeatureUtility.matchFeature(content) || matchSYSNEED == 1);

		String[] uselessPatterns2 = new String[] { "look forward",
				"would like to work", "willing to contribute",
				"please give your suggestion" };
		boolean useless2 = FeatureUtility.isContain(contentLower,
				uselessPatterns2);
		bvalues.add(useless2);
		boolean what = (contentLower.contains("should") || (contentLower
				.startsWith("an option")))
				&& isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		bvalues.add(what);
		boolean ref = FeatureUtility.isContain(content, refPatterns);
		bvalues.add(ref);
		boolean exp1 = contentLower.startsWith("to do this")
				|| contentLower.startsWith("possibly")
				|| contentLower.startsWith("consequently")
				|| contentLower.contains("something like")
				|| contentLower.contains("just like")
				|| contentLower.contains("benefits:")
				|| contentLower.contains("i ever use")
				|| contentLower.contains("limitation")
				|| contentLower.contains("current")
				|| contentLower.endsWith(":");
		bvalues.add(exp1);
		bvalues.add(matchMDGOODVB == 1 && isRealFirst == 1);
		boolean want1 = (contentLower.contains("add") && contentLower
				.contains("support"))
				|| contentLower.contains("needs to support")
				|| contentLower.startsWith("let's")
				|| contentLower.contains("i'm asking for")
				|| contentLower.contains("asked for")
				|| contentLower.contains("basic idea")
				|| contentLower.contains("we need");
		bvalues.add(want1);
		boolean quesReally = contentLower.contains("really") && question == 1;
		bvalues.add(quesReally);
		bvalues.add(isPastTense == 1);
		boolean isThere = (contentLower.startsWith("is there"))
				&& question == 1;
		bvalues.add(isThere);
		boolean iwant = (contentLower.contains("i want")) && isRealFirst == 1;
		bvalues.add(iwant);
		boolean want3 = contentLower.startsWith("there is a need")
				|| contentLower.startsWith("would it")
				|| contentLower.startsWith("i needed this")
				|| contentLower.startsWith("i will")
				|| contentLower.contains("i'm considering")
				|| contentLower.contains("i am considering")
				|| contentLower.contains("i am planning")
				|| contentLower.contains("i'm planning")
				|| contentLower.contains("why don't we")
				|| contentLower.contains("looking for a feature")
				|| contentLower.contains("need to be supported");
		bvalues.add(want3);
		boolean toodthis = contentLower.contains("to do this");
		bvalues.add(toodthis);
		boolean act1 = action != null && action.length() != 0
				&& action.equalsIgnoreCase("mean");
		boolean act2 = action != null && action.length() != 0
				&& action.equalsIgnoreCase("propose");
		bvalues.add(act1);
		bvalues.add(act2);
		boolean wesupport = action != null && action.length() != 0
				&& action.equalsIgnoreCase("support") && subject != null
				&& subject.length() != 0 && subject.equals("we");
		boolean proposal = action != null && action.length() != 0
				&& subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");
		bvalues.add(wesupport);
		bvalues.add(proposal);
		boolean prp = contentLower.contains("you must")
				|| contentLower.contains("it must")
				|| contentLower.contains("that must")
				|| contentLower.contains("which must")
				|| contentLower.contains("i must");
		boolean must = contentLower.contains("must");
		bvalues.add(must && prp);
		bvalues.add(must && !prp);
		boolean expstart = contentLower.startsWith("unfortunately")
				|| contentLower.startsWith("actually");
		bvalues.add(expstart);
		bvalues.add(FeatureUtility.matchShouldBePossible(content));
		bvalues.add(FeatureUtility.matchNOTONLY(content));

		//TODO
		bvalues.add(FeatureUtility.matchMDAllow(content));
		bvalues.add(matchIsBAD == 1 || matchIsNotGOOD == 1);
		boolean knowhy = contentLower.contains("would like to know why");
		bvalues.add(knowhy);
		// example
		boolean example1 = contentLower.contains("for example")
				|| contentLower.contains("similar to")
				|| contentLower.startsWith("like")
				|| contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		bvalues.add(example1);
		boolean containlink = content.contains("<link-http>")
				|| content.contains("<http-link>")
				|| content.contains("<html-link>")
				|| content.contains("<EXAMPLE>")
				|| content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP");
		boolean shortS = (containlink || contentLower.contains(" like "))
				&& (numValidVerbs < 2 && numValidWords < 10);
		bvalues.add(shortS);
		boolean see = (contentLower.contains("see") || contentLower
				.contains("read")) && containlink && numValidWords < 10;
		bvalues.add(see);
		boolean code = content.matches(".*:[\\s]*<CODE>.*")
				|| content.equalsIgnoreCase("<CODE>");
		bvalues.add(code);
		// explanation
		bvalues.add(contentLower.contains("current"));
		// benefit
		bvalues.add(FeatureUtility.matchMDAllow(content));
		// == 1 ||
		// [20]matchIsGOOD [21]matchIsNotGOOD [22]matchIsBAD [23]matchIsNotBAD
		bvalues.add(matchIsGOOD == 1);// || matchIsNotBAD == 1 &&
		// !contentLower.contains("but");
		// example
		bvalues.add(contentLower.contains("something like"));
		// bad
		bvalues.add(matchIsBAD == 1); // [20]matchIsGOOD [21]matchIsNotGOOD
		// [22]matchIsBAD [23]matchIsNotBAD
		bvalues.add(matchVBDGOOD == 1);
		// bvalues.add(containNEG==1&&containGood);
		//
		// bvalues.add(question==1&&containGood);
		// result=4;
		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower
				.contains("not familiar enough"))
				|| contentLower.contains("without success");
		bvalues.add(bad);
		// example
		boolean eg = contentLower.contains("for example")
				|| contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		bvalues.add(eg);
		// benefit
		// startswith good
		String startWord = content.split(" ")[0];
		bvalues.add(FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT));
		boolean good1 = contentLower.contains("won't have to")
				|| contentLower.contains("no longer need")
				|| contentLower.contains("could reduce")
				|| contentLower.contains("benefit")
				|| content.matches(".*save[^,.;?\"']*time.*")
				|| content.matches(".*save[^,.;?\"']*memory.*")
				|| contentLower.startsWith("having this")
				|| contentLower.contains("could just")
				|| contentLower.contains("can just")
				|| contentLower.contains("a great feature")
				|| contentLower.contains("give a value");
		bvalues.add(good1);
		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
		bvalues.add(matchHelpSystem);
		boolean exp = contentLower.startsWith("ideally")
				|| contentLower.startsWith("note")
				|| (contentLower.startsWith("in order to") && isRealFirst == 1);
		bvalues.add(exp);
		boolean goal = contentLower.contains("goal")
				|| contentLower.contains("wanna");
		bvalues.add(goal);
		// example
		boolean eg2 = contentLower.startsWith("for example")
				|| contentLower.startsWith("example")
				|| contentLower.contains("something like");
		bvalues.add(eg2);
		bvalues.add(act1);
		bvalues.add(act2);
		bvalues.add(wesupport);
		bvalues.add(proposal);
		boolean drawback = contentLower.contains("be forced to")
				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
				|| contentLower.contains("not possible")
				|| contentLower.contains("not a good idea")
				|| contentLower.contains("unfortunately")
				|| contentLower.contains("not pretty")
				|| contentLower.contains("again and again")
				|| contentLower.contains("no need")
				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
				|| (contentLower.contains("support")
				&& (containNEG == 1 || contentLower.contains("only")) && !content
				.contains("should"))
				|| (containNEG == 1 && contentLower.contains("only"))
				|| content.matches(".*currently[^,.;?\"']*only.*")
				|| contentLower.contains("will only")
				|| contentLower.contains("can only")
				|| contentLower.contains("don't wish")
				|| contentLower.contains("won't like")
				|| contentLower.contains("won't be able")
				|| contentLower.contains("but i cannot")
				|| contentLower.contains("had an issue");
		bvalues.add(drawback);
		boolean eg3 = (containlink || contentLower.contains(" like "))
				&& (numValidVerbs < 2 && numValidWords < 10);
		bvalues.add(eg3);
		bvalues.add(contentLower.contains("only"));
		bvalues.add(contentLower.contains("would be nice"));
		boolean wouldbenice = contentLower.contains("it would be nice ")
				|| contentLower.contains("it would be much better");
		bvalues.add(wouldbenice);


		//attributeList.add(new Attribute("tag", labelArray));
		// vals[31] = labelArray.indexOf(fr.getLabel(i));
		//	public static String[] tagNames = new String[] { "explanation", "want",
		//"useless", "benefit", "drawback", "example" };





		//attributeList.add(new Attribute("tag", labelArray));
		// vals[31] = labelArray.indexOf(fr.getLabel(i));

		int size = bvalues.size();

		double[] vals = new double[size+1];
		for( int i = 0 ; i < size; i++){
			vals[i] = bool.indexOf(bvalues.get(i).toString());
		}

		if(shortTagName){
			if(index >2)
				vals[size] = 0;
		}

		else
			vals[size] = index;


		return vals;

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

}