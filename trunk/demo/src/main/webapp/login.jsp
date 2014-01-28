<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<script type="text/javascript" src="jquery-1.8.0.min.js"></script>
<script type="text/javascript">

var urlOfGetLt = 'https://sso.slimsmart.cn:8443/cas/login?get-lt=true&n='+new Date().getTime();

function flightHandler(json){
	if(json){
		   $("#lt").val(json.lt);
           $("#execution").val(json.execution);
	}
};

$(document).ready(function() {
	$.ajax({
	    type: "get",
	    async: false,
	    url: urlOfGetLt,
	    dataType: "jsonp",
	    jsonp: "callback",
	    jsonpCallback:"flightHandler",
	    success: function(json){
	    	console.log(json);
	    },
	    error: function(){
	        alert("无法获取登录key");
	    }
	});
});
</script>
</head>
<body>
	<form id="myLoginForm" action="https://sso.slimsmart.cn:8443/cas/login" method="get">
		<input type="hidden" name="_eventId" value="submit" />
		<input type="hidden" name="execution" value="" id="execution" />
		<input type="hidden"name="warn" value="true" />
		
		<input type="hidden" name="login-action" value="http://www.slimsmart.cn:8888/sso-demo/login.jsp" />
		<table>
			<tr>
				<td>用户名:</td>
				<td><input type="text" value="" name="username"></td>
			</tr>
			<tr>
				<td>密码:</td>
				<td><input type="text" value="" name="password"></td>
			</tr>
			<tr>
				<td>登录票据:</td>
				<td><input type="text" name="lt" id="lt" value=""></td>
			</tr>
			<tr>
				<td>服务:</td>
				<td><input type="text" name="service"
					value="http://www.slimsmart.cn:8888/sso-demo"></td>
			</tr>
			<tr>
			<td>验证码</td>
			<td>
			<input type="text" name ="authcode" /> <img src="https://sso.slimsmart.cn:8443/cas/captcha.jpg" alt="" />
			</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><input type="submit" /></td>
			</tr>
		</table>
	</form>
</body>
</html>
