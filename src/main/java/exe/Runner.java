package main.java.exe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import main.java.core.DataParser;
import main.java.core.RequestAnalyzer;
import main.java.core.RequestClassifier;
import main.java.util.FeatureUtility;
import weka.associations.Apriori;
import weka.core.Instances;

public class Runner {

	static String PMA_Tagged_File = "data//PMA.properties";
	static String ACTIVEMQ_Tagged_File = "data//PMA+ActiveMQ+AspectJ+mopidy.properties";//PMA+ActiveMQ+AspectJ+mopidy
	static String rawDataFile = "data//dataset_pma+actmq+aspectj+mopidy-0328.arff";

	//String rawDataFile = "data//dataset_16-attributes-tfidf-0305.arff";
	static String booleanData = "data//dataset_boolean_";
	static String classLabel = "category";

	public static void main(String[] args) throws Exception {
		//GroupTrainData.groupTrainData("resource//train", "resource//PMA.properties");

		predictByAnalyzer();

		//predictByAnalyzerInput("nominal");


		//buildDataFromRules();

		//predictByText(true);
		//String rawDataFile = "resource//dataset_16-attributes-tfidf-0305.arff";
		//String filteredDataFile = "resource//dataFiltered_16-attributes-tfidf-actmq-0306.arff";
		//String filteredDataFile = "data//dataFiltered_16-attributes-tfidf-0305.arff";

		//Instances data = DataParser.readIntoFeatureRequests(ACTIVEMQ_Tagged_File,"full");
		//FeatureUtility.exportInstancesToFile(data,rawDataFile);

		//Instances data = new Instances(new BufferedReader(new FileReader(rawDataFile)));
		//data.setClassIndex(data.numAttributes()-1);
		//RequestClassifier.filterData(data, filteredDataFile);
		//FeatureUtility.exportInstancesToFile(data, filteredDataFile);
		//RequestClassifier.classify(filteredDataFile, rawDataFile);
		//RequestClassifier.predict("resource//exp1//dataFiltered2.arff",classIndex);
		//FeatureUtility.printInstancesByTag(data, "SENTENCE");

		//System.out.println("Total Size of dataset = "+data.numInstances());

		//RequestAnalyzer.predictTag(data);

		//create dataset to do first classify: useless, want, explanation.

		//buildDataFromRules();
		//
		//RequestClassifier.printInstancesByTag(data);






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
	public static void predictByAnalyzer() throws FileNotFoundException, IOException{
		//Instances data = DataParser.readIntoFeatureRequests(ACTIVEMQ_Tagged_File);
		//FeatureUtility.exportInstancesToFile(data,rawDataFile);

		Instances data = new Instances(new BufferedReader(new FileReader(rawDataFile)));
		RequestAnalyzer.predictTag(data);
	}

	@Test
	public static void predictByAnalyzerInput(String option) throws Exception{

//		InputStreamReader in = new InputStreamReader(new FileInputStream(rawDataFile),"UTF-8");
//		BufferedReader reader = new BufferedReader(in);
//		Instances data = new Instances(reader);

		Instances data = new Instances(new BufferedReader(new FileReader(rawDataFile)));
		data.setClassIndex(data.numAttributes()-1);
		int valuesize = RequestClassifier.getValueSize(data);
		System.out.println("size of rules = "+valuesize);
		Instances dataset = DataParser.buildFirstDataSet(data,valuesize,option);
		System.out.println("NumInstances = "+dataset.numInstances());
		FeatureUtility.exportInstancesToFile(dataset,booleanData+option+".arff");


		InputStreamReader isr = new InputStreamReader(new FileInputStream(booleanData+option+".arff"),"UTF-8");
		BufferedReader read = new BufferedReader(isr);
		Instances newdata = new Instances(read);
		//	Instances newdata = new Instances(new BufferedReader(new FileReader(booleanData)));
		newdata.setClassIndex(newdata.numAttributes()-1);

		System.out.println(newdata.numInstances());
		//RequestClassifier.classify(newdata,classLabel);

		// build associator
		Apriori apriori = new Apriori();
		apriori.setClassIndex(newdata.classIndex());
		apriori.buildAssociations(newdata);

		// output associator
		System.out.println(apriori);

	}


}