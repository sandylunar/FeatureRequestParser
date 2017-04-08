package main.java.bean;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class Node {
    public String tag;
    String sentence;
    Node parent;
    ArrayList<Node> children = new ArrayList<Node>();

    public Node(String tag, String sentence) {
        this.tag = tag;
        this.sentence = StringEscapeUtils.escapeHtml4(sentence);
    }

    public Node(String tag, String sentence, Node parent) {
        this.tag = tag;
        this.sentence = StringEscapeUtils.escapeHtml4(sentence);
        addParent(parent);
    }

    public void addParent(Node parent) {
        this.parent = parent;
    }

    public void addChildren(Node child) {
        child.addParent(this);
        children.add(child);
    }

    public boolean hasChildren() {
        if (children == null || children.isEmpty())
            return false;

        return true;
    }

    public String toString() {
        /*String result = "["+tag+"]"+sentence+"\n";

        if(hasChildren()){
            for(Node node : children){
                result+="\t"+node.toString();
            }
        }else
            result = "\t"+result;

        return result;*/
        tag = tag.replaceAll("\\s+", " ");
        sentence = sentence.replaceAll("\\s+", " ");
        String result = "<li><span class=\"glyphicon glyphicon-sort-by-attributes\"><span style=\"font-family:sans-serif;font-size:130%;font-weight:600\">&nbsp;" + tag.toUpperCase() + "<br></span><span style=\"font-family:sans-serif;font-size:120%;font-weight:300\">" + sentence + "</span></span><br><br>";
        if (hasChildren()) {
            result += "<ul>";
            for (Node node : children) {
                String tmp = node.toString();
                //System.out.println(result);
                if (node.sentence.equals("")){
                    tmp = tmp.replaceFirst("<br><br>$", "");
                }
                if (node.parent.sentence.equals("")) {
                    tmp = tmp.replaceFirst("(?<=<span style=\"font-family:sans-serif;font-size:130%;font-weight:600\">).+?(?=</span>)", "&nbsp;");
                }
                if (node.tag.equalsIgnoreCase("want")) {
                    tmp = tmp.replaceFirst("(?<=span class=\")glyphicon glyphicon-sort-by-attributes", "glyphicon glyphicon-star");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#000080");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#000080");
                }
                if (node.tag.equalsIgnoreCase("benefit")){
                    tmp = tmp.replaceFirst("(?<=span class=\")glyphicon glyphicon-sort-by-attributes", "glyphicon glyphicon-thumbs-up");
                }
                if (node.tag.equalsIgnoreCase("example")){
                    tmp = tmp.replaceFirst("(?<=span class=\")glyphicon glyphicon-sort-by-attributes", "glyphicon glyphicon-hand-right");
                }
                result += tmp;
            }
            result += "</ul>";
        } else
            result += "</li>";

        result = result.replaceAll("\n*", "");
        return result;
    }


}
