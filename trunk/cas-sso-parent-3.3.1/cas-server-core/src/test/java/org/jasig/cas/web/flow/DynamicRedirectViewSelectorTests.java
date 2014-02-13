/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.web.flow;

import junit.framework.TestCase;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.test.MockRequestContext;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2005/08/19 18:27:17 $
 * @since 3.1
 *
 */
public class DynamicRedirectViewSelectorTests extends TestCase {

    private DynamicRedirectViewSelector selector = new DynamicRedirectViewSelector();

    public void testNoService() {
        try {
        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        this.selector.makeEntrySelection(context);
        fail("null pointer expected.");
        } catch (final NullPointerException e) {
            return;
        }
    }
    
    public void testNotUsedMethods() {
        assertFalse(this.selector.isEntrySelectionRenderable(new MockRequestContext()));
    }
    
    public void testWithCasService() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("service", "http://www.cnn.com");
        final SimpleWebApplicationServiceImpl impl = SimpleWebApplicationServiceImpl.createServiceFrom(request);
        
        final MockRequestContext context = new MockRequestContext();
        
        context.getFlowScope().put("service", impl);
        
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        assertTrue(this.selector.makeEntrySelection(context) instanceof ExternalRedirect);
        assertTrue(this.selector.makeRefreshSelection(context) instanceof ExternalRedirect);
    }

    /*
    XXX: re-enable when we figure out JVM requirements
    public void testWithGoogleAccountsService() throws Exception {
        
        final GoogleAccountsService service = GoogleAccountsServiceTests.getGoogleAccountsService();
        service.setPrincipal(TestUtils.getPrincipal());
        
        final MockRequestContext context = new MockRequestContext();
        
        context.getFlowScope().put("service", service);
        
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        assertTrue(this.selector.makeEntrySelection(context) instanceof ApplicationView);
    }
     */
}
