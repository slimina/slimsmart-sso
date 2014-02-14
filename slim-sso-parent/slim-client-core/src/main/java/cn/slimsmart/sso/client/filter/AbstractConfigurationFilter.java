package cn.slimsmart.sso.client.filter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractConfigurationFilter implements Filter {

	protected final Log log = LogFactory.getLog(getClass());

	private boolean ignoreInitConfiguration = false;

	protected final String getPropertyFromInitParams(
			final FilterConfig filterConfig, final String propertyName,
			final String defaultValue) {
		final String value = filterConfig.getInitParameter(propertyName);

		if (StringUtils.isNotBlank(value)) {
			log.info("Property [" + propertyName + "] loaded from FilterConfig.getInitParameter with value [" + value
					+ "]");
			return value;
		}

		final String value2 = filterConfig.getServletContext().getInitParameter(propertyName);

		if (StringUtils.isNotBlank(value2)) {
			log.info("Property [" + propertyName + "] loaded from ServletContext.getInitParameter with value ["
					+ value2 + "]");
			return value2;
		}
		InitialContext context;
		try {
			context = new InitialContext();
		} catch (final NamingException e) {
			log.warn(e, e);
			return defaultValue;
		}

		final String shortName = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
		final String value3 = loadFromContext(context, "java:comp/env/cas/" + shortName + "/" + propertyName);

		if (StringUtils.isNotBlank(value3)) {
			log.info("Property [" + propertyName + "] loaded from JNDI Filter Specific Property with value [" + value3
					+ "]");
			return value3;
		}

		final String value4 = loadFromContext(context, "java:comp/env/cas/" + propertyName);

		if (StringUtils.isNotBlank(value4)) {
			log.info("Property [" + propertyName + "] loaded from JNDI with value [" + value4 + "]");
			return value4;
		}

		log.info("Property [" + propertyName + "] not found.  Using default value [" + defaultValue + "]");
		return defaultValue;
	}

	protected final boolean parseBoolean(final String value) {
		return ((value != null) && value.equalsIgnoreCase("true"));
	}

	protected final String loadFromContext(final InitialContext context,
			final String path) {
		try {
			return (String) context.lookup(path);
		} catch (final NamingException e) {
			return null;
		}
	}

	public final void setIgnoreInitConfiguration(boolean ignoreInitConfiguration) {
		this.ignoreInitConfiguration = ignoreInitConfiguration;
	}

	protected final boolean isIgnoreInitConfiguration() {
		return this.ignoreInitConfiguration;
	}
}
