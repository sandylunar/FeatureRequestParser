<%--
  Created by IntelliJ IDEA.
  User: zzyo
  Date: 17-5-2
  Time: 下午3:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/resources/font-awesome-4.7.0/css/font-awesome.min.css">
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="/resources/js/bootstrap.min.js"></script>
    <title>Research</title>
    <script>
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "https://hm.baidu.com/hm.js?a20f9aa055af98f694fc83a3930c167a";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
</head>
<body style="background-color: #eeeeee">
<div class="row">
    <div class="col-md-2">
        <div style="margin-top:170px;text-align: right;padding-right: 10px">
            <div id="nav">
                <ul class="nav nav-pills nav-stacked">
                    <li role="presentation" style="font-size: 20px"><a href="#Project Summary">Project Summary</a></li>
                    <%--<li role="presentation"><a href="#People">People</a></li>--%>
                    <li role="presentation" style="font-size: 20px"><a href="#Subjects">Subjects</a></li>
                    <li role="presentation" style="font-size: 20px"><a href="#Feature Request Analyzer Tool">Feature Request Analyzer Tool</a></li>
                    <li role="presentation" style="font-size: 20px"><a href="#Evaluation">Evaluation</a></li>
                    <li role="presentation" style="font-size: 20px"><a href="#Download">Download</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="col-md-10" style="border-left: 1px solid #00b7ee;background-color: #FFFFFF">
        <div style="padding-left:25px;padding-right: 50px">
            <h1 style="margin-top: 30px">Understanding Feature Requests by Leveraging Heuristic and Semantic
                Analysis</h1>
            <p style="font-size: 25px;font-weight: 800">
                Online System:
                <a href="featureRequest.html" target="_blank">Feature Request Analyzer</a>
            </p>
            <hr style="background-color: #bbd1d4; border: none; height: 2px;">

            <h2 style="margin-top: 30px"><a name="Project Summary">Project Summary</a></h2>
            <p style="font-size: large">New features are typically introduced into the issue tracking systems
                during
                system
                evolution, especially for projects involve more stakeholders and distributed. By structuring the
                contents of
                feature requests into content-ware category will benefit further communication and content
                analysis. In
                this
                paper, we category the contents of feature request into Intent, Explanation, Benefit, Drawback,
                Example,
                and
                Trivialness. We propose a set of heuristic rules along with semantic metrics to determine the
                category
                of
                each sentence from the feature requests. The results show that the combination of heuristic and
                semantic
                analysis can reach a relatively high performance in the task of understanding feature requests
                compared
                to
                linguistic machine learning with the semantic features. We also compare the linguistic machine
                learning
                with
                the semantic features to the traditional linguistic machine learning. The results show that the
                semantic
                features can largely improve the classification performances as well.</p>
            <p style="font-size: large">The paper submitted to ASE 2017.</p>

            <%--<h2 style="margin-top: 30px"><a name="People">People</a></h2>
            <p style="font-size: large">
                Lin Shi (<a href="http://itechs.iscas.ac.cn" target="_blank">iTechs</a>,<a
                    href="http://www.iscas.ac.cn"
                    target="_blank">ISCAS</a>),
                Celia Chen(<a href="http://csse.usc.edu/" target="_blank">USC</a>),
                Qing Wang(<a href="http://itechs.iscas.ac.cn" target="_blank">iTechs</a>, <a
                    href="http://www.iscas.ac.cn"
                    target="_blank">ISCAS</a>),
                MingShu Li (<a href="http://itechs.iscas.ac.cn" target="_blank">iTechs</a>, <a
                    href="http://www.iscas.ac.cn"
                    target="_blank">ISCAS</a>),
                and Barry Boehm(<a href="http://csse.usc.edu/" target="_blank">USC</a>)</p>--%>

            <h2 style="margin-top: 30px"><a name="Subjects">Subjects</a></h2>
            <ul style="margin-top: 15px">
                <li style="font-size: large"><a href="https://www.phpmyadmin.net/">Phpmyadmin</a>(PMA: A web-based MySQL management tools written in
                    PHP.
                </li>
                <li style="font-size: large"><a href="https://www.mopidy.com/">Mopidy</a>: An extensible music server written in Python.</li>
                <li style="font-size: large"><a href="http://activemq.apache.org/">Activemq</a>: An open source message broker written in Java.</li>
                <li style="font-size: large"><a href="http://www.eclipse.org/aspectj/">AspectJ</a>: An aspect-oriented programming (AOP) extension created at
                    PARC for
                    the
                    Java programming language.
                </li>
                <li style="font-size: large"><a href="http://hibernate.org/orm/">Hibernate ORM</a>: An object-relational mapping tool for the Java
                    programming
                    language, written in Java.
                </li>
                <li style="font-size: large"><a href="http://www.eclipse.org/swt/">SWT</a>: A UI toolkit used by the Eclipse Platform and most other
                    Eclipse
                    projects
                    written in Java.
                </li>
                <li style="font-size: large"><a href="http://logging.apache.org/log4j/1.2/">Log4j</a>: A Java-based logging utility.</li>
                <li style="font-size: large"><a href="http://hadoop.apache.org/docs/r1.2.1/hdfs_design.html">HDFS</a>: The primary storage system used by Hadoop applications.</li>
                <li style="font-size: large"><a href="http://archiva.apache.org/index.cgi">Archiva</a>: An extensible repository management software written in
                    Java.
                </li>
            </ul>
            <table class="table table-bordered" style="margin-top: 15px">
                <thead>
                <tr>
                    <th></th>
                    <th>PMA</th>
                    <th>Mopidy</th>
                    <th>Activemq</th>
                    <th>AspectJ</th>
                    <th>ORM</th>
                    <th>SWT</th>
                    <th>Log4j</th>
                    <th>HDFS</th>
                    <th>archiva</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <th scope="row">Community</th>
                    <td>github</td>
                    <td>github</td>
                    <td>apache</td>
                    <td>eclipse</td>
                    <td>hibernate</td>
                    <td>eclipse</td>
                    <td>apache</td>
                    <td>apache</td>
                    <td>apache</td>
                </tr>
                <tr>
                    <th scope="row">Domain</th>
                    <td>database tool</td>
                    <td>music server</td>
                    <td>message broker</td>
                    <td>programming</td>
                    <td>mapping tool</td>
                    <td>UI toolkit</td>
                    <td>logging</td>
                    <td>storage</td>
                    <td>repository tool</td>
                </tr>
                <tr>
                    <th scope="row">#FRs</th>
                    <td>1740</td>
                    <td>312</td>
                    <td>486</td>
                    <td>472</td>
                    <td>778</td>
                    <td>1540</td>
                    <td>177</td>
                    <td>402</td>
                    <td>137</td>
                </tr>
                <tr>
                    <th scope="row">#selected FRs</th>
                    <td>57</td>
                    <td>62</td>
                    <td>62</td>
                    <td>96</td>
                    <td>82</td>
                    <td>44</td>
                    <td>59</td>
                    <td>98</td>
                    <td>42</td>
                </tr>
                <tr>
                    <th scope="row">#Sentences</th>
                    <td>193</td>
                    <td>189</td>
                    <td>167</td>
                    <td>362</td>
                    <td>299</td>
                    <td>147</td>
                    <td>249</td>
                    <td>394</td>
                    <td>112</td>
                </tr>
                </tbody>
            </table>

            <h2 style="margin-top: 30px"><a name="Feature Request Analyzer Tool">Feature Request Analyzer
                Tool:</a></h2>
            <p style="font-size: 20px;font-weight: 500">
                Online System:
                <a href="featureRequest.html" target="_blank">Feature Request Analyzer</a>
            </p>
            <img src=/resources/picturces/FRAT.png>

            <h2 style="margin-top: 30px"><a name="Evaluation">Evaluation</a></h2>
            <img src=/resources/picturces/Eva1.png width="600px">
            <img src=/resources/picturces/Eva2.png width="1000px" height="792px">

            <h2 style="margin-top: 30px"><a name="Download">Download</a></h2>
            <table class="table">
                <tbody>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> activemq.txt (21.4k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/activemq.txt download=""><i class="fa fa-download"
                                                                                  aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> archiva.txt (13.3k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/archiva.txt download=""><i class="fa fa-download"
                                                                                 aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> aspectj.txt (47.7k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/aspectj.txt download=""><i class="fa fa-download"
                                                                                 aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> hdfs.txt (51.3k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/hdfs.txt download=""><i class="fa fa-download"
                                                                              aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> hibernate.txt (35.0k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/hibernate.txt download=""><i class="fa fa-download"
                                                                                   aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> log4j.txt (30.9k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/log4j.txt download=""><i class="fa fa-download"
                                                                               aria-hidden="true"></i></a></td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> mopidy.txt (22.1k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/mopidy.txt download=""><i class="fa fa-download"
                                                                                aria-hidden="true"></i></a></td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> pma.txt (22.6k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/pma.txt download=""><i class="fa fa-download"
                                                                             aria-hidden="true"></i></a>
                    </td>
                </tr>
                <tr>
                    <td><i class="fa fa-folder" aria-hidden="true"></i> swt.txt (16.6k)</td>
                    <td>May 2, 2017, 19:52 PM</td>
                    <td>
                        <mark>V1.0</mark>
                    </td>
                    <td><a href=/resources/uploadFile/swt.txt download=""><i class="fa fa-download"
                                                                             aria-hidden="true"></i></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <%--<div class="col-md-1"></div>--%>
</div>
<script>
    /*    window.onload=function(){
     //获取待定位的元素
     var suspendNavigation=document.getElementById("nav");
     window.onscroll=function(){
     //alert("test1");
     //alert(suspendNavigation.style.top);
     //alert(document.body.scrollTop);
     //alert(document.documentElement.scrollTop);
     //suspendNavigation.style.top=document.body.scrollTop+"px";
     suspendNavigation.style.top=(document.documentElement.scrollTop||document.body.scrollTop)+"px";

     }
     }*/
    var nav = document.getElementById("nav");
    var _height = nav.offsetWidth;
    window.onscroll = function(){
        var _top = document.body.scrollTop || document.documentElement.scrollTop;//兼容
        if(_top>=170){
            nav.style.position = "fixed";
            nav.style.top = 0 +"px";
            nav.style.width = _height+"px";
        }else{
            nav.style.position = "absolute";
            nav.style.top = 170+"px";
            nav.style.width = _height+"px";
        }
    }
</script>
</body>
</html>
