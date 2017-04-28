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
        String result = "<li><span style=\"font-family:sans-serif;font-size:130%;font-weight:600\"><i class=\"fa fa-book\" aria-hidden=\"true\"></i>&nbsp;" + tag.toUpperCase() + "<br></span><span style=\"font-family:sans-serif;font-size:120%;font-weight:300\">" + sentence + "</span><br>";
        if (tag.equalsIgnoreCase("title")){
            result = result.replaceFirst("<br></span>", "&nbsp;&nbsp;</span>");
        }
        if (hasChildren()) {
            result += "<ul>";
            for (Node node : children) {
                String tmp = node.toString();
                //System.out.println(result);
                if (node.sentence.equals("")){
                    tmp = tmp.replaceFirst("<span style=\"font-family:sans-serif;font-size:120%;font-weight:300\">.*?</span><br>", "");
                    tmp = tmp.replaceFirst("<br></span>", "</span>");
                }
                if (node.parent.sentence.equals("")) {
                    tmp = tmp.replaceFirst("(?<=<i class=\"fa fa-book\" aria-hidden=\"true\"></i>).+?(?=</span>)", "&nbsp;");
                }
                /*if (!node.tag.equalsIgnoreCase("intent")){
                    tmp = tmp.replaceFirst("<br><br>", "");
                }*/
                if (node.tag.equalsIgnoreCase("intent")) {
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "heart");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#FF6A6A");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#FF6A6A");
                }
                if (node.tag.equalsIgnoreCase("benefit")){
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "smile-o");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#FF8247");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#FF8247");
                }
                if (node.tag.equalsIgnoreCase("drawback")){
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "frown-o");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#FFB90F");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#FFB90F");
                }
                if (node.tag.equalsIgnoreCase("example")){
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "eye");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#63BE78");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#63BE78");
                }
                if (node.tag.equalsIgnoreCase("explanation")){
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "bookmark");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#A2D07F");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#A2D07F");
                }
                if (node.tag.equalsIgnoreCase("trivia")){
                    tmp = tmp.replaceFirst("(?<=i class=\"fa fa-)book", "trash");
                    tmp = tmp.replaceFirst("font-weight:600", "font-weight:600;color:#E0E383");
                    tmp = tmp.replaceFirst("font-weight:300", "font-weight:450;color:#E0E383");
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
