package cn.slimsmart.sso.client.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.slimsmart.sso.client.session.HashMapBackedSessionMappingStorage;
import cn.slimsmart.sso.client.session.SessionMappingStorage;

public final class SingleSignOutHandler {

    private final Log log = LogFactory.getLog(getClass());

    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();
    
    private String artifactParameterName = "ticket";

    private String logoutParameterName = "logoutRequest";


    public void setSessionMappingStorage(final SessionMappingStorage storage) {
        this.sessionMappingStorage = storage;
    }

    public SessionMappingStorage getSessionMappingStorage() {
        return this.sessionMappingStorage;
    }

    public void setArtifactParameterName(final String name) {
        this.artifactParameterName = name;
    }

    public void setLogoutParameterName(final String name) {
        this.logoutParameterName = name;
    }

    public void init() {
        CommonUtil.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
        CommonUtil.assertNotNull(this.logoutParameterName, "logoutParameterName cannot be null.");
        CommonUtil.assertNotNull(this.sessionMappingStorage, "sessionMappingStorage cannote be null."); 
    }
    
    public boolean isTokenRequest(final HttpServletRequest request) {
        return StringUtils.isNotBlank(CommonUtil.safeGetParameter(request, this.artifactParameterName));
    }

    /**
     * Determines whether the given request is a CAS logout request.
     *
     * @param request HTTP request.
     *
     * @return True if request is logout request, false otherwise.
     */
    public boolean isLogoutRequest(final HttpServletRequest request) {
        return !isMultipartRequest(request) &&
        		StringUtils.isNotBlank(CommonUtil.safeGetParameter(request, this.logoutParameterName));
    }

    /**
     * Associates a token request with the current HTTP session by recording the mapping
     * in the the configured {@link SessionMappingStorage} container.
     * 
     * @param request HTTP request containing an authentication token.
     */
    public void recordSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(true);

        final String token = CommonUtil.safeGetParameter(request, this.artifactParameterName);
        if (log.isDebugEnabled()) {
            log.debug("Recording session for token " + token);
        }
        try {
            this.sessionMappingStorage.removeBySessionById(session.getId());
        } catch (final Exception e) {
        }
        sessionMappingStorage.addSessionById(token, session);
    }
   
    /**
     * Destroys the current HTTP session for the given CAS logout request.
     *
     * @param request HTTP request containing a CAS logout message.
     */
    public void destroySession(final HttpServletRequest request) {
        final String logoutMessage = CommonUtil.safeGetParameter(request, this.logoutParameterName);
        if (log.isTraceEnabled()) {
            log.trace ("Logout request:\n" + logoutMessage);
        }
        
        final String token = XmlUtil.getTextForElement(logoutMessage, "SessionIndex");
        if (StringUtils.isNotBlank(token)) {
            final HttpSession session = this.sessionMappingStorage.removeSessionByMappingId(token);
            if (session != null) {
                String sessionID = session.getId();

                if (log.isDebugEnabled()) {
                    log.debug ("Invalidating session [" + sessionID + "] for token [" + token + "]");
                }
                try {
                    session.invalidate();
                } catch (final IllegalStateException e) {
                    log.debug("Error invalidating session.", e);
                }
            }
        }
    }

    private boolean isMultipartRequest(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart");
    }
}
