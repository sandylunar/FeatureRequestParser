package main.java.predictor;

import main.java.bean.FeatureRequestOL;
import main.java.util.FeatureUtility;
import weka.core.Instance;
import weka.core.Instances;

public class FuzzyPredictorPMAMopidy implements TagPredictor{
	
	public int predictTagIndex(Instance item, Instances data, FeatureRequestOL request, int index) {
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

//		if (numNNP >= 0.5)
//			result = 2;
//
//		if (sentimentScore < 2 && sentimentProbability > 0.6)
//			result = 0;

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

//		if (question == 1 && FeatureUtility.isContain(content, wantPatterns2)) {
//			result = 1;
//
//		}

//		if (matchVBDGOOD == 1 || containNEG == 1 || contentLower.startsWith("in addition")
//				|| contentLower.startsWith("also ") || contentLower.startsWith("so ")
//				|| contentLower.startsWith("perhaps") || contentLower.startsWith("by default")
//				|| contentLower.startsWith("maybe") || contentLower.startsWith("given")
//				|| contentLower.startsWith("unfortunately") || contentLower.startsWith("similarly")
//				|| contentLower.contains("why") || contentLower.contains("for example")
//				|| contentLower.startsWith("more detail") || contentLower.startsWith("whereas")) {
//
//			result = 0;
//		}

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
//		if (however)
//			result = 0;

//		if (contentLower.contains("would help") && matchMDGOODIF == 0)
//			result = 0;

//		String[] expPatterns2 = new String[] { "would like to know why", "because", "since", "to do this" };
//		String[] expPatterns3 = new String[] { "but", "maybe", "like", "sometime" };// basically
//		boolean exp2 = FeatureUtility.isContain(contentLower, expPatterns2);
//		boolean exp3 = FeatureUtility.doesStartWith(contentLower, expPatterns3);
//
//		if (exp2 || exp3)
//			result = 0;

//		boolean questionSugg = contentLower.contains("suggestion") && question == 0;
//		if (questionSugg)
//			result = 0;

//		if (matchMDGOODIF == 1)
//			result = 1;

		boolean first = startWithVB == 1 || contentLower.startsWith("goal") || contentLower.contains("we could")
				|| matchMDGOODVB == 1;

		if (isRealFirst == 1 && first)
			result = 1;

//		boolean weshould = contentLower.contains("we should");
//		if (weshould)
//			result = 1;

		boolean questionweshould = contentLower.contains("whether we should")
				|| contentLower.contains("if you think we should");
		if (questionweshould)
			result = 0;

//		boolean ithinkgoodif = matchGOODIF == 1 && contentLower.contains("i think");
//		if (ithinkgoodif)
//			result = 1;

//		String[] uselessPatterns2 = new String[] { "look forward", "would like to work", "willing to contribute",
//				"please give your suggestion" };
//		boolean useless2 = FeatureUtility.isContain(contentLower, uselessPatterns2);
//		if (useless2)
//			result = 2;

		boolean what = (contentLower.contains("should") || (contentLower.startsWith("an option"))) && isRealFirst == 1
				&& !FeatureUtility.isContain(content, expPatterns);
		if (what)
			result = 1;

		boolean ref = FeatureUtility.isContain(content, refPatterns);

		if (ref)
			result = 0;

//		boolean exp1 = contentLower.startsWith("to do this") || contentLower.startsWith("possibly")
//				|| contentLower.startsWith("consequently") || contentLower.contains("something like")
//				|| contentLower.contains("just like") || contentLower.contains("benefit")// benefits:
//				|| contentLower.contains("i ever use") || contentLower.contains("limitation")
//				|| contentLower.contains("current") || contentLower.endsWith(":");
//		if (exp1) {
//			result = 0;
//		}

		if (matchMDGOODVB == 1 && isRealFirst == 1) {
			result = 1;
		}

//		boolean want1 = (contentLower.contains("add") && contentLower.contains("support"))
//				|| contentLower.contains("needs to support") || contentLower.startsWith("let's")
//				|| contentLower.contains("i'm asking for") || contentLower.contains("asked for")
//				|| contentLower.contains("basic idea") || contentLower.contains("we need");
//		if (want1) {
//			result = 1;
//
//		}

		boolean quesReally = contentLower.contains("really") && question == 1;
//		if (quesReally)
//			result = 0;

//		if (isPastTense == 1)
//			result = 0;

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
//		if (want3)
//			result = 1;

		boolean toodthis = contentLower.contains("to do this");
		if (toodthis)
			result = 0;

		boolean wesupport = action != null && action.length() != 0 && action.equalsIgnoreCase("support")
				&& subject != null && subject.length() != 0 && subject.equals("we")
				&& !contentLower.contains("only support") && !contentLower.contains("support only");
		boolean proposal = action != null && action.length() != 0 && subject != null && subject.length() != 0
				&& subject.toLowerCase().equals("proposal");

//		if (wesupport)
//			result = 1;

//		if (proposal)
//			result = 1;

//		boolean prp = contentLower.contains("you must") || contentLower.contains("it must")
//				|| contentLower.contains("that must") || contentLower.contains("which must")
//				|| contentLower.contains("i must");
//		boolean must = contentLower.contains("must");
//
//		if (must && prp)
//			result = 0;
//		if (must && !prp)
//			result = 1;

		boolean expstart = contentLower.startsWith("unfortunately") || contentLower.startsWith("actually");
//		if (expstart)
//			result = 0;

//		if (FeatureUtility.matchShouldBePossible(content))
//			result = 0;

//		if (FeatureUtility.matchNOTONLY(content))
//			result = 1;

//		if (FeatureUtility.matchFeature(content) || matchSYSNEED == 1)
//			result = 1;

		boolean want4 = contentLower.startsWith("please") || contentLower.startsWith("the idea is")
				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
				|| contentLower.contains("we can ") || contentLower.contains("we would like ")
				|| contentLower.contains("we may") || contentLower.contains("we should ")
				|| contentLower.contains("should we") || contentLower.contains("we'd need")
				|| contentLower.contains("OGM has to") || contentLower.contains("i want ")
				|| contentLower.contains("would like to");

//		if (want4 && containNEG == 0) 
//			result = 1;

//		boolean act1 = action != null && action.length() != 0 && action.equalsIgnoreCase("mean");
//		boolean act2 = action != null && action.length() != 0 && action.equalsIgnoreCase("propose");
//		if (act1)
//			result = 0;
//
//		if (act2)
//			result = 1;

		// TODO

		if (result != 0)
			return result;

		if (content.contains("this is a very useful strategy for at least Datomic"))
			System.out.println();

		if (result != 1 && FeatureUtility.matchMDAllow(content)) {
			result = 3;
		}

		if (matchIsBAD == 1 || matchIsNotGOOD == 1) 
			result = 4;

//		boolean knowhy = contentLower.contains("would like to know why");
//
//		if (knowhy)
//			result = 4;

		// example
		boolean example1 = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like") || contentLower.startsWith("other")
				|| contentLower.endsWith("following:");
		if (example1)
			result = 5;

		boolean containlink = content.contains("<link-http>") || content.contains("<http-link>")
				|| content.contains("<html-link>") || content.contains("<EXAMPLE>") || content.contains("web-page-link")
				|| content.contains("<http>") || content.contains("LINK-HTTP") || content.contains("<LINK>");

//		boolean shortS = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
//				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
//		if (shortS)
//			result = 5;
//
//		boolean see = (contentLower.contains("see") || contentLower.contains("read")) && containlink
//				&& numValidWords < 10;
//
//		if (see)
//			result = 5;

		boolean code = content.matches(".*:[\\s]*<CODE>.*") || content.equalsIgnoreCase("<CODE>");
//		if (code)
//			result = 5;

		// explanation
		if (contentLower.contains("current"))
			result = 0;

		// benefit
		if (FeatureUtility.matchMDAllow(content)) { 
			result = 3;
		}

		
		if (matchIsGOOD == 1 && !contentLower.contains("should be")) 
			result = 3;

		// example
		if (contentLower.contains("something like"))
			result = 5;

		// bad
//		if (matchIsBAD == 1) 
//			result = 4;

		if (matchVBDGOOD == 1)
			result = 4;

		

//		boolean bad = content.matches(".*cause[^,.;?\"']*problem.*")
//				|| (content.matches(".*not[^,.;?\"']*enough.*") && !contentLower.contains("not familiar enough"))
//				|| contentLower.contains("without success") || contentLower.contains("has no functionality");
//		if (bad)
//			result = 4;

		// example
		boolean eg = contentLower.contains("for example") || contentLower.contains("similar to")
				|| contentLower.startsWith("like");
		if (eg)
			result = 5;

		// benefit
		// startswith good

		String startWord = content.split(" ")[0];
//		if (FeatureUtility.isContain(startWord, FeatureUtility.GOOD_BENEFIT)) {
//			result = 3;
//		}

//		boolean good1 = contentLower.contains("won't have to") || contentLower.contains("no longer need")
//				|| contentLower.contains("could reduce") || content.matches(".*save[^,.;?\"']*time.*")
//				|| content.matches(".*save[^,.;?\"']*memory.*") || content.matches(".*avoid[^,.;?\"']*extra.*")
//																												
//				|| contentLower.startsWith("having this") || contentLower.contains("could just")
//				|| contentLower.contains("can just") || contentLower.contains("a great feature")
//				|| contentLower.contains("give a value") || contentLower.contains("would also help");
//
//		if (good1)
//			result = 3;

		// help system
		boolean matchHelpSystem = FeatureUtility.matchHelpSystem(content);
//		if (matchHelpSystem)
//			result = 3;

		// example
		boolean eg2 = contentLower.startsWith("for example") || contentLower.startsWith("example")
				|| contentLower.contains("something like");

		if (eg2)
			result = 5;

//		boolean exp = contentLower.startsWith("ideally") || contentLower.startsWith("note")
//				|| (contentLower.startsWith("in order to") || contentLower.matches(".*if.*then.*") && isRealFirst == 1);
//
//		if (exp || contentLower.contains("by default") || contentLower.startsWith("while")
//				|| contentLower.startsWith("sometimes"))
//			result = 0;
//
//		boolean goal = contentLower.contains("goal") || contentLower.contains("wanna");
//		if (goal)
//			result = 1;

//		if (contentLower.startsWith("let me know"))
//			result = 2;

//		if (act1)
//			result = 0;
//
//		if (act2)
//			result = 1;
//
//		if (wesupport)
//			result = 1;

//		if (proposal)
//			result = 1;

		// if+isBAD not drawback
//		boolean drawback = contentLower.contains("be forced to")
//				|| (contentLower.startsWith("but") && containNEG == 1 && matchIsNotBAD != 1)
//				|| contentLower.contains("not possible") || contentLower.contains("not a good idea")
//				|| contentLower.contains("there is not a way") || contentLower.contains("unfortunately")
//				|| contentLower.contains("not pretty") || contentLower.contains("again and again")
//				|| contentLower.contains("no need")
//				|| (contentLower.startsWith("care will be taken") && matchIsNotBAD == 1)
//				|| (contentLower.contains("support") && (containNEG == 1 || contentLower.contains("only"))
//						&& !content.contains("should"))
//				|| (containNEG == 1 && contentLower.contains("only"))
//				|| contentLower.matches(".*currently[^.;?\"']*only.*")
//				|| contentLower.contains("will only") || contentLower.contains("can only")
//				|| contentLower.contains("don't wish") || contentLower.contains("won't like")
//				|| contentLower.contains("won't be able") || contentLower.contains("but i cannot")
//				|| contentLower.contains("user cannot") || contentLower.contains("had an issue")
//				|| contentLower.contains("may be an issue") || contentLower.contains("drawback")
//				|| contentLower.contains("nowhere") || contentLower.contains("at all")
//				|| contentLower.contains("a lot of work") || contentLower.contains("more work")
//				|| content.matches(".*why[^,.;?\"']*only.*");
//
//		if (drawback)
//			result = 4;
//
//		boolean eg3 = (containlink || contentLower.contains(" like ")) && (numValidVerbs < 2 && numValidWords < 10)
//				&& !contentLower.contains("look like") && !contentLower.contains("looks like");
//		if (eg3)
//			result = 5;
//
//		if (result == 3) {
//			if (contentLower.contains("only"))
//				result = 0;
//
//			if (contentLower.contains("would be nice"))
//				result = 3;
//		}
//
//		boolean wouldbenice = contentLower.contains("it would be nice ")
//				|| contentLower.contains("it would be much better") || contentLower.contains("it would be great")
//				|| contentLower.contains("we would like to");
//
//		if (wouldbenice)
//			result = 1;
//
//		if (contentLower.startsWith("please") || contentLower.startsWith("the idea is")
//				|| contentLower.startsWith("implement support") || contentLower.contains("we could ")
//				|| contentLower.contains("we might ") || contentLower.contains("it should be possible")
//				|| contentLower.contains("we can ") || contentLower.contains("we may ")
//				|| contentLower.contains("we should ") || contentLower.contains("should we")
//				|| contentLower.contains("we'd need") || contentLower.contains("OGM has to")) // subject=OGM
//
//			result = 1;

//		if (contentLower.contains("i like to"))
//			result = 1;

//		if (subject.equalsIgnoreCase("candidate"))
//			result = 0;

		return result;

	}
}
