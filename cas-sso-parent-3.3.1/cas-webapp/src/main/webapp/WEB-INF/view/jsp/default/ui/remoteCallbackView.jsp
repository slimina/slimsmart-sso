<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
     var remoteLoginUrl = "${remoteLoginUrl}";
     if(remoteLoginUrl == ""){
    	 window.location.href ="${pageContext.request.contextPath}";
     }else{
    	 var remoteUrl = remoteLoginUrl+"?validated=true";
         var service = null;
         <c:if test="${service != null && service != ''}">
           service = "&service=" + encodeURIComponent("${service}");
         </c:if>
         
         // 构造错误消息
         var errorMessage = "";
         <spring:hasBindErrors name="credentials">
         errorMessage = encodeURIComponent('<c:forEach var="error" items="${errors.allErrors}">${error.code}&</c:forEach>');
         </spring:hasBindErrors>
         
          var errorCode = null;
          var authcodeError = "${authcodeError}";
          if(authcodeError.indexOf("required.authcode")!=-1 || authcodeError.indexOf("error.authcode.bad")!=-1){
        	  errorCode =501;
          }else if(errorMessage.indexOf("required.username")!=-1 || errorMessage.indexOf("required.password")!=-1 ){
        	  errorCode =502;
          }else if(errorMessage.indexOf("error.authentication.credentials.bad")!=-1){
        	  errorCode = 503;
          }
          if(errorCode){
        	  remoteUrl +="&errorCode="+errorCode;
          }
         // 跳转回去
         window.location.href = remoteUrl + service;
     }
</script>
</head>
<body>${remoteLoginMessage}
</body>
</html>