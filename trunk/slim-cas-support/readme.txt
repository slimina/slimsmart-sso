����cas����ʹ֧֮��Զ��POST�ύ��֤��¼��ͬʱ���ش�����Ϣ��
slim-cas-client-support���ͻ���֧��jar��
slim-cas-server-support�������֧��jar��
slim-cas-webapp��cas�����

�ͻ������ã�
maven����slim-cas-client-support jar��
web.xml���ã�
<!-- ���ڵ����˳����ù���������ʵ�ֵ���ǳ����ܣ���ѡ���� -->
<listener>
	<listener-class>cn.slimsmart.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>

<!-- �ù���������ʵ�ֵ���ǳ����ܣ���ѡ���á� -->
<filter>
	<filter-name>CAS Single Sign Out Filter</filter-name>
	<filter-class>cn.slimsmart.cas.client.session.SingleSignOutFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>CAS Single Sign Out Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<!-- �ù����������û�����֤���������������� -->
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
		<!--�����server�Ƿ���˵�IP -->
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

<!-- �ù����������Ticket��У�鹤�������������� -->
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

<!-- �ù���������ʵ��HttpServletRequest����İ����� ������������ͨ��HttpServletRequest��getRemoteUser()�������SSO��¼�û��ĵ�¼������ѡ���á� -->
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

<!-- �ù�����ʹ�ÿ����߿���ͨ��org.jasig.cas.client.util.AssertionHolder����ȡ�û��ĵ�¼���� ����AssertionHolder.getAssertion().getPrincipal().getName()�� -->
<filter>
	<filter-name>CAS Assertion Thread Local Filter</filter-name>
	<filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>CAS Assertion Thread Local Filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
<!-- ======================== �����¼���� ======================== -->


��¼ҳ�棺
<form id="myLoginForm" action="https://sso.slimsmart.cn:8443/cas/remoteLogin" method="POST">
	<input type="hidden" name="service" value="http://www.slimsmart.cn:8888/demo" />
	<input type="hidden" name="loginUrl" value="http://www.slimsmart.cn:8888/demo/login.jsp"  />
	 <input type="hidden" name="submit" value="true" />
	<table>
		<tr>
			<td>�û���:</td>
			<td><input type="text" value="" name="username"></td>
		</tr>
		<tr>
			<td>����:</td>
			<td><input type="text" value="" name="password"></td>
		</tr>
		<tr>
			<td>��֤��:</td>
			<td><input type="text" value="" name="authcode"><img src="https://sso.slimsmart.cn:8443/cas/captcha.jpg" alt="" /></td>
		</tr>
		<tr>
			<td align="right" colspan="2"><input type="submit" /></td>
		</tr>
	</table>
</form>

�˳����ӣ�

<a href="https://sso.slimsmart.cn:8443/cas/remoteLogout?service=http://www.slimsmart.cn:8888/demo">����ǳ�</a>


ע��
https://sso.slimsmart.cn:8443/cas   cas��������ַ
http://www.slimsmart.cn:8888/demo  �ͻ��˵�ַ
http://www.slimsmart.cn:8888/demo/login.jsp �ͻ��˵�½ҳ��