<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>登录</title>
</head>
<body>
	<form id="myLoginForm" action="https://sso.slimsmart.cn:8443/cas/remoteLogin" method="POST">
		<input type="hidden" name="service" value="http://www.slimsmart.cn:8888/demo" />
		<input type="hidden" name="loginUrl" value="http://www.slimsmart.cn:8888/demo/login.jsp"  />
		 <input type="hidden" name="submit" value="true" />
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
				<td>验证码:</td>
				<td><input type="text" value="" name="authcode"><img src="https://sso.slimsmart.cn:8443/cas/captcha.jpg" alt="" /></td>
			</tr>
			<tr>
				<td align="right" colspan="2"><input type="submit" /></td>
			</tr>
		</table>
	</form>
</body>
</html>
