package main.java.exe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Set;

import org.junit.Test;

import main.java.bean.Rule;
import main.java.core.DataParser;
import main.java.core.RequestAnalyzer;
import main.java.core.RequestClassifier;
import main.java.predictor.FuzzyPredictorFromPMA;
import main.java.predictor.FuzzyPredictorPMAMopidy;
import main.java.predictor.FuzzyPredictorPMAMopidyActivemq;
import main.java.predictor.FuzzyPredictorPMAMopidyActivemqAspectj;
import main.java.predictor.PMAPredictor;
import main.java.predictor.TagPredictor;
import main.java.util.FeatureUtility;
import weka.associations.Apriori;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Runner {
	
	static String PMA_Tagged_File = "data//PMA.properties";
	static String ACTIVEMQ_Tagged_File = "data//PMA+ActiveMQ+AspectJ+mopidy.properties";//PMA+ActiveMQ+AspectJ+mopidy
	static String Hibernate_File = "data//hibernate.txt";
	//static String rawDataFile = "data//dataset_pma+actmq+aspectj+mopidy-0328.arff";
	static String rawDataFile = "data//hiberante.arff";
	
	//String rawDataFile = "data//dataset_16-attributes-tfidf-0305.arff";
	static String booleanData = "data//dataset_boolean_v2_";
	static String classLabel = "category";
	static String option = "numeric";
	
	public static void main(String[] args) throws Exception {
		//GroupTrainData.groupTrainData("resource//train", "resource//PMA.properties");
		
		//RequestAnalyzer.showItemsByTag("", "data//pma.txt");
		  
		//convertRawData("data//pma.txt","data//pma-v4.arff","full");
	
		//convertRawData("data//PMA+ActiveMQ+AspectJ+mopidy.properties","data//dataset_pma+actmq+aspectj+mopidy-0410.arff","full");
		
		//convertRawData("data//swt.txt","data//swt-v3.arff","full");
		//convertRawData("data//hibernate.txt","data//hibernate-v4.arff","full");
		
		//predictByAnalyzer("data//swt-v3.arff");
		
		//predictByAnalyzer("data//hibernate-v4.arff");
		
		//predictByAnalyzer("data//dataset_pma+actmq+aspectj+mopidy-0410.arff");
  
		
		//predictByAnalyzerInput("data//dataset_pma+actmq+aspectj+mopidy-0328.arff",option);//nominal ,numeric 
		
		//calculateSuppConf("data//pma-v4.arff");
		
		//calculateSuppConf("data//dataset_pma+actmq+aspectj+mopidy-0410.arff");
		FuzzyPredictorFromPMA fp = new FuzzyPredictorFromPMA();
		predictByAnalyzer("data//mopidy.arff",fp);
		
		
		//convertRawData("data//mopidy.txt","data//mopidy.arff","full");
		//calculateSuppConf("data//mopidy.arff");
		FuzzyPredictorPMAMopidy fp2 = new FuzzyPredictorPMAMopidy();
		//predictByAnalyzer("data//pma-v4.arff",fp2);
		//predictByAnalyzer("data//mopidy.arff",fp2);
		
		//convertRawData("data//activemq.txt","data//activemq.arff","full");
		predictByAnalyzer("data//activemq.arff",fp2);
		
		//calculateSuppConf("data//activemq.arff");
		
		FuzzyPredictorPMAMopidyActivemq fp3 = new FuzzyPredictorPMAMopidyActivemq();
		//convertRawData("data//aspectj.txt","data//aspectj.arff","full");
		//predictByAnalyzer("data//pma-v4.arff",fp3);
		//predictByAnalyzer("data//mopidy.arff",fp3);
		//predictByAnalyzer("data//activemq.arff",fp3);
		//calculateSuppConf("data//aspectj.arff");
		predictByAnalyzer("data//aspectj.arff",fp3);
		
		
		FuzzyPredictorPMAMopidyActivemqAspectj fp4 = new FuzzyPredictorPMAMopidyActivemqAspectj();
		predictByAnalyzer("data//hibernate-v4.arff",fp4);
		predictByAnalyzer("data//swt-v3.arff",fp4);
		
		
		//RequestClassifier.classify("data//dataset_boolean_v2_numeric.arff", "category");
		
		//autoTag("data//log4j-notag.txt","data//log4j-v1.txt");
		//convertRawData("data//log4j-v2.txt","data//log4j-v2.arff","full");
		predictByAnalyzer("data//log4j-v2.arff",fp4);
		//RequestClassifier.predict("data//pma-v4.arff",classIndex);
		//buildDataFromRules();
		
		//autoTag("data//hdfs-notag.txt","data//hdfs-v1.txt");
		//convertRawData("data//hdfs-v1.txt","data//hdfs-v1.arff","full");
		predictByAnalyzer("data//hdfs-v1.arff",fp4);
		//predictByText(true);
		//String rawDataFile = "resource//dataset_16-attributes-tfidf-0305.arff";
		//String filteredDataFile = "resource//dataFiltered_16-attributes-tfidf-actmq-0306.arff";
		//String filteredDataFile = "data//dataFiltered_16-attributes-tfidf-0305.arff";
				
		//autoTag("data//archiva-notag.txt","data//archiva-v1.txt");
		//convertRawData("data//archiva-v1.txt","data//archiva-v1.arff","full");
		predictByAnalyzer("data//archiva-v1.arff",fp4);
		   
		//Instances data = new Instances(new BufferedReader(new FileReader(rawDataFile)));
		//data.setClassIndex(data.numAttributes()-1);
		//RequestClassifier.filterData(data, filteredDataFile);
		//FeatureUtility.exportInstancesToFile(data, filteredDataFile);
		
		//RequestClassifier.predict("resource//exp1//dataFiltered2.arff",classIndex);
		//FeatureUtility.printInstancesByTag(data, "SENTENCE");
		
		//System.out.println("Total Size of dataset = "+data.numInstances());
		
		//RequestAnalyzer.predictTag(data);
		
		//create dataset to do first classify: useless, want, explanation.
	
		//buildDataFromRules();
		//
		//RequestClassifier.printInstancesByTag(data);
		
		testBefore();
		
		
		
		
	}
	
	private static void testBefore() throws FileNotFoundException, IOException {
		
		System.out.println("\n\n\nTest Before\n====================================================");
		
//		FuzzyPredictorFromPMA fp = new FuzzyPredictorFromPMA();
//		predictByAnalyzer("data//pma-v4.arff",fp);
//		
//		
//		FuzzyPredictorPMAMopidy fp2 = new FuzzyPredictorPMAMopidy();
//		predictByAnalyzer("data//pma-v4.arff",fp2);
//		predictByAnalyzer("data//mopidy.arff",fp2);

		
		
//		FuzzyPredictorPMAMopidyActivemq fp3 = new FuzzyPredictorPMAMopidyActivemq();
//		predictByAnalyzer("data//pma-v4.arff",fp3);
//		predictByAnalyzer("data//mopidy.arff",fp3);
//		predictByAnalyzer("data//activemq.arff",fp3);
//		
//		
		FuzzyPredictorPMAMopidyActivemqAspectj fp4 = new FuzzyPredictorPMAMopidyActivemqAspectj();
		predictByAnalyzer("data//pma-v4.arff",fp4);
		predictByAnalyzer("data//mopidy.arff",fp4);
		predictByAnalyzer("data//activemq.arff",fp4);
		predictByAnalyzer("data//aspectj.arff",fp4);
		
	}

	private static void autoTag(String input, String output) throws FileNotFoundException, UnsupportedEncodingException {
		DataParser.loadTaggedFRFile(input);
		DataParser.constructFeatureRequestListFromNonTag();
		System.out.println(DataParser.autoTagBuffer.toString());
		
		FileOutputStream fop = null;
		File file;
		file = new File(output);
		fop = new FileOutputStream(file);
		try {
			fop.write(DataParser.autoTagBuffer.toString().getBytes("UTF-8"));
			fop.flush();
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Writed to File: "+output);
	}

	private static void convertRawData(String filename, String outputFile, String option) throws Exception {
		// TODO Auto-generated method stub
	Instances data = DataParser.readIntoFeatureRequests(filename,option);
	FeatureUtility.exportInstancesToFile(data,outputFile);
	}

	private static void calculateSuppConf(String filename) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename),"UTF-8");
		BufferedReader read = new BufferedReader(isr);		   
		Instances data = new Instances(read);
		
		data.setClassIndex(data.numAttributes()-1);
		int valuesize = RequestClassifier.getValueSize(data);
		
		
		
		System.out.println("size of rules = "+valuesize);
		Instances dataset = DataParser.buildBooleanDataSet(data,valuesize,option);
		System.out.println("NumInstances = "+dataset.numInstances());
		FeatureUtility.exportInstancesToFile(dataset,booleanData+option+".arff");
		
		
		double[][] sc = RequestAnalyzer.calculateSuppConf(dataset);
		ArrayList<String> vabNames = RequestAnalyzer.variableNames;
		
		
		
		System.out.println("Start calculating support and confidence...");
		System.out.println("Total Size of dataset = "+dataset.numInstances());
		System.out.println("Total Size of attributes = "+dataset.numAttributes());
		System.out.println("Total Size of varNames = "+vabNames.size());
		
		Set<Integer> set = RequestAnalyzer.variablesMap.keySet();
		int[] rankCategory = {0,5,4,2,3,1};
		double[] confidences = new double[sc.length];
		Double[] confidences2 = null;
		double sum[][] =RequestAnalyzer.sum;
		double numTags[]= RequestAnalyzer.numTags;
		
		ArrayList<Double> sortedConfidence = new ArrayList<Double>();
		ArrayList<Rule> rules = new ArrayList<Rule>();

		for(Integer i : rankCategory){
			
			System.out.println("\n\n==============================");
			System.out.println(RequestAnalyzer.tagNames[i]);
			ArrayList<Integer> list = RequestAnalyzer.variablesMap.get(i);
			ArrayList<Rule> rulesubset = new ArrayList<Rule>();
			
			
					
			for(int j : list){
				String name = vabNames.get(j);
				double s = sc[j][1];
				double c = sc[j][0];
				confidences[j]=c;      
				//System.out.printf("%d - \"%s\" : confidence = %.2f (%.0f/%.0f), support = %.2f(%.0f/%.0f)\n",j,name,c,sum[j][0],sum[j][1],s,sum[j][0],numTags[(int)RequestAnalyzer.getVariableIndex(j)]);
				if(c>0){
					Rule r = new Rule();
					r.setName(name);
					r.setConfidence(c);
					r.setSupport(s);
					r.setConsequence(i);
					r.setWeight(0);
					r.setId(j);
					rulesubset.add(r);
				}
				else{
					System.out.printf("#%d - \"%s\" : confidence = %.2f (%.0f/%.0f), support = %.2f(%.0f/%.0f)\n",j,name,c,sum[j][0],sum[j][1],s,sum[j][0],numTags[(int)RequestAnalyzer.getVariableIndex(j)]);

				}
				
			}
			
			Rule[] rArray = new Rule[rulesubset.size()];
			rArray=	rulesubset.toArray(rArray);
			Arrays.sort(rArray);
			int before = rules.size();
			for(int k = 0; k < rArray.length; k++){
				Rule r = rArray[k];
				r.setWeight(before+k);
				rules.add(r);
			}
		}
		
		for(Rule r : rules){
			System.out.println(r);
		}
		
		//TODO, rank by type
		
		//orders[i] = ranks for rule i;
		
		PMAPredictor pp = new PMAPredictor();
		
		pp.setTagRuleMap(RequestAnalyzer.variablesMap);
		pp.setRuleOrders(rules);
		pp.setVarNames(vabNames);
		pp.setTextDataSource(data);
		pp.trainOrders(dataset);
		//pp.trainClassWeights(dataset, 100);
		
		//pp.trainByNegatives(5);
		//pp.printClassWeights();
		
		pp.printOrders();

		predictByAnalyzer(filename,pp);

	}
	

	@Test
	public static void predictByText(boolean first) throws Exception{
		String filterFile = "data//dataset_simple_tag_filtered.arff";
		Instances filteredData;
		if(first){
			Instances data = DataParser.readIntoFeatureRequests(ACTIVEMQ_Tagged_File,"simple");
			FeatureUtility.exportInstancesToFile(data,"data//dataset_simple_tag.arff");
			filteredData = RequestClassifier.filterData(data);
			filteredData.setClassIndex(filteredData.attribute("category").index());
			FeatureUtility.exportInstancesToFile(filteredData,filterFile);
		}else{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filterFile)));
			filteredData = new Instances(new BufferedReader(new FileReader(filterFile)));
			filteredData.setClassIndex(filteredData.attribute("category").index());
			
		}
		
		
		RequestClassifier.classify(filteredData,classLabel);
		
	}
	
	@Test
	public static void predictByAnalyzer(String rawDataFile, TagPredictor tp) throws FileNotFoundException, IOException{
		//Instances data = DataParser.readIntoFeatureRequests(ACTIVEMQ_Tagged_File);
		//FeatureUtility.exportInstancesToFile(data,rawDataFile);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(rawDataFile),"UTF-8");
		BufferedReader read = new BufferedReader(isr);		   
		Instances data = new Instances(read);
		
		System.out.println("\n\n==========================================================\n");
		System.out.println("Prediction Result for "+ rawDataFile);
		System.out.println("numInstance = "+data.numInstances());
		RequestAnalyzer.predictTag(data,tp);
	}
	
	@Test
	public static void predictByAnalyzerInput(String filename, String option) throws Exception{
		
//		InputStreamReader in = new InputStreamReader(new FileInputStream(rawDataFile),"UTF-8");
//		BufferedReader reader = new BufferedReader(in);
//		Instances data = new Instances(reader);
		
		Instances data = new Instances(new BufferedReader(new FileReader(filename)));
		data.setClassIndex(data.numAttributes()-1);
		int valuesize = RequestClassifier.getValueSize(data);
		
		
		
		System.out.println("size of rules = "+valuesize);
		Instances dataset = DataParser.buildBooleanDataSet(data,valuesize,option);
		System.out.println("NumInstances = "+dataset.numInstances());
		FeatureUtility.exportInstancesToFile(dataset,booleanData+option+".arff");
		
	
		InputStreamReader isr = new InputStreamReader(new FileInputStream(booleanData+option+".arff"),"UTF-8");
		BufferedReader read = new BufferedReader(isr);
		Instances newdata = new Instances(read);
	//	Instances newdata = new Instances(new BufferedReader(new FileReader(booleanData)));
		newdata.setClassIndex(newdata.numAttributes()-1);
	
		System.out.println(newdata.numInstances());
		RequestClassifier.classify(newdata,classLabel);
		
//		// build associator
//	    Apriori apriori = new Apriori();
//	    apriori.setClassIndex(newdata.classIndex());
//	    apriori.buildAssociations(newdata);
//
//	    // output associator
//	    System.out.println(apriori);
//		
	}
}
