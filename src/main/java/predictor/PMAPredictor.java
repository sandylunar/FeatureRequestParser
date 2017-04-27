package main.java.predictor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import main.java.bean.FeatureRequestOL;
import main.java.bean.Rule;
import main.java.core.DataParser;
import main.java.core.RequestAnalyzer;
import main.java.core.RequestClassifier;
import main.java.util.FeatureUtility;
import weka.core.Instance;
import weka.core.Instances;

public class PMAPredictor implements TagPredictor {
	
	private static final double REWARD = 0.01;
	private static final double PUNISH = 0.01;

	
	HashMap<Integer, ArrayList<Integer>> variablesMap;
	int[] orders;
	ArrayList<String> vabNames;
	ArrayList<Rule> rules;
	private Instances data;
	//int[] importances = {10,20,25,55,10,10};
	int[] importances = {1,1,1,1,1,1};
	//double[] classWeights = {0,0,0,0,0,0};
	double[] classWeights = {1,1,1,1,1,1};
	public static String[] tagNames = new String[] { "explanation", "want", "useless", "benefit", "drawback","example" };
	
	public int predictTagIndex(Instance item, Instances data, FeatureRequestOL request, int index) {
		
		double[] vals = RequestAnalyzer.getVariables(item, data,false);
		int winner = 0;
		Rule[] sortedByWeight = sortedByWeight();
		ArrayList<Integer> winners = new ArrayList<Integer>();
		
		for(int rank = sortedByWeight.length-1 ;  rank >= 0; rank --){
			int id = sortedByWeight[rank].getId();
			if(vals[id] == 1){
				winner = id;
				break;
				}
		}
		
		for(int rank = sortedByWeight.length-1 ;  rank >= 0; rank --){
			int id = sortedByWeight[rank].getId();
			if(vals[id] == 1){
				winners.add(id);
			}
		}
		
		//int predict = getWeightedTagIndex(winners);
		
		int predict = getTagIndex(winner);
		return predict;
	}

	private int getWeightedTagIndex(ArrayList<Integer> winners) {
		int predict = -1;
		double maxWeight = 0;
		for(int i = 0; i < winners.size(); i++){
			int id = winners.get(i);
			double consequence = getConsequence(id);
			double temp = getWeight(id)*classWeights[(int)consequence];
			if(temp > maxWeight){
				maxWeight = temp;
				predict = (int)consequence;}
			
		}
		return predict;
	}

	private Rule[] sortedByWeight() {
		Rule[] list = new Rule[rules.size()];
		list =	rules.toArray(list);
		
		int d = list.length;
		while(true){
			d = d/2;
			for(int x = 0; x < d; x++){
				for(int i = x+d; i < list.length; i=i+d){
					Rule temp = list[i];
					int j;
					for(j=i-d; j>=0&&list[j].getWeight()>temp.getWeight();j=j-d){
						list[j+d] = list[j];
					}
					list[j+d] = temp;
				}
			}
		if(d==1)
			break;
		
		}
		
		
		return list;
	}

	private int getTagIndex(int winner) {
		Set<Integer> set = variablesMap.keySet();
		for(Integer i : set){
			ArrayList<Integer> list = variablesMap.get(i);
			for(int j : list){
				if(j == winner)
					return i;
			}
		
		}
		return -1;
	}

	public void setTagRuleMap(HashMap<Integer, ArrayList<Integer>> variablesMap) {
		this.variablesMap = variablesMap;
		
	}

	public void setRuleOrders(int[] orders) {

		this.orders = orders;
	}

	

	public void trainOrders(Instances dataset) {
		ListIterator<Instance> list = dataset.listIterator();
		int count = 0;
		ArrayList<Integer> noneMatchedItems = new ArrayList<Integer>();
		while (list.hasNext()) {
			Instance item = list.next();
			
			double[] vals = new double[dataset.numAttributes()];
			for(int i = 0; i < dataset.numAttributes(); i++){
				vals[i] = item.value(i);
			}
			
			//update orders start
			double classValue = vals[dataset.numAttributes()-1];
			ArrayList<Integer> ruleIndexList = RequestAnalyzer.variablesMap.get((int)classValue);
			int maxRightRule = 0;
			int maxAllRule = 0;
			double maxWeight = 0;
			for(int i : ruleIndexList){
				if(vals[i] == 1){ 
					//System.out.println("Item "+count+" Match to right rule: "+vabNames.get(i)+", rank ="+getWeight(i]);
					if(getWeight(i)>maxWeight){
						maxWeight = getWeight(i);
						maxRightRule = i;
						}
					}
			}
			
			maxWeight = 0;
			for(int i = 0; i < dataset.numAttributes()-1;i++){
				if(vals[i] == 1){
					//System.out.println("Item "+count+" All matched rules: "+vabNames.get(i)+", rank ="+getWeight(i));
					if(getWeight(i)>maxWeight){
						maxWeight = getWeight(i);
						maxAllRule = i;
					}
				}
			}
			
			if(maxRightRule == 0)
				noneMatchedItems.add(count);
			
			double w_maxRightRule = getWeight(maxRightRule);
			double w_maxAllRule = getWeight(maxAllRule);
			double csq_maxRightRule = getConsequence(maxRightRule);
			double csq_maxAllRule = getConsequence(maxAllRule);
			
			if( w_maxRightRule < w_maxAllRule && (csq_maxRightRule!=csq_maxAllRule)){
				Instance textItem = data.get(count);
				String text = textItem.stringValue(0);
//				System.out.println("\nGoing to swap");
//				System.out.println(text);
//				System.out.println("Item "+count+" All matched rules: "+vabNames.get(maxAllRule)+", rank ="+getWeight(maxAllRule));
//				System.out.println("Item "+count+" Match to right rule: "+vabNames.get(maxRightRule)+", rank ="+getWeight(maxRightRule));
				
				for(Rule r : rules){ 
					double weight = r.getWeight();
					if(weight>w_maxRightRule && weight<=w_maxAllRule){
						r.setWeight(weight-1);
//						System.out.println("Now rule <"+r.getName()+"> is rank to: "+r.getWeight());	
					}
				}
				resetWeight(maxRightRule,w_maxAllRule);
				
//				System.out.println("Now rule <"+vabNames.get(maxRightRule)+"> is rank to: "+getWeight(maxRightRule));
				
				
			}
			count++;
			
		}
		
	}
	
	public void trainClassWeights(Instances dataset, int round) {
		while(round >0){
			ListIterator<Instance> list = dataset.listIterator();
			int count = 0;
			while (list.hasNext()) {
				Instance item = list.next();
				
				double[] vals = new double[dataset.numAttributes()];
				for(int i = 0; i < dataset.numAttributes(); i++){
					vals[i] = item.value(i);
				}
				
				//update orders start
				double classValue = vals[dataset.numAttributes()-1];
				ArrayList<Integer> positives = new ArrayList<Integer>();
				
				for(int i = 0; i < dataset.numAttributes()-1;i++){
					if(vals[i] == 1){
						positives.add(i);
						double p_consequence = getConsequence(i);
						if(p_consequence == -1)
							continue;
						if(p_consequence == classValue)
							reward(p_consequence,classValue
									);
						else
							punish(p_consequence,classValue);
						
					}
				}
				count++;
			}
			round--;
		}
		
		
	}
	
	private void punish(double consequence, double classValue) {
		double oldWeight = classWeights[(int)consequence];
		int importanceDeta = importances[(int)classValue]-importances[(int)consequence]+1;
		if(importanceDeta<=0)
			importanceDeta = 1;
		double newWeight = oldWeight - PUNISH*oldWeight*(importanceDeta);
		classWeights[(int)consequence] = newWeight;
		
	}

	private void reward(double consequence,double classValue) {
		double oldWeight = classWeights[(int)consequence];
		double importance = importances[(int)classValue];
		double newWeight = oldWeight + REWARD*(1-oldWeight)*importance;
		classWeights[(int)consequence] = newWeight;
	}

	private double getConsequence(int id) {
		for(Rule r : rules){
			if(r.getId() == id){
				return r.getConsequence();
			}
		}
		
		return -1;
	}

	public  double getWeight( int id){
		
		for(Rule r : rules){
			if(r.getId() == id){
				return r.getWeight();
			}
		}
		
		return -1;
	}
	
	public void resetWeight(int id, double newWeight){
		for(Rule r : rules){
			if(r.getId() == id){
				r.setWeight(newWeight);
				return;
			}
		}
		System.out.println("Error, cannot find id = "+id);
		
	}

	public void setVarNames(ArrayList<String> vabNames) {
		this.vabNames = vabNames;
		
	}

	public int[] getOrders() {
		return orders;	}

	public void setRuleOrders(ArrayList<Rule> rules) {
		this.rules = rules;
	}
	
	public void printOrders(){
		Rule[] sortedByWeight = sortedByWeight();
		for(int rank = sortedByWeight.length-1 ;  rank >= 0; rank --){
			System.out.println(sortedByWeight[rank]);
		}
	}
	
	public void printClassWeights(){
		System.out.println("\n\nShow the weights for each class.");
		System.out.println("========================");
		for(int i= 0; i <tagNames.length; i++){
			System.out.println(tagNames[i]+": "+classWeights[i]);
		}
		
		System.out.println("When importances = "+Arrays.toString(importances));
	}
	
	 public static void main(String[] args) {
		          int[] a={49,38,65,97,76,13,27,49,78,34,12,64,1};
		          System.out.println("排序之前：");
		         for (int i = 0; i < a.length; i++) {
		             System.out.print(a[i]+" ");
		         }
		       //希尔排序
		        int d = a.length;
		        while(true){
		             d = d / 2;
		             for(int x=0;x<d;x++){
		                for(int i=x+d;i<a.length;i=i+d){
		                     int temp = a[i];
		                    int j;
		                    for(j=i-d;j>=0&&a[j]>temp;j=j-d){
		                         a[j+d] = a[j];
		                     }
		                     a[j+d] = temp;
		                }
		            }
		             if(d == 1){
	                break;
		            }
		         }
		         System.out.println();
		         System.out.println("排序之后：");
		        for (int i = 0; i < a.length; i++) {
		             System.out.print(a[i]+" ");
		       }
		    }

	public void setTextDataSource(Instances data) {
		this.data = data;
		
	}

	public void trainByNegatives(int round) {
		// TODO Auto-generated method stub
		
		while(round>0){
		
		Instances negatives = new Instances(data);
		data.setClassIndex(data.numAttributes()-1);
		negatives.delete();
		negatives.setClassIndex(negatives.numAttributes()-1);
		
		ListIterator<Instance> list = data.listIterator();
		while (list.hasNext()) {
			Instance item = list.next();
			int index = (int) item.classValue();
			String sentence = item.stringValue(0);

			int predict = predictTagIndex(item, data, null, -1);
			
			if(index != predict){
				negatives.add(item);
			}
		}
		
		System.out.println("\n\nNegatives:"+negatives.numInstances()+" at round "+round);
		//System.out.println(negatives);
		
		int valuesize = RequestClassifier.getValueSize(negatives);
		//System.out.println("size of rules = "+valuesize);
		Instances booleanNegatvies = DataParser.buildBooleanDataSet(negatives,valuesize,"numeric");
		
		trainOrders(booleanNegatvies);
		
		round--;
		}
	}

}
