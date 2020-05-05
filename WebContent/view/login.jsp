<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta http-equiv="Cache-Control" content="no-siteapp" />
<link rel="shortcut icon" href="favicon.ico" />
<link rel="bookmark" href="favicon.ico" />
<link href="h-ui/css/H-ui.min.css" rel="stylesheet" type="text/css" />
<link href="h-ui/css/H-ui.login.css" rel="stylesheet" type="text/css" />
<link href="h-ui/lib/icheck/icheck.css" rel="stylesheet" type="text/css" />
<link href="h-ui/lib/Hui-iconfont/1.0.1/iconfont.css" rel="stylesheet"
	type="text/css" />

<link rel="stylesheet" type="text/css"
	href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">

<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="h-ui/js/H-ui.js"></script>
<script type="text/javascript"
	src="h-ui/lib/icheck/jquery.icheck.min.js"></script>

<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>

<script type="text/javascript">
	$(function() {
		//点击图片切换验证码
		$("#vcodeImg").click(
				function() {
					this.src = "CpachaServlet?method=GetLoginVCode&t="
							+ new Date().getTime();
				});

		//登录
		$("#submitBtn")
				.click(
						function() {
							var username=$("#username").val();
							var password=$("#password").val();
							var vcode=$("#vcode").val();
							if(username==''){
								$.messager.alert("消息提醒", "请填写用户名!", "warning");
								return;
							}
							if(password==''){
								$.messager.alert("消息提醒", "请填写密码!", "warning");
								return;
							}
							if(vcode==''){
								$.messager.alert("消息提醒", "请填写验证码!", "warning");
								return;
							}
							var data = $("#form").serialize();
							$
									.ajax({
										type : "post",
										url : "LoginServlet?method=LoginAction",
										data : data,
										dataType : "json", //返回数据类型
										success : function(data) {
											if("error"==data.type){
												$.messager.alert("消息提醒", data.msg, "warning");
												$("#vcodeImg").click();//切换验证码
												$("input[name='vcode']").val("");//清空验证码输入框
											}else{
												alert("登录成功");
												window.location.href = "SystemServlet?method=toHome";
											}
										}

									});
						});

		//设置复选框
		$(".skin-minimal input").iCheck({
			radioClass : 'iradio-blue',
			increaseArea : '25%'
		});
	})
</script>
<title>登录|图书管理系统</title>
<meta name="keywords" content="图书管理系统">
</head>
<body>

	<div class="header" style="padding: 0;">
		<h2
			style="color: white; width: 400px; height: 60px; line-height: 60px; margin: 0 0 0 30px; padding: 0;">图书管理系统</h2>
	</div>
	<div class="loginWraper">
		<div id="loginform" class="loginBox">
			<form id="form" class="form form-horizontal" method="post">
				<div class="row cl">
					<label class="form-label col-3"><i class="Hui-iconfont">&#xe60d;</i></label>
					<div class="formControls col-8">
						<input id="username" name="username" type="text" placeholder="账户"
							class="input-text size-L">
					</div>
				</div>
				<div class="row cl">
					<label class="form-label col-3"><i class="Hui-iconfont">&#xe60e;</i></label>
					<div class="formControls col-8">
						<input id="password" name="password" type="password" placeholder="密码"
							class="input-text size-L">
					</div>
				</div>
				<div class="row cl">
					<div class="formControls col-8 col-offset-3">
						<input class="input-text size-L" name="vcode" id="vcode" type="text"
							placeholder="请输入验证码" style="width: 200px;"> <img
							title="点击图片切换验证码" id="vcodeImg"
							src="CpachaServlet?method=GetLoginVCode">
					</div>
				</div>

				<div class="mt-20 skin-minimal" style="text-align: center;">
					<div class="radio-box">
						<input type="radio" id="radio-2" name="type" checked value="2" />
						<label for="radio-1">用户</label>
					</div>
					<div class="radio-box">
						<input type="radio" id="radio-3" name="type" value="3" /> <label
							for="radio-2">图书管理员</label>
					</div>
					<div class="radio-box">
						<input type="radio" id="radio-1" name="type" value="1" /> <label
							for="radio-3">超级管理员</label>
					</div>
				</div>

				<div class="row">
					<div class="formControls col-8 col-offset-3">
						<input id="submitBtn" type="button"
							class="btn btn-success radius size-L"
							value="&nbsp;登&nbsp;&nbsp;&nbsp;&nbsp;录&nbsp;"> <a
							href="LoginServlet?method=toRegister"
							class="btn btn-success radius size-L" style="margin-left: 165px">&nbsp;注&nbsp;&nbsp;&nbsp;&nbsp;册&nbsp;</a>
					</div>
				</div>
			</form>
		</div>
	</div>
	<div class="footer">Copyright &nbsp; 525@YPH</div>


</body>
</html>