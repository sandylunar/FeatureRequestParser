package main.java.bean;

import java.util.ArrayList;

public class Node {
	String tag;
	String sentence;
	Node parent;
	ArrayList<Node> children = new ArrayList<Node>();
	
	public Node(String tag, String sentence){
		this.tag = tag;
		this.sentence = sentence;
	}
	
	public Node(String tag, String sentence, Node parent){
		this.tag = tag;
		this.sentence = sentence;
		addParent(parent);
	}
	
	public void addParent(Node parent){
		this.parent = parent;
	}
	
	public void addChildren(Node child){
		children.add(child);
	}
	
	public boolean hasChildren(){
		if(children==null || children.isEmpty())
			return false;
		
		return true;
	}
	
	public String toString(){
		String result = "["+tag+"]"+sentence+"\n";
		
		if(hasChildren()){
			for(Node node : children){
				result+="\t"+node.toString();
			}
		}else
			result = "\t"+result;
		
		return result;
	}
	

}
