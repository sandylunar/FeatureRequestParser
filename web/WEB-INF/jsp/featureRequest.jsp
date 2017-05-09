<%--
  Created by IntelliJ IDEA.
  User: zzyo
  Date: 2017/3/16
  Time: 21:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>FRA</title>
    <!-- Bootstrap -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <%--<!--引入wangEditor.css-->
    <link rel="stylesheet" type="text/css" href="/resources/css/wangEditor.min.css">--%>
    <link rel="stylesheet" href="/resources/font-awesome-4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="/resources/css/jquery.treeview.css"/>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="/resources/js/bootstrap.min.js"></script>
    <%--<script type="text/javascript" src="/resources/js/wangEditor.js"></script>--%>
    <script type="text/javascript" src="/resources/js/jquery.treeview.js"></script>

    <script type="text/javascript" charset="utf-8" src="/resources/js/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="/resources/js/ueditor.all.js"></script>
    <!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
    <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
    <script type="text/javascript" charset="utf-8" src="/resources/lang/en/en.js"></script>
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
<%--Chrome垂直居中--%>
<body style="display: flex;flex-direction: column;justify-content: center;">
<%--FireFox IE 垂直居中--%>
<%--<body style="position: absolute;top: 50%;-webkit-transform: translateY(-50%)">--%>
<div class="container">
    <div class="row" style="border: thin solid #5bc0de;padding:20px 30px;border-radius: 15px">
        <div class="col-md-6">
            <h1>Input</h1>
            <form>
                <input type="hidden" name="dataType" id="dataType" value="tree">
                <div class="form-group">
                    <label for="name">What’s your system’s name and alias:</label>
                    <input type="text" class="form-control" id="name" name="name">
                    <label for="name">e.g.,phpmyadmin, pma</label>
                </div>
                <div class="form-group">
                    <label for="FRTitle">FR Title</label>
                    <input type="text" class="form-control" id="FRTitle" name="FRTitle">
                </div>
                <div class="form-group">
                    <label for="container">FR Description</label>
                    <%--<textarea id="FRDes" style="height:200px" name="FRDes"></textarea>--%>
                    <!-- 加载编辑器的容器 -->
                    <script id="container" name="content" type="text/plain"></script>
                </div>
                <div style="float: right">
                    <a href="#sample" onclick="showSample()" style="margin-right: 15px;font-size: 17px">Sample Feature Request</a>
                    <button class="btn btn-info" type="button" onclick="confirm()" style="font-size: 17px">Submit</button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <div class="form-group">
                <h1>Output</h1>
                <%--<textarea class="form-control" id="output" style="height: 430px"></textarea>--%>
                <div>
                    <ul id="tree" class="filetree">
                    </ul>
                </div>

            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var ue = UE.getEditor('container', {
        initialFrameHeight: 300,
        initialFrameWeight: 524,
        enableContextMenu: false,
        elementPathEnabled: false,
        wordCount: false
    });

    function confirm() {
        $('ul').html("");
        var dataType = $('#dataType').val();
        var name = $('#name').val();
        var title = $('#FRTitle').val();
        if (title == null || title == undefined || title == '')
        {alert("Please input FR Title!");return;}
        //获取html内容，返回: <p>hello</p>
        var html = ue.getContent();
        if (html == null || html == undefined || html == '')
        {alert("Please input FR Description!");return;}
        //获取纯文本内容，返回: hello
        var txt = ue.getContentTxt();
        //alert(html);
        /*$.post('index.do', {
         name: name,
         FRTitle: title,
         FRDes: html
         }, function (data) {
         //alert(data);
         alert("Block数目："+data.bNum+"\n\nSentence数目："+data.sNum);
         $('ul').html(data.output);
         $("#tree").treeview();
         });*/
        $.ajax({
            url: 'featureRequest.html',
            type: "POST",
            data: {name: name, FRTitle: title, FRDes: html, dataType: dataType},
            //timeout: 30000,
            dataType: "json",
            success: function (data) {
                //alert("Block数目：" + data.bNum + "\n\nSentence数目：" + data.sNum);
                $('ul').html(data.output);
                $("#tree").treeview({
                    collapsed: data.collapsed
                });
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //alert(XMLHttpRequest.status);
                //alert(XMLHttpRequest.readyState);
                //alert(textStatus);
                //alert("some errors...");
            }
        });
    }

    function showSample(){
        $('#name').val('Phpmyadmin');
        $('#FRTitle').val('option to disable JS-Windows for errors');
        ue.setContent('<p>Please add an option to redisplay the "old" error-div if something failed.</p>\
<p>If you have a long (text) sql-query failing, you get an totally undersized js-error dialog which is hard to read and closes on an accidential click.</p>\
<p>I suggest: add an div on top of the page, containing the error, have the query-section (with inline) under it.</p>\
<p>Having this, you can see the error on full monitor width, second you can change the query to make it work and maybe you can put a structured output above it.</p>\
<p>Sometimes only a quote or brace is missing, which you must find.</p>\
<p>Best Regards,</p>\
<p>Thanks!</p>');
    }
</script>

<%--<script type="text/javascript">

    var editor = new wangEditor('FRDes');
    // 关闭菜单栏fixed
    editor.config.menuFixed = false;
    editor.config.menus = $.map(wangEditor.config.menus, function (item, key) {
        if (item === 'source') {
            return null;
        }
        if (item === 'underline') {
            return null;
        }
        if (item === 'italic') {
            return null;
        }
        if (item === 'strikethrough') {
            return null;
        }
        if (item === 'eraser') {
            return null;
        }
        if (item === 'bgcolor') {
            return null;
        }
        if (item === 'emotion') {
            return null;
        }
        if (item === 'fontfamily') {
            return null;
        }
        if (item === 'fontsize') {
            return null;
        }
        if (item === 'fontsize') {
            return null;
        }
        if (item === 'alignleft') {
            return null;
        }
        if (item === 'aligncenter') {
            return null;
        }
        if (item === 'alignright') {
            return null;
        }
        if (item === 'img') {
            return null;
        }
        if (item === 'video') {
            return null;
        }
        if (item === 'location') {
            return null;
        }
        if (item === 'fullscreen') {
            return null;
        }
        return item;
    });

    editor.create();

    function confirm() {
        $('ul').html("");
        // 获取编辑器区域完整html代码
        var html = editor.$txt.html();
        // 获取编辑器纯文本内容
        var text = editor.$txt.text();
        // 获取格式化后的纯文本
        var formatText = editor.$txt.formatText();
        var name = $('#name').val();
        var title = $('#FRTitle').val();
        $.post('index.do', {
            name: name,
            FRTitle: title,
            FRDes: html
        }, function (data) {
            $('ul').html(data);
            $("#tree").treeview();
        })
    }
</script>--%>
</body>
</html>