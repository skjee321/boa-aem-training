package com.adobe.aem.guides.wknd.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(AemContextExtension.class)
class BofaResourceTypesServletTest {

    AemContext aemContext = new AemContext();
    BofaResourceTypesServlet bofaResourceTypesServlet = new BofaResourceTypesServlet();


    @BeforeEach
    void setUp() {
        aemContext.build().resource("/content/wknd/test","jcr:title", "WKND Servlet Test Page");
        aemContext.currentResource("/content/wknd/test");
    }

    @Test
    void doGet_Pass() throws ServletException, IOException {

        MockSlingHttpServletRequest request = aemContext.request();
        MockSlingHttpServletResponse response = aemContext.response();

        bofaResourceTypesServlet.doGet(request, response);

        assertEquals("Page Title = WKND Servlet Test Page", response.getOutputAsString());
        assertEquals("GET", request.getMethod());
        assertEquals(200, response.getStatus());
    }

    @Test
    void doGet_Fail() throws ServletException, IOException {

        MockSlingHttpServletRequest request = aemContext.request();
        MockSlingHttpServletResponse response = aemContext.response();

        bofaResourceTypesServlet.doGet(request, response);

        assertNotEquals("Page Title = WKND Servlet Test Page1", response.getOutputAsString());
    }


    @Test
    void doPost_Pass() throws ServletException, IOException {

        MockSlingHttpServletRequest request = aemContext.request();
        MockSlingHttpServletResponse response = aemContext.response();
        request.addRequestParameter("firstName", "AEM");
        request.addRequestParameter("lastName", "Training");

        bofaResourceTypesServlet.doPost(request, response);

        assertEquals("AEM", request.getParameter("firstName"));
        assertEquals("Training", request.getParameter("lastName"));
        assertEquals("======FORM SUBMITTED========", response.getOutputAsString());

    }
}