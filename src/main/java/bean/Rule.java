package main.java.bean;

import java.util.ArrayList;

public class Rule implements Comparable<Rule>{
	double confidence;

	double consequence;

	int id;

	String name;

	double support;

	double weight;

	public double getConfidence() {
		return confidence;
	}

	public double getConsequence() {
		return consequence;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getSupport() {
		return support;
	}

	public double getWeight() {
		return weight;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public void setConsequence(double consequence) {
		this.consequence = consequence;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Rule rule) {
		if(this.confidence < rule.confidence)
			return -1;
		else if(this.confidence == rule.confidence)
			return 0;
		else 
			return 1;
	}
	
	public String toString(){
		return "id = "+id+", name = "+name+", confidence = "+confidence+", consequence = "+consequence+", weight = "+weight;
	}
	


}
