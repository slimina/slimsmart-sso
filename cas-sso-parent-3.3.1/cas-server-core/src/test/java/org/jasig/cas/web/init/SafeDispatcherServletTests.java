/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package org.jasig.cas.web.init;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContextException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

import junit.framework.TestCase;

/**
 * Testcase for SafeDispatcherServlet.
 * 
 * @author Andrew Petro
 * @version $Revision: 42053 $ $Date: 2007-06-10 09:17:55 -0400 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public class SafeDispatcherServletTests extends TestCase {

    private SafeDispatcherServlet safeServlet;

    private ServletContext mockContext;

    private MockServletConfig mockConfig;

    protected void setUp() throws Exception {
        super.setUp();

        this.safeServlet = new SafeDispatcherServlet();

        this.mockContext = new MockServletContext();
        this.mockConfig = new MockServletConfig(this.mockContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test that SafeDispatcherServlet does not propogate exceptions generated
     * by its underlying DispatcherServlet on init() and that it stores the
     * exception into the ServletContext as the expected attribute name.
     */
    public void testInitServletConfig() {

        /*
         * we fail if safeServlet propogates exception we rely on the underlying
         * DispatcherServlet throwing an exception when init'ed in this way
         * without the servlet name having been set and without there being a
         * -servlet.xml that it can find on the classpath.
         */
        this.safeServlet.init(this.mockConfig);

        /*
         * here we test that the particular exception stored by the underlying
         * DispatcherServlet has been stored into the ServetContext as an
         * attribute as advertised by SafeDispatcherServlet. we rely on knowing
         * the particular exception that the underlying DispatcherServlet throws
         * under these circumstances;
         */
        BeanDefinitionStoreException bdse = (BeanDefinitionStoreException) this.mockContext
            .getAttribute(SafeDispatcherServlet.CAUGHT_THROWABLE_KEY);
        assertNotNull(bdse);

    }

    /*
     * Test that the SafeDispatcherServlet does not service requests when it has
     * failed init and instead throws an ApplicationContextException.
     */
    public void testService() throws ServletException, IOException {
        this.safeServlet.init(this.mockConfig);

        ServletRequest mockRequest = new MockHttpServletRequest();
        ServletResponse mockResponse = new MockHttpServletResponse();

        try {
            this.safeServlet.service(mockRequest, mockResponse);
        } catch (ApplicationContextException ace) {
            // good, threw the exception we expected.
            return;
        }

        fail("Should have thrown ApplicationContextException since init() failed.");
    }

    public void testServiceSucceeds() {
        this.mockConfig = new MockServletConfig(this.mockContext, "cas");
        this.safeServlet.init(this.mockConfig);

        ServletRequest mockRequest = new MockHttpServletRequest();
        ServletResponse mockResponse = new MockHttpServletResponse();

        try {
            this.safeServlet.service(mockRequest, mockResponse);
        } catch (ApplicationContextException e) {
            System.out.println(e);
            fail("Unexpected exception.");
        } catch (Exception e) {
            return;
        }
    }
}
