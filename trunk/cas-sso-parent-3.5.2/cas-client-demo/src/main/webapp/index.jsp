<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>登录成功</title>
</head>
<body>
	<div class="welcome">
		您好：<%=request.getRemoteUser()%></div>
	<div id="logout">
		<a
			href="https://sso.slimsmart.cn:8443/cas/remoteLogout?service=http://www.slimsmart.cn:8888/demo">单点登出</a>
	</div>
</body>
</html>