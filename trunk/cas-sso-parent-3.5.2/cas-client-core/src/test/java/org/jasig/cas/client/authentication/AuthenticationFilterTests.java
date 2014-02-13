/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.client.authentication;

import junit.framework.TestCase;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.AssertionImpl;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Tests for the AuthenticationFilter.
 *
 * @author Scott Battaglia
 * @version $Revision: 11753 $ $Date: 2007-01-03 13:37:26 -0500 (Wed, 03 Jan 2007) $
 * @since 3.0
 */
public final class AuthenticationFilterTests extends TestCase {

    private static final String CAS_SERVICE_URL = "https://localhost:8443/service";

    private static final String CAS_LOGIN_URL = "https://localhost:8443/cas/login";

    private AuthenticationFilter filter;

    protected void setUp() throws Exception {
        // TODO CAS_SERVICE_URL, false, CAS_LOGIN_URL
        this.filter = new AuthenticationFilter();
        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("service", "https://localhost:8443/service");
        this.filter.init(config);
    }

    protected void tearDown() throws Exception {
        this.filter.destroy();
    }

    public void testRedirect() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = new FilterChain() {

            public void doFilter(ServletRequest arg0, ServletResponse arg1)
                    throws IOException, ServletException {
                // nothing to do
            }
        };

        request.setSession(session);
        this.filter.doFilter(request, response, filterChain);

        assertEquals(CAS_LOGIN_URL + "?service="
                + URLEncoder.encode(CAS_SERVICE_URL, "UTF-8"), response
                .getRedirectedUrl());
    }

    public void testRedirectWithQueryString() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        request.setQueryString("test=12456");
        request.setRequestURI("/test");
        request.setSecure(true);
        final FilterChain filterChain = new FilterChain() {

            public void doFilter(ServletRequest arg0, ServletResponse arg1)
                    throws IOException, ServletException {
                // nothing to do
            }
        };

        request.setSession(session);
        this.filter = new AuthenticationFilter();

        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("serverName", "localhost:8443");
        this.filter.init(config);

        this.filter.doFilter(request, response, filterChain);

        assertEquals(CAS_LOGIN_URL
                + "?service="
                + URLEncoder.encode("https://localhost:8443"
                + request.getRequestURI() + "?" + request.getQueryString(),
                "UTF-8"), response.getRedirectedUrl());
    }

    public void testAssertion() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = new FilterChain() {

            public void doFilter(ServletRequest arg0, ServletResponse arg1)
                    throws IOException, ServletException {
                // nothing to do
            }
        };

        request.setSession(session);
        session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION,
                new AssertionImpl("test"));
        this.filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    public void testRenew() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = new FilterChain() {

            public void doFilter(ServletRequest arg0, ServletResponse arg1)
                    throws IOException, ServletException {
                // nothing to do
            }
        };

        this.filter.setRenew(true);
        request.setSession(session);
        this.filter.doFilter(request, response, filterChain);

        assertNotNull(response.getRedirectedUrl());
        assertTrue(response.getRedirectedUrl().indexOf("renew=true") != -1);
    }

    public void testGateway() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = new FilterChain() {

            public void doFilter(ServletRequest arg0, ServletResponse arg1)
                    throws IOException, ServletException {
                // nothing to do
            }
        };

        request.setSession(session);
        this.filter.setRenew(true);
        this.filter.setGateway(true);
        this.filter.doFilter(request, response, filterChain);
        assertNotNull(session.getAttribute(DefaultGatewayResolverImpl.CONST_CAS_GATEWAY));
        assertNotNull(response.getRedirectedUrl());

        final MockHttpServletResponse response2 = new MockHttpServletResponse();
        this.filter.doFilter(request, response2, filterChain);
        assertNull(session.getAttribute(DefaultGatewayResolverImpl.CONST_CAS_GATEWAY));
        assertNull(response2.getRedirectedUrl());
    }
}
