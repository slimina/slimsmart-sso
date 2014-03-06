package cn.slimsmart.cas.client.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jasig.cas.client.session.SessionMappingStorage;

public final class SingleSignOutHttpSessionListener implements HttpSessionListener {

	private SessionMappingStorage sessionMappingStorage;
	
    public void sessionCreated(final HttpSessionEvent event) {
        // nothing to do at the moment
    }

    public void sessionDestroyed(final HttpSessionEvent event) {
    	if (sessionMappingStorage == null) {
    	    sessionMappingStorage = getSessionMappingStorage();
    	}
        final HttpSession session = event.getSession();
        sessionMappingStorage.removeBySessionById(session.getId());
    }

    protected static SessionMappingStorage getSessionMappingStorage() {
    	return SingleSignOutFilter.getSingleSignOutHandler().getSessionMappingStorage();
    }
}
