package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.OSGiConfigDemo;
import com.adobe.aem.guides.wknd.core.services.OSGiConfig;
import com.adobe.aem.guides.wknd.core.services.OSGiConfigModule;
import com.adobe.aem.guides.wknd.core.services.OSGiFactoryConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = OSGiConfigDemo.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class OSGiConfigDemoImpl implements OSGiConfigDemo{

    private static final Logger log = LoggerFactory.getLogger(OSGiConfigDemoImpl.class);
    /* ========= START : OSGi Config : Configurations within Service ======== */
    @OSGiService
    OSGiConfig oSGiConfig;

    @Override
    public String getServiceName() {
        return oSGiConfig.getServiceName();
    }

    @Override
    public int getServiceCount() {
        return oSGiConfig.getServiceCount();
    }

    @Override
    public boolean isLiveData() {
        return oSGiConfig.isLiveData();
    }

    @Override
    public String[] getCountries() {
        return oSGiConfig.getCountries();
    }

    @Override
    public String getRunModes() {
        return oSGiConfig.getRunModes();
    }
    /* ========= END : OSGi Config : Configurations within Service ======== */

    /* ========= START : OSGi Config : Configurations outside Service ======== */
    @OSGiService
    OSGiConfigModule oSGiConfigModule;

    @Override
    public int getServiceId() {
        return oSGiConfigModule.getServiceId();
    }

    @Override
    public String getServiceNameModule() {
        return oSGiConfigModule.getServiceName();
    }

    @Override
    public String getServiceURL() {
        return oSGiConfigModule.getServiceURL();
    }

    /* ========= END : OSGi Config : Configurations outside Service ======== */

    /* ========= START : OSGi Config : Configurations Factory design ======== */
    @OSGiService
    OSGiFactoryConfig oSGiFactoryConfig;

    @Override
    public List<OSGiFactoryConfig> getAllOSGiConfigs() {
        log.info("Enter OSGi Config Factory!!!");
        return oSGiFactoryConfig.getAllConfigs();
    }
    /* ========= END : OSGi Config : Configurations Factory design ======== */

}
