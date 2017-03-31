package main.java.parse;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.*;
import main.java.bean.FeatureRequestOL;
import main.java.bean.Replacement;
import main.java.bean.Sentence;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzyo on 2017/3/17.
 */
public class Parser {

    private Map<String, Replacement> replacementMap = new HashMap<>();
    private ArrayList<ArrayList<Integer>> blocks = new ArrayList<>();
    private ArrayList<Sentence> sentences = new ArrayList<>();

    public Parser() {
    }

    public String parseCode(String origin) {
        Pattern pattern = Pattern.compile("<pre\\sclass=\"brush:(.+?);toolbar:false\">(.+?)</pre>");
        Matcher matcher = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        int codeIndex = 0;
        while (matcher.find()) {
            String codeType = matcher.group(1);
            String code = matcher.group(2);
            String replace = "<CODE-" + codeType.toUpperCase() + codeIndex + ">";
            String token = "<CODE-" + codeType.toUpperCase() + ">";
            System.out.println(token + "  " + code);
            matcher.appendReplacement(sb, replace + "<BLOCK-END>");
            Replacement replacement = new Replacement();
            replacement.setOrigin(code);
            replacement.setReplacement(token);
            this.replacementMap.put(replace, replacement);
            codeIndex++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /***
     *
     * URL的标准格式           >=     protocol :// hostname[:port] / path / [;parameters][?query]#fragment
     *
     * 1.字符串附加超链接       >=	    原字符串<HTTP-LINK>
     * 2.URL无超链接           >=	    <HTTP-LINK>
     * 3.URL附加自身超链接      >=	    <HTTP-LINK>
     */

    public String parseLink(String origin) {
        String urlRegex = "((http|ftp|https)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        Pattern pattern = Pattern.compile("<a href=\"(.+?)\".*?>(.+?)</a>");
        Matcher matcher = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        int linkIndex = 0;
        while (matcher.find()) {
            String href = matcher.group(1);
            String text = matcher.group(2);
            String replace = "";
            if (text.matches(urlRegex)) {
                replace = "<LINK-HTTP" + linkIndex + ">";
            } else {
                replace = text + "<LINK-HTTP" + linkIndex + ">";
            }
            System.out.println(replace + "    " + href);
            String token = "<LINK-HTTP>";
            matcher.appendReplacement(sb, replace);
            Replacement replacement = new Replacement();
            replacement.setOrigin(text + "<" + href + ">");
            replacement.setReplacement(token);
            this.replacementMap.put("<LINK-HTTP" + linkIndex + ">", replacement);
            linkIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();
        /*sb.setLength(0);
        //　纯文本url
        pattern = Pattern.compile(urlRegex);
        matcher = pattern.matcher(origin);
        while (matcher.find()) {
            String url = matcher.group(0);
            String replace = "<LINK-HTTP" + linkIndex + ">";
            System.out.println(replace + "    " + url);
            matcher.appendReplacement(sb, replace);
            Replacement replacement = new Replacement();
            replacement.setOrigin(url);
            replacement.setReplacement(replace.replaceFirst("\\d+", ""));
            this.replacementMap.put(replace, replacement);
            linkIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();*/
        return origin;
    }

    public String parseList(String origin) {
        //origin = origin.replaceAll("<li>(.+)?<\\/li>", "<list>");
        int lIndex = 0;
        Pattern pattern = Pattern.compile("<li><p>(.+?)<\\/p><\\/li>");
        Matcher matcher = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String str = matcher.group(0);
            String list = matcher.group(1);
            System.out.println(str + "    " + list);
            String token = "<LIST" + lIndex + ">";
            matcher.appendReplacement(sb, token);
            Replacement replacement = new Replacement();
            replacement.setOrigin(list);
            this.replacementMap.put(token, replacement);
            lIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();
        return origin;
    }

    public String parseHtmlToText(String origin) {
        origin = origin.replaceAll("<(\\w+)><\\/\\1>", "");
        origin = origin.replaceAll("<p><br><\\/p>", "");
        origin = origin.replaceAll("<p><br/></p>", "");
        origin = origin.replaceAll("<br\\/?>", "");
        // list 结束即BLOCK结束
        origin = origin.replaceAll("(<\\/[ou]l>)", "$0<BLOCK-END>");     // 记得去掉换行
        //  </p> p结束即BLOCK结束
        //　</p> 后是list/code 不能替换BLOCK-END
        origin = origin.replaceAll("<\\/p>(?!(<[ou]l)|(<CODE-))", "<\\/p><BLOCK-END>");      // 记得去掉换行
        origin = origin.replaceAll("<([a-z]+?).*?>([.\\s\\S]+?)<\\/\\1>", "$2");
        origin = origin.replaceAll("&nbsp;", " ");
        origin = origin.replaceAll("&lt;", "<");
        origin = origin.replaceAll("&gt;", ">");
        origin = origin.replaceAll("&#39;", "'");
        origin = origin.replaceAll("&quot;", "\"");
        return origin;
    }

    public String parseQuote(String origin) {
        String regEx = "([\"']).*?\\1";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        int quoteIndex = 0;
        while (matcher.find()) {
            String quote = matcher.group(0);
            String token = "<QUOTE" + quoteIndex + ">";
            //type = Matcher.quoteReplacement(type);
            System.out.println(quote + "    " + token);
            Replacement replacement = new Replacement();
            replacement.setOrigin(quote);
            replacement.setReplacement("<QUOTE>");
            this.replacementMap.put("<QUOTE" + quoteIndex + ">", replacement);
            matcher.appendReplacement(sb, token);
            quoteIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();
        return origin;
    }

    public String parseEmail(String origin) {
        String regEx = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        int emailIndex = 0;
        while (matcher.find()) {
            String email = matcher.group(0);
            String token = "<EMAIL" + emailIndex + ">";
            System.out.println(email + "    " + token);
            Replacement replacement = new Replacement();
            replacement.setOrigin(email);
            replacement.setReplacement("<EMAIL>");
            this.replacementMap.put(token, replacement);
            matcher.appendReplacement(sb, token);
            emailIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();
        return origin;
    }

    public String parseShort(String origin) {
        String[][] shorts = {{"\\be\\.g(?=\\s)", "\\be\\.g\\.(?=\\s)", "\\beg(?=\\s)", "\\beg\\.(?=\\s)", "\\bi\\.e\\.(?=\\s)", "\\bi\\.e(?=\\s)"}, {"\\.NET(.?)"}, {"\\bImo(?=\\s)"}};
        String[] replace = {"for example", "dotNET", "in my opinion"};
        StringBuffer sb = new StringBuffer();
        int shortIndex = 0;
        for (int i = 0; i < shorts.length; i++) {
            for (int j = 0; j < shorts[i].length; j++) {
                Pattern pattern = Pattern.compile(shorts[i][j]);
                Matcher matcher = pattern.matcher(origin);
                while (matcher.find()) {
                    String str = matcher.group(0);
                    String token = "<" + replace[i] + shortIndex + ">";
                    System.out.println(str + "  " + token);
                    Replacement replacement = new Replacement();
                    replacement.setOrigin(str);
                    replacement.setReplacement(replace[i]);
                    this.replacementMap.put(token, replacement);
                    matcher.appendReplacement(sb, token);
                    shortIndex++;
                }
                matcher.appendTail(sb);
                origin = sb.toString();
                sb.setLength(0);
            }
        }
        return origin;
    }

    public String parseFile(String origin) {
        String[] fileTypes = {"php", "sql", "java", "rm", "htaccess", "jar", "py", "exe"};
        String regEx = "";
        StringBuffer sb = new StringBuffer();
        int fileIndex = 0;
        for (int index = 0; index < fileTypes.length; index++) {
            // parse unix path file(full path and relative path)
            regEx = "((((\\.){0,2}\\/)*((((\\w*-|\\w*\\.)*\\w*)\\/)*((\\w*-|\\w*\\.)*\\w*)))|" +
                    // windows path(full path and relative path)
                    "(([C-Z]:|(\\.){0,2}\\\\)?(((\\w*-|\\w*\\.)*\\w*)\\\\)*((\\w*-|\\w*\\.)*\\w*)))\\." + fileTypes[index]/* + "(?!\\.[a-z])"*/;
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(origin);
            while (matcher.find()) {
                String file = matcher.group(0);
                String token = fileTypes[index].toUpperCase();
                token = "<FILE-" + token + fileIndex + ">";
                System.out.println(file + "  " + token);
                Replacement replacement = new Replacement();
                replacement.setOrigin(file);
                replacement.setReplacement("<FILE-" + fileTypes[index].toUpperCase() + ">");
                this.replacementMap.put(token, replacement);
                matcher.appendReplacement(sb, token);
                fileIndex++;
            }
            matcher.appendTail(sb);
            origin = sb.toString();
            sb.setLength(0);
        }
        return origin;
    }

    /**
     * Unix 绝对路径 | 相对路径必须以（./）（../）开头
     * Windows 绝对路径 | 相对路径必须以（.\）（..\）开头
     */

    public String parsePath(String origin) {
        // parse unix path(full path and relative path)     windows path(full path and relative path)    package path
        String regEx = "(?<![\\<(http)])" +
                "(((\\.){0,2}\\/(((\\w*-|\\w*\\.)*\\w+)\\/)*((\\w*-|\\w*\\.)*\\w*))|" +
                "(((([C-Z]:)|(\\.){0,2})\\\\)(((\\w*-|\\w*\\.)*\\w+)\\\\)*((\\w*-|\\w*\\.)*\\w*))|" +
                "(([a-zA-Z]+\\.){2,}[a-zA-Z]+(?=[\\s\\.])))";
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(origin);
        int pathIndex = 0;
        while (matcher.find()) {
            String path = matcher.group(0);
            String token = "<PATH" + pathIndex + ">";
            //type = Matcher.quoteReplacement(type);
            System.out.println(path + "   " + token);
            Replacement replacement = new Replacement();
            replacement.setOrigin(path);
            replacement.setReplacement("<PATH>");
            this.replacementMap.put(token, replacement);
            matcher.appendReplacement(sb, token);
            pathIndex++;
        }
        matcher.appendTail(sb);
        origin = sb.toString();
        return origin;
    }

    public ArrayList<String> parseBlock(String origin) {
        ArrayList<String> blockList = new ArrayList<>(Arrays.asList(origin.split("<BLOCK-END>")));
        for (int i = 0; i < blockList.size(); i++) {
            String tmp = blockList.get(i);
            tmp = tmp.replaceAll("(?<=<)\\$", "");
            tmp = tmp.replaceAll("\\$(?=\\d{0,2}>)", "");
            blockList.set(i, tmp);
        }
        for (String block : blockList) {
            System.out.println("block--->" + block);
        }
        System.out.println("------------------------------------------------");
        return blockList;
    }

    public void parseSentences(ArrayList<String> blocks) {
        int sIndex = 0, bIndex = 0;
        for (String str : blocks) {
            ArrayList<Integer> block = new ArrayList<>();
            this.blocks.add(block);
            /*Reader reader = new StringReader(str);
            DocumentPreprocessor dp = new DocumentPreprocessor(reader);
            ArrayList<String> sentences = new ArrayList<>();
            for (List<HasWord> words : dp) {
                String sentence = "";
                for (HasWord word : words) {
                    sentence = sentence.concat(" " + word.word());
                }
                sentences.add(sentence);
            }*/
            //                                人名缩写不分句               后有空格或大写字母分句
            String[] sTmp = str.split("(?<!\\s[A-Z])((\\.|\\?|\\!)((\\s*)|(?=[A-Z])))");
            ArrayList<String> sentences = new ArrayList<>(Arrays.asList(sTmp));
            for (String sen : sentences) {
                System.out.println(sen);
            }
            // 处理孤立的LIST,CODE
            for (int i = 0; i < sentences.size(); i++) {
                String sen = sentences.get(i);
                if (sen.matches("^\\s*<LIST\\d+.*")) {
                    sentences.set(i - 1, sentences.get(i - 1).concat(sen));
                    sentences.remove(i);
                } else if (sen.matches("^\\s*<CODE-.*")) {
                    sentences.set(i - 1, sentences.get(i - 1).concat(sen));
                    sentences.remove(i);
                }
            }
            // -------------------   parse jar path     ------------------------------
            // -------------------   parse jar path     ------------------------------
            for (String sen : sentences) {
                String tmp = sen;
                Sentence sentence = new Sentence();
                Pattern pattern = Pattern.compile("<LIST\\d{1,2}>");
                Matcher matcher = pattern.matcher(sen);
                while (matcher.find()) {
                    String token = matcher.group(0);
                    Sentence sList = new Sentence();
                    String origin = this.replacementMap.get(token).getOrigin();
                    sList.setOrigin(origin);
                    sList.setResult(origin);
                    sentence.addItemList(sList);
                    tmp = tmp.replace(token, "\n* " + origin);
                }
                sentence.setOrigin(tmp);
                sentence.setResult(sen);
                block.add(sIndex);
                this.blocks.set(bIndex, block);
                this.sentences.add(sentence);
                sIndex++;
            }
            bIndex++;
        }
    }

    public static List<CoreLabel> getRawWords(String text) {
        TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(text));
        List<CoreLabel> rawWords = tok.tokenize();
        return rawWords;
    }

    public static String[] getTokens(String content) {
        List<CoreLabel> words = getRawWords(content);
        String[] results = new String[words.size()];
        for (int i = 0; i < words.size(); i++)
            results[i] = words.get(i).word();
        return results;
    }

    public static int getTokenIndex(String str, String token) {
        //System.out.println("Str is--->" + str);
        String[] results = getTokens(str);
        for (int i = 0; i < results.length; i++) {
            //System.out.println("Result is--->" + results[i] + "Token is--->" + token);
            token = getTokens(token)[0];
            if (token.equals(results[i])) {
                //System.out.println("TokenIndex is--->" + i);
                return i;
            }
        }
        return -1;
    }

    public Sentence updateSentence(Sentence sentence) {
        StringBuffer sb = new StringBuffer();
        String origin = sentence.getOrigin();
        String tmp = origin;
        int flag = 0;
        //Pattern pattern = Pattern.compile("<\\$.+?\\$\\d{0,2}>");
        Pattern pattern = Pattern.compile("<[a-zA-Z-\\s]+\\d{1,2}>");
        Matcher matcher = pattern.matcher(origin);
        while (matcher.find()) {
            flag = 1;
            String token = matcher.group(0);
            Replacement replacement = this.replacementMap.get(token);
            int tokenIndex = getTokenIndex(tmp, token);
            replacement.setIndexOfReplace(tokenIndex);
            sentence.addReplacements(replacement);
            if (token.matches("<LINK-HTTP\\d{1,2}>")) {
                if (replacement.getOrigin().matches("((http|ftp|https)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?<.+?>")) {
                    matcher.appendReplacement(sb, replacement.getOrigin().replaceFirst("<.+?>", ""));
                    System.out.println(replacement.getOrigin().replaceFirst("<.+?>", ""));
                } else {
                    matcher.appendReplacement(sb, "");
                }
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.getOrigin()));
            }
        }
        if (flag == 1) {
            matcher.appendTail(sb);
            sentence.setOrigin(sb.toString());
            sb.setLength(0);
        }
        String result = sentence.getResult();
        result = result.replaceAll("<LIST\\d{1,2}>", "");
        result = result.replaceAll("(?<=[a-zA-Z])\\d{0,2}>", ">");
        sentence.setResult(result);
        return sentence;
    }

    public void parseReplacement() {
        for (Sentence sen : this.sentences) {
            System.out.println(sen.getResult());
        }
        // change sentence.origin to origin text
        //set Replacement List in Sentence
        // set index of replacement
        for (int i = 0; i < this.sentences.size(); i++) {
            Sentence sentence = this.sentences.get(i);
            this.sentences.set(i, updateSentence(sentence));
        }
    }

    public void parseExe(String raw) {
        String tmp = parseCode(raw);
        tmp = parseLink(tmp);
        tmp = parseList(tmp);
        tmp = parseHtmlToText(tmp);
        tmp = parseEmail(tmp);
        tmp = parseQuote(tmp);
        tmp = parseShort(tmp);
        tmp = parseFile(tmp);
        tmp = parsePath(tmp);
        ArrayList<String> blocks = parseBlock(tmp);
        parseSentences(blocks);
        parseReplacement();
    }

    public FeatureRequestOL getFR(String name, String title, String des) {
        //fr.setBlockItems(parseExe(des));
        parseExe(des);
        FeatureRequestOL fr = new FeatureRequestOL(name, title, this.blocks, this.sentences);
        return fr;
    }

    public int getBlockNum(){
        return this.blocks.size();
    }

    public int getSenNum(){
        return this.sentences.size();
    }

    public String printResult(FeatureRequestOL fr) {
        String result = "";
        result = result.concat("\n----------------   parsing start   -----------------------------\n");
        int sIndex = 0;
        ArrayList<Sentence> sentences = fr.getFullSentences();
        ArrayList<ArrayList<Integer>> blocks = fr.getBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            result = result.concat("\n------------------   block " + i + " start   --------------------------------\n\n");
            for (int j = 0; j < blocks.get(i).size(); j++) {
                result = result.concat("\n--------------------   sentence " + blocks.get(i).get(j) + " start   -------------------------\n");
                result = result.concat("\nOrigin is--->" + sentences.get(sIndex).getOrigin() + "\n");
                result = result.concat("\nResult is--->" + sentences.get(sIndex).getResult() + "\n");
                result = result.concat("\nReplace is--->" + sentences.get(sIndex).getReplacements().toString() + "\n");
                for (Sentence sentence1 : sentences.get(sIndex).getItemLists()) {
                    result = result.concat("\n        ---------------------------   list sentence start   --------------------\n");
                    result = result.concat("\n        Origin is--->" + sentence1.getOrigin() + "\n");
                    result = result.concat("\n        Result is--->" + sentence1.getResult() + "\n");
                    result = result.concat("\n        Replace is--->" + sentence1.getReplacements().toString() + "\n");
                    result = result.concat("\n        ----------------------------   list sentence end   ---------------------\n");
                }
                sIndex++;
                result = result.concat("\n-----------------------   sentence " + blocks.get(i).get(j) + " end   ----------------------------\n");
            }
            result = result.concat("\n------------------------   block " + i + " end   -----------------------------\n\n");
        }
        return result;
    }



}
