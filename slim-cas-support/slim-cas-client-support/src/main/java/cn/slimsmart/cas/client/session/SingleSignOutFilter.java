
package cn.slimsmart.cas.client.session;

import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.AbstractConfigurationFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class SingleSignOutFilter extends AbstractConfigurationFilter {

    private static final SingleSignOutHandler handler = new SingleSignOutHandler();

    public void init(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            handler.setArtifactParameterName(getPropertyFromInitParams(filterConfig, "artifactParameterName", "ticket"));
            handler.setLogoutParameterName(getPropertyFromInitParams(filterConfig, "logoutParameterName", "logoutRequest"));
        }
        handler.init();
    }

    public void setArtifactParameterName(final String name) {
        handler.setArtifactParameterName(name);
    }
    
    public void setLogoutParameterName(final String name) {
        handler.setLogoutParameterName(name);
    }

    public void setSessionMappingStorage(final SessionMappingStorage storage) {
        handler.setSessionMappingStorage(storage);
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (handler.isTokenRequest(request)) {
            handler.recordSession(request);
        } else if (handler.isLogoutRequest(request)) {
            handler.destroySession(request);
            // Do not continue up filter chain
            return;
        } else {
            log.trace("Ignoring URI " + request.getRequestURI());
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        // nothing to do
    }
    
    protected static SingleSignOutHandler getSingleSignOutHandler() {
        return handler;
    }
}
