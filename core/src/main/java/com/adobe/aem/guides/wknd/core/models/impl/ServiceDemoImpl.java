package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.ServiceDemo;
import com.adobe.aem.guides.wknd.core.services.DemoService;
import com.adobe.aem.guides.wknd.core.services.DemoServiceB;
import com.adobe.aem.guides.wknd.core.services.MultiService;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
adapters = ServiceDemo.class,
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ServiceDemoImpl implements ServiceDemo {
    private static final Logger LOG= LoggerFactory.getLogger(ServiceDemoImpl.class);

    /* START - Invoking Service from Sling Model Demo */
    @OSGiService
    DemoService demoService;

    @Override
    public Iterator<Page> getPagesList(){
        return demoService.getPages();
    }
    /*END - Invoking Service from Sling Model Demo*/

    /*START - Invoking a Service from another service Demo*/
    @OSGiService
    DemoServiceB demoServiceB;

    @Override
    public List<String> getPageTitleList() {
        return demoServiceB.getPages();
    }

    @Override
    public String getNameWithReference() {
        return demoServiceB.getNameWithReference();
    }

    /*END - Invoking a Service from another service Demo*/

    /*START -MULTI-SERVICES: ServiceRanking demo*/
    @OSGiService(filter = "(component.name=serviceA)")
    MultiService multiService;

    @Override
    public String getNameFromService() {
        return multiService.getName();
    }
    /*END - MULTI-SERVICES: ServiceRanking demo*/

    /* START - MULTI-SERVICES: Filter demo*/
    @OSGiService(filter = "(component.name=com.adobe.aem.guides.wknd.core.services.impl.MultiServiceBImpl)")
    MultiService multiServiceB;

    @Override
    public String getNameFromServiceB() {
        return multiServiceB.getName();
    }
    /* END - MULTI-SERVICES: Filter demo*/

}

