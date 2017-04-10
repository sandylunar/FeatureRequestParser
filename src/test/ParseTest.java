package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import main.java.bean.FeatureRequestOL;
import main.java.bean.Node;
import main.java.parse.Parser;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import static test.StanfordCoreNlpDemo.pipeline;

/**
 * Created by zzyo on 2017/3/17.
 */
public class ParseTest {

    private String test = "<p>Hi! Json export not good for Cyrillic in unicode.If I add JSON_UNESCAPED_UNICODE&nbsp;</p><p><br></p><p><br></p><pre style=\"max-width: 100%;\"><code class=\"php hljs\" codemark=\"1\"> <span class=\"hljs-keyword\">if</span> (<span class=\"hljs-keyword\">isset</span>($GLOBALS[<span class=\"hljs-string\">'json_pretty_print'</span>])\n" +
            "                &amp;&amp; $GLOBALS[<span class=\"hljs-string\">'json_pretty_print'</span>]\n" +
            "            ) {\n" +
            "                $encoded = json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);\n" +
            "            } <span class=\"hljs-keyword\">else</span> {\n" +
            "                $encoded = json_encode($data, JSON_UNESCAPED_UNICODE);&nbsp;<span style=\"font-size: inherit;\">}</span></code></pre><p>all exported beautifully.<a href=\"http://www.baidu.com\" target=\"_blank\">linkExample</a>&nbsp; &nbsp; &nbsp; &nbsp; /home/user/file.sql</p><p><br></p><p><br></p><p>C:\\project\\classes</p><p><br></p>";


    private String test2 = "<p>Hi!&nbsp;\"Json&nbsp;export\"&nbsp;not&nbsp;good&nbsp;for&nbsp;Cyrillic&nbsp;in&nbsp;unicode.</p>\n" +
            "<p>If&nbsp;I&nbsp;add&nbsp;JSON_UNESCAPED_UNICODE&nbsp;all&nbsp;exported&nbsp;beautifully.</p><p><br></p><pre style=\"max-width: 100%;\"><code class=\"php hljs\" codemark=\"1\"> <span class=\"hljs-keyword\">if</span> (<span class=\"hljs-keyword\">isset</span>($GLOBALS[<span class=\"hljs-string\">'json_pretty_print'</span>])\n" +
            "                &amp;&amp; $GLOBALS[<span class=\"hljs-string\">'json_pretty_print'</span>]\n" +
            "            ) {\n" +
            "                $encoded = json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);\n" +
            "            } <span class=\"hljs-keyword\">else</span> {\n" +
            "                $encoded = json_encode($data, JSON_UNESCAPED_UNICODE);\n" +
            "            }\n" +
            "\t\t\t</code></pre><p><br></p><p>../home/user/file1.sql &nbsp; \'file example:\'C:\\Windows\\System32-calc\\calc.exe &nbsp;/home/user/file.sql &nbsp; &nbsp;./home/user/file.java &nbsp; &nbsp;C:\\project\\classes</p><pre style=\"max-width: 100%;\"><code class=\"javascript hljs\" codemark=\"1\"> <span class=\"hljs-function\"><span class=\"hljs-keyword\">function</span> <span class=\"hljs-title\">submit</span>(<span class=\"hljs-params\"></span>) </span>{\n" +
            "        <span class=\"hljs-comment\">// 获取编辑器区域完整html代码</span>\n" +
            "        <span class=\"hljs-keyword\">var</span> html = editor.$txt.html();\n" +
            "        <span class=\"hljs-comment\">/*html = html.replace(/\\s+/g, \"\");*/</span>\n" +
            "        <span class=\"hljs-comment\">//alert(html);</span>\n" +
            "        $.base64.utf8encode = <span class=\"hljs-literal\">true</span>;\n" +
            "        html = $.base64().encode(html,<span class=\"hljs-string\">\"utf8\"</span>);\n" +
            "        <span class=\"hljs-comment\">//alert(html);</span>\n" +
            "        <span class=\"hljs-comment\">//document.write(html);</span>\n" +
            "        html = <span class=\"hljs-string\">\"title=\"</span>+$(<span class=\"hljs-string\">'#title'</span>).val()+<span class=\"hljs-string\">\"&amp;data=\"</span>+html+<span class=\"hljs-string\">\"&amp;type=\"</span>+$(<span class=\"hljs-string\">'#type'</span>).val();\n" +
            "        $.ajax(\n" +
            "                {\n" +
            "                    type: <span class=\"hljs-string\">\"POST\"</span>,\n" +
            "                    url: <span class=\"hljs-string\">\"/edit.html\"</span>,\n" +
            "                    data: html,\n" +
            "                    dataType:<span class=\"hljs-string\">'Text'</span>,\n" +
            "                    success: <span class=\"hljs-function\"><span class=\"hljs-keyword\">function</span>(<span class=\"hljs-params\">data</span>) </span>{\n" +
            "                        location.href = data;\n" +
            "                    }\n" +
            "                }\n" +
            "        );<br></code></pre><p><br></p><p><br></p><p>this is a list:</p><ol><li>move&nbsp;the&nbsp;code&nbsp;which&nbsp;finds&nbsp;$tmp_subdir&nbsp;from&nbsp;file.php&nbsp;to&nbsp;configfile.php</li><li>call&nbsp;this&nbsp;function&nbsp;in&nbsp;both&nbsp;file.php&nbsp;and&nbsp;Encoding.php</li><li>right&nbsp;zzyo1995@qq.com?</li><li>Can&nbsp;i&nbsp;also&nbsp;align&nbsp;the&nbsp;parenthesis&nbsp;a&nbsp;756257660@qq.combit&nbsp;as&nbsp;it&nbsp;doesn't&nbsp;look&nbsp;that&nbsp;good&nbsp;?</li><li>Also&nbsp;I&nbsp;have&nbsp;to&nbsp;move&nbsp;it&nbsp;to&nbsp;ConfigFile.php&nbsp;right&nbsp;?</li></ol><p>this is a list end.</p><div><br></div><div></div><p>Yes,zzyo@hust.edu.cn&nbsp;C:\\project\\classes;&nbsp;  core.java.util;&nbsp; /ext-name/&nbsp; ./&nbsp; /var/cache/mopidy&nbsp;please&nbsp;use&nbsp;coding&nbsp;style&nbsp;as&nbsp;described&nbsp;in&nbsp;our&nbsp;docs:&nbsp;<p>e.g e.g. i.e .NET Imo </p><a href=\"https://github.com/phpmyadmin/phpmyadmin/wiki/Developer_guidelines#coding-style\" target=\"_blank\">https://github.com/phpmyadmin/phpmyadmin/wiki/Developer_guidelines#coding-style</a><p><br></p>";

    private String test4 = "<p>I&nbsp;would&nbsp;appreciate&nbsp;a&nbsp;feature&nbsp;that&nbsp;allows&nbsp;to&nbsp;control&nbsp;some&nbsp;User&nbsp;Interface&nbsp;functionality&nbsp;from&nbsp;phpMyAdmin.&nbsp;In&nbsp;particular,&nbsp;I&nbsp;would&nbsp;like&nbsp;to&nbsp;be&nbsp;able&nbsp;to&nbsp;globally&nbsp;remove&nbsp;the&nbsp;DELETE&nbsp;button&nbsp;from&nbsp;the&nbsp;SQL&nbsp;tab&nbsp;when&nbsp;exploring&nbsp;a&nbsp;table.&nbsp;It&nbsp;seems&nbsp;too&nbsp;easy&nbsp;to&nbsp;accidentally&nbsp;click&nbsp;that&nbsp;button&nbsp;and&nbsp;press&nbsp;Go&nbsp;before&nbsp;realizing&nbsp;what&nbsp;you're&nbsp;doing.&nbsp;I&nbsp;don't&nbsp;wish&nbsp;to&nbsp;have&nbsp;\"DELETE&nbsp;FROM&nbsp;table&nbsp;WHERE&nbsp;1\"&nbsp;ever&nbsp;displayed&nbsp;on&nbsp;our&nbsp;production&nbsp;systems.</p><p>Maybe something like $cfg['ShowSqlDeleteButton']?</p><p>Thanks in advance for your consideration.</p><p><br></p>";
    private String test3 = "<p>aaaa:</p><p><ul><li>bbbbb</li><li>bbbbb</li><li>dddd</li></ul></p><p><br></p>";
    private String test5 = "<p>this is a list :</p><p><ol><li>aaa</li><li>bbb</li><li>ccc</li></ol><div>aaaaa</div><div></div></p><pre style=\"max-width: 100%;\"><code class=\"javascript hljs\" codemark=\"1\">javascript code</code></pre><p><br></p>";
    private String test6 = "<p>this is a list start.</p><ol><li>aaa</li><li>bbb</li><li>this is a file /com/main.java.</li></ol><p>this is a list end,my name is Ronald W. Reagan.</p><p>this is a code. This is another sentence.<br></p><pre style=\"max-width:100%;overflow-x:auto;\"><code class=\"javascript hljs\" codemark=\"1\">javascript code</code></pre><p>this is a code end<br></p>";
    private String test7 = "<p>qqqq:http://www.baidu.com</p><ul class=\" list-paddingleft-2\" style=\"list-style-type: disc;\"><li><p>wwww</p></li><li><p>eeee</p></li><li><p>rrrr</p></li></ul><p>qqqqqqq<br/></p><pre class=\"brush:cpp;toolbar:false\">aaaaaa&nbsp;c#&nbsp;code</pre><p><a href=\"http://www.baidu.com\" target=\"_self\">http://www.baidu.com</a></p>";
    private String test8 = "<p>pppp com.edu.stanford.nlp.this is another sentence.</p>";
    private String test9 = "<p>pppppp com.edu.stanford.nlp. this is another sen.</p><ul class=\" list-paddingleft-2\" style=\"list-style-type: disc;\"><li><p>list1</p></li><li><p>list2</p></li><li><p>list3</p></li></ul><p>this is list end.this is code:</p><pre class=\"brush:cpp;toolbar:false\">#include&lt;iostream&gt;</pre><p>this is code end.</p><p><a href=\"http://www.baidu.com\" target=\"_self\">baidu</a> <a href=\"http://www.youku.com\">http://www.youku.com</a>&nbsp;<a href=\"http://www.baidu.com\">www.baidu.com</a>&nbsp;</p>";
    private String test10 = "<p>this is paths: ./etc/share com.edu.stanford.nlp C:\\windows\\</p>";

    @Test
    public void testCode() {
        String origin = test7;
        System.out.println(origin);
        System.out.println("---------------------   parse code  -----------------------------\n");
        Parser parser = new Parser();
        String tmp = parser.parseCode(origin);
        System.out.println(tmp);
        System.out.println("---------------------   parse link  -----------------------------\n");
        tmp = parser.parseLink(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse list  ------------------------------\n");
        tmp = parser.parseList(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse html&list  ------------------------------\n");
        tmp = parser.parseHtmlToText(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse block  ------------------------------\n");
        ArrayList<String> blocks = parser.parseBlock(tmp);
        System.out.println("---------------------   parse sentences  ------------------------------\n");
        parser.parseSentences(blocks);
        System.out.println("---------------------   parse email  ------------------------------\n");
        tmp = parser.parseEmail(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse quote  ------------------------------\n");
        tmp = parser.parseQuote(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse shorts  ------------------------------\n");
        tmp = parser.parseShort(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse file  ------------------------------\n");
        tmp = parser.parseFile(tmp);
        System.out.println(tmp);
        System.out.println("---------------------   parse path  ------------------------------\n");
        tmp = parser.parsePath(tmp);
        System.out.println(tmp);

        //System.out.println(parser.getReplacementMap());
    }

    @Test
    public void parseTest() {
        Parser parser = new Parser();
        FeatureRequestOL fr = parser.getFR("", "", test10);
        System.out.println(parser.printResult(fr));
        int bIndex = 0;
        for (ArrayList<Integer> block : fr.getBlocks()) {
            System.out.print("Block " + bIndex + ":");
            for (int index : block) {
                System.out.print(index + "  ");
            }
            System.out.print("\n");
            bIndex++;
        }
        System.out.print("origin text:\n");
        System.out.print(fr.toString());
    }

    @Test
    public void tokenTest() {
        String content = "<For example0><For example1><For example2><dotNET3><In my opinion4>";
        String[] tokens = Parser.getTokens(content);
        for (String token : tokens) {
            System.out.println(token);
            if (token.equalsIgnoreCase("<For example0>")) {
                System.out.println("匹配:--------------------------------");
            }
        }
    }

    @Test
    public void nlpTest() {
        String origin = "I have an app with different feature modules! \n" +
                "I want to have a Home view, with a menu that let me navigate to each feature module. The routes of each should be defined inside each feature module. I want to have each module rendering in a specific section of that home view ( the router-outlet). I am M. Bond.";
        String origin1 = "this is a list start:<$LIST$>aaa\n" +
                "<LIST>bbb.\n" +
                "<$LIST$>ccc.\n" +
                "\n" +
                "this is a list end.\n" +
                "this is a code.<$CODE-JAVASCRIPT$0>.this is a code end.";
        String origin2 = "this is a list start:<LIST0><LIST1><LIST2>";
        String origin3 = "this is a path com.edu.stanford.nlp.this is another sentence.";
        String origin4 = "this is a path /resources/css.this is another sentence.";
        String origin5 = "this is list end.this is code.";
        /*Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(origin4);
        pipeline.annotate(annotation);
        StringWriter sw = new StringWriter();
        try {
            pipeline.jsonPrint(annotation, sw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(sw.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode sentencesNode = rootNode.path("sentences");
        int sentencesSize = sentencesNode.size();
        System.out.println(sentencesSize);*/
        Reader reader = new StringReader(origin5);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> words : dp) {
            String sentence = "";
            for (HasWord word : words) {
                sentence = sentence.concat(" " + word.word());
            }
            System.out.println(sentence);
        }
        /*Document document = new Document(origin);
        for(Sentence sentence : document.sentences()){
            System.out.println(sentence.toString());
        }*/
    }

    @Test
    public void nodeTest() {
        Node root = new Node("title", "this is title");
        for (int i = 0; i < 3; i++) {
            Node want = new Node("want", "this is want " + i);
            Node benefit = new Node("benefit", "");
            for (int j = 0; j < 3; j++) {
                Node benefit1 = new Node("benefit", "this is benefit " + i + "-" + j);
                benefit.addChildren(benefit1);
            }
            Node example = new Node("example", "this is example " + i);
            want.addChildren(benefit);
            want.addChildren(example);
            root.addChildren(want);
        }
        System.out.println(root.toString());

    }

    ArrayList<Integer> wantSplit;
    ArrayList<Integer> tmp = new ArrayList<>();
    ArrayList<ArrayList<Integer>> groups = new ArrayList<>();

    @Test
    public void wantNodeTest() {
        Integer[] split = {1, 2, 1};
        Integer[] split1 = {1, 4, 4};
        Integer[] split2 = {1, 4, 4, 0};
        Integer[] split3 = {4, 1, 3};
        Integer[] split4 = {4, 1, 3, 4};
        Integer[] split5 = {4, 3, 1, 2};
        Integer[] split6 = {4, 0, 3, 1, 2};
        Integer[] split7 = {4, 0, 0, 3, 1, 2};
        Integer[] split8 = {4, 0, 0, 3, 1, 0, 2, 0};
        wantSplit = new ArrayList<>(Arrays.asList(split));
        //System.out.println(wantSplit);
        tmp = (ArrayList<Integer>) wantSplit.clone();
        getGroups(1, 0, wantSplit);
        Collections.reverse(groups);
        System.out.println("Groups--->" + groups);
        ArrayList<Double> sd = new ArrayList<>();
        double s = 0;
        for (ArrayList<Integer> list : groups) {
            int sum = 0;
            for (int i : list) {
                sum += i;
            }
            double avg = sum / list.size(), dec;
            for (int i = 0; i < list.size(); i++) {
                dec = list.get(i) - avg;
                s += dec * dec;
            }
            s = s / list.size();
            sd.add(s);
            System.out.println(list + " 方差--->" + s);
            s = 0;
        }
        int index = sd.indexOf(Collections.min(sd));
        ArrayList<Integer> nodeList = groups.get(index);
        System.out.println("best is --->" + nodeList);
    }

    public void getGroups(int pre, int start, ArrayList<Integer> target) {
        if (start == target.size() - 1) {
            if (tmp.size() == target.size() - 1)
                groups.add(tmp);
            //System.out.println(tmp);
            tmp = (ArrayList<Integer>) wantSplit.clone();
            return;
        } else {
            if (1 == pre) {
                //System.out.println("start-->"+(start+1));
                getGroups(1, start + 1, target);
                tmp.set(start, tmp.get(start) + tmp.get(start + 1));
                tmp.remove(start + 1);
                //System.out.println(tmp);
                //System.out.println("start-->"+(start+1));
                getGroups(2, start + 1, target);
            } else if (2 == pre) {
                //System.out.println("start-->"+(start+1));
                getGroups(2, start + 1, target);
            }
        }
    }
}
