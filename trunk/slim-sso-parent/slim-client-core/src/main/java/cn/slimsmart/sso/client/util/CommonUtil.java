package cn.slimsmart.sso.client.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonUtil {
	private static final Log LOG = LogFactory.getLog(CommonUtil.class);

	private CommonUtil() {
		// nothing to do
	}

	public static void sendRedirect(final HttpServletResponse response,
			final String url) {
		try {
			response.sendRedirect(url);
		} catch (final Exception e) {
			LOG.warn(e.getMessage(), e);
		}
	}

	public static void assertTrue(final boolean cond, final String message) {
		if (!cond) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotNull(final Object object, final String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotEmpty(final Collection<?> c,
			final String message) {
		assertNotNull(c, message);
		if (c.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	public static String formatForUtcTime(final Date date) {
		final DateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static String safeGetParameter(final HttpServletRequest request, final String parameter) {
		if ("POST".equals(request.getMethod()) && "logoutRequest".equals(parameter)) {
			LOG.debug("safeGetParameter called on a POST HttpServletRequest for LogoutRequest.  Cannot complete check safely.  Reverting to standard behavior for this Parameter");
			return request.getParameter(parameter);
		}
		return request.getQueryString() == null || request.getQueryString().indexOf(parameter) == -1 ? null : request.getParameter(parameter);
	}

	public static String constructServiceUrl(final HttpServletRequest request,
			final HttpServletResponse response, final String service, final String serverName, final String artifactParameterName, final boolean encode) {
		if (StringUtils.isNotBlank(service)) {
			return encode ? response.encodeURL(service) : service;
		}

		final StringBuilder buffer = new StringBuilder();

		if (!serverName.startsWith("https://") && !serverName.startsWith("http://")) {
			buffer.append(request.isSecure() ? "https://" : "http://");
		}

		buffer.append(serverName);
		buffer.append(request.getRequestURI());

		if (StringUtils.isNotBlank(request.getQueryString())) {
			final int location = request.getQueryString().indexOf(artifactParameterName + "=");

			if (location == 0) {
				final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
				if (LOG.isDebugEnabled()) {
					LOG.debug("serviceUrl generated: " + returnValue);
				}
				return returnValue;
			}

			buffer.append("?");

			if (location == -1) {
				buffer.append(request.getQueryString());
			} else if (location > 0) {
				final int actualLocation = request.getQueryString()
						.indexOf("&" + artifactParameterName + "=");

				if (actualLocation == -1) {
					buffer.append(request.getQueryString());
				} else if (actualLocation > 0) {
					buffer.append(request.getQueryString().substring(0,
							actualLocation));
				}
			}
		}

		final String returnValue = encode ? response.encodeURL(buffer.toString()) : buffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("serviceUrl generated: " + returnValue);
		}
		return returnValue;
	}

}
