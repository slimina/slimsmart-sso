根据cas改造使之支持远程POST提交验证登录，同时返回错误休息。
slim-cas-client-support：客户端支持jar包
slim-cas-server-support：服务端支持jar包
slim-cas-webapp：cas服务端

客户端配置：
maven引入slim-cas-client-support jar包
web.xml配置：
<!-- 用于单点退出，该过滤器用于实现单点登出功能，可选配置 -->
<listener>
	<listener-class>cn.slimsmart.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>

<!-- 该过滤器用于实现单点登出功能，可选配置。 -->
<filter>
	<filter-name>CAS Single Sign Out Filter</filter-name>
	<filter-class>cn.slimsmart.cas.client.session.SingleSignOutFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>CAS Single Sign Out Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<!-- 该过滤器负责用户的认证工作，必须启用它 -->
<filter>
	<filter-name>CASFilter</filter-name>
	<filter-class>cn.slimsmart.cas.client.authentication.RemoteAuthenticationFilter</filter-class>
	 <init-param>
<param-name>localLoginUrl</param-name>
<param-value>http://www.slimsmart.cn:8888/demo/login.jsp</param-value>
</init-param>
	<init-param>
		<param-name>casServerLoginUrl</param-name>
		<param-value>https://sso.slimsmart.cn:8443/cas/remoteLogin</param-value>
		<!--这里的server是服务端的IP -->
	</init-param>
	<init-param>
		<param-name>serverName</param-name>
		<param-value>http://www.slimsmart.cn:8888</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>CASFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<!-- 该过滤器负责对Ticket的校验工作，必须启用它 -->
<filter>
	<filter-name>CAS Validation Filter</filter-name>
	<filter-class>
		org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter
	</filter-class>
	<init-param>
		<param-name>casServerUrlPrefix</param-name>
		<param-value>https://sso.slimsmart.cn:8443/cas</param-value>
	</init-param>
	<init-param>
		<param-name>serverName</param-name>
		<param-value>http://www.slimsmart.cn:8888</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>CAS Validation Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<!-- 该过滤器负责实现HttpServletRequest请求的包裹， 比如允许开发者通过HttpServletRequest的getRemoteUser()方法获得SSO登录用户的登录名，可选配置。 -->
<filter>
	<filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
	<filter-class>
		org.jasig.cas.client.util.HttpServletRequestWrapperFilter
	</filter-class>
</filter>
<filter-mapping>
	<filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<!-- 该过滤器使得开发者可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。 比如AssertionHolder.getAssertion().getPrincipal().getName()。 -->
<filter>
	<filter-name>CAS Assertion Thread Local Filter</filter-name>
	<filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>CAS Assertion Thread Local Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
<!-- ======================== 单点登录结束 ======================== -->


登录页面：
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

退出链接：

<a href="https://sso.slimsmart.cn:8443/cas/remoteLogout?service=http://www.slimsmart.cn:8888/demo">单点登出</a>


注：
https://sso.slimsmart.cn:8443/cas   cas服务器地址
http://www.slimsmart.cn:8888/demo  客户端地址
http://www.slimsmart.cn:8888/demo/login.jsp 客户端登陆页面