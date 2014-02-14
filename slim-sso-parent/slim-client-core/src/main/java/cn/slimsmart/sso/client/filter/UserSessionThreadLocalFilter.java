package cn.slimsmart.sso.client.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.slimsmart.sso.client.session.UserSession;
import cn.slimsmart.sso.client.util.SessionHolder;

public final class UserSessionThreadLocalFilter implements Filter {

	public void init(final FilterConfig filterConfig) throws ServletException {
		// nothing to do here
	}

	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpSession session = request.getSession(false);
		final UserSession userSession = (UserSession) (session == null ? request.getAttribute(AbstractFilter.CONST_SLIM_USERSESSION) : session
				.getAttribute(AbstractFilter.CONST_SLIM_USERSESSION));
		try {
			SessionHolder.setUserSession(userSession);
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			SessionHolder.clear();
		}
	}

	public void destroy() {
		// nothing to do
	}

}
