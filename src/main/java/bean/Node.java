package main.java.bean;

import java.util.ArrayList;

public class Node {
    public String tag;
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
        child.addParent(this);
        children.add(child);
    }

    public boolean hasChildren(){
        if(children==null || children.isEmpty())
            return false;

        return true;
    }

    public String toString(){
        /*String result = "["+tag+"]"+sentence+"\n";

        if(hasChildren()){
            for(Node node : children){
                result+="\t"+node.toString();
            }
        }else
            result = "\t"+result;

        return result;*/
        String result = "<li><span class=\"glyphicon glyphicon-sort-by-attributes\"><span style=\"font-family:Arial;font-size:120%;font-weight:bold\">&nbsp;"+tag.toUpperCase()+"<br></span>"+sentence+"\n"+"</span>";
        if (hasChildren()){
            result += "<ul>";
            for(Node node : children){
                String tmp = node.toString();
                //System.out.println(result);
                if (node.parent.sentence.equals("")){
                    tmp = tmp.replaceFirst("(?<=<span style=\"font-family:Arial;font-size:120%;font-weight:bold\">).+?(?=<\\/span>)", "&nbsp;");
                }
                result += tmp;
            }
            result += "</ul>";
        }
        else
            result += "</li>";
        return result;
    }


}
