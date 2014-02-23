/*
 * All rights Reserved, tianwei7518.
 * Copyright(C) 2014-2015
 * 2014年2月20日 
 */

package cn.slimsmart.sso.client.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SingleSignOutHttpSessionListener implements HttpSessionListener{
	
	protected final Log log = LogFactory.getLog(getClass());

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		log.debug("create session id = "+sessionEvent.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		log.debug("destroy session id = "+sessionEvent.getSession().getId());
	}

}
