package cn.slimsmart.sso.client.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.slimsmart.sso.client.util.CommonUtil;

public abstract class AbstractFilter extends AbstractConfigurationFilter {

	public static final String CONST_SLIM_USERSESSION = "_const_slim_usersession_";

	protected final Log log = LogFactory.getLog(getClass());

	private String artifactParameterName = "ticket";

	private String serviceParameterName = "service";

	private boolean encodeServiceUrl = true;

	private String serverName;

	private String service;

	public final void init(final FilterConfig filterConfig) throws ServletException {
		if (!isIgnoreInitConfiguration()) {
			setServerName(getPropertyFromInitParams(filterConfig, "serverName", null));
			log.trace("Loading serverName property: " + this.serverName);
			setService(getPropertyFromInitParams(filterConfig, "service", null));
			log.trace("Loading service property: " + this.service);
			setArtifactParameterName(getPropertyFromInitParams(filterConfig, "artifactParameterName", "ticket"));
			log.trace("Loading artifact parameter name property: " + this.artifactParameterName);
			setServiceParameterName(getPropertyFromInitParams(filterConfig, "serviceParameterName", "service"));
			log.trace("Loading serviceParameterName property: " + this.serviceParameterName);
			setEncodeServiceUrl(parseBoolean(getPropertyFromInitParams(filterConfig, "encodeServiceUrl", "true")));
			log.trace("Loading encodeServiceUrl property: " + this.encodeServiceUrl);
			initInternal(filterConfig);
		}
		init();
	}

	protected void initInternal(final FilterConfig filterConfig) throws ServletException {
		// template method
	}

	public void init() {

		CommonUtil.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
		CommonUtil.assertNotNull(this.serviceParameterName, "serviceParameterName cannot be null.");
		CommonUtil.assertTrue(StringUtils.isNotEmpty(this.serverName) || StringUtils.isNotEmpty(this.service),
				"serverName or service must be set.");
		CommonUtil.assertTrue(StringUtils.isBlank(this.serverName) || StringUtils.isBlank(this.service),
				"serverName and service cannot both be set.  You MUST ONLY set one.");
	}

	public final void setServerName(final String serverName) {
		if (serverName != null && serverName.endsWith("/")) {
			this.serverName = serverName.substring(0, serverName.length() - 1);
			log.info(String.format("Eliminated extra slash from serverName [%s].  It is now [%s]", serverName,
					this.serverName));
		} else {
			this.serverName = serverName;
		}
	}
	
	protected final String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
        return CommonUtil.constructServiceUrl(request, response, this.service, this.serverName, this.artifactParameterName, this.encodeServiceUrl);
    }

	public final void setService(final String service) {
		this.service = service;
	}

	public final void setArtifactParameterName(final String artifactParameterName) {
		this.artifactParameterName = artifactParameterName;
	}

	public final void setServiceParameterName(final String serviceParameterName) {
		this.serviceParameterName = serviceParameterName;
	}

	public final void setEncodeServiceUrl(final boolean encodeServiceUrl) {
		this.encodeServiceUrl = encodeServiceUrl;
	}

	public final String getArtifactParameterName() {
		return this.artifactParameterName;
	}

	public final String getServiceParameterName() {
		return this.serviceParameterName;
	}

}
