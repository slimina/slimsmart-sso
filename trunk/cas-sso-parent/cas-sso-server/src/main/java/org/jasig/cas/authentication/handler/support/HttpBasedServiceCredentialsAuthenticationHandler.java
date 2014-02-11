/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.authentication.handler.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.HttpBasedServiceCredentials;
import org.jasig.cas.util.HttpClient;

/**
 * Class to validate the credentials presented by communicating with the web
 * server and checking the certificate that is returned against the hostname,
 * etc.
 * <p>
 * This class is concerned with ensuring that the protocol is HTTPS and that a
 * response is returned. The SSL handshake that occurs automatically by opening
 * a connection does the heavy process of authenticating.
 * 
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public final class HttpBasedServiceCredentialsAuthenticationHandler implements
    AuthenticationHandler {

    /** The string representing the HTTPS protocol. */
    private static final String PROTOCOL_HTTPS = "https";

    /** Boolean variable denoting whether secure connection is required or not. */
    private boolean requireSecure = true;

    /** Log instance. */
    private final Log log = LogFactory.getLog(getClass());

    /** Instance of Apache Commons HttpClient */
    private HttpClient httpClient;

    public boolean authenticate(final Credentials credentials) {
        final HttpBasedServiceCredentials serviceCredentials = (HttpBasedServiceCredentials) credentials;
        if (this.requireSecure
            && !serviceCredentials.getCallbackUrl().getProtocol().equals(
                PROTOCOL_HTTPS)) {
            if (log.isDebugEnabled()) {
                log.debug("Authentication failed because url was not secure.");
            }
            return false;
        }
        log
            .debug("Attempting to resolve credentials for "
                + serviceCredentials);

        return this.httpClient.isValidEndPoint(serviceCredentials
            .getCallbackUrl());
    }

    /**
     * @return true if the credentials provided are not null and the credentials
     * are a subclass of (or equal to) HttpBasedServiceCredentials.
     */
    public boolean supports(final Credentials credentials) {
        return credentials != null
            && HttpBasedServiceCredentials.class.isAssignableFrom(credentials
                .getClass());
    }

    /** Sets the HttpClient which will do all of the connection stuff. */
    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Set whether a secure url is required or not.
     * 
     * @param requireSecure true if its required, false if not. Default is true.
     */
    public void setRequireSecure(final boolean requireSecure) {
        this.requireSecure = requireSecure;
    }
}
