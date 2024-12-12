package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.config.BofaOSGiConfig;
import com.adobe.aem.guides.wknd.core.services.OSGiConfigModule;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = OSGiConfigModule.class,immediate = true)
@Designate(ocd = BofaOSGiConfig.class)
public class OSGiConfigModuleImpl implements OSGiConfigModule{

    private int serviceId;
    private String serviceName;
    private String serviceURL;

    @Activate
    protected void activate(BofaOSGiConfig bofaOSGiConfig){
        serviceId=bofaOSGiConfig.serviceID();
        serviceName=bofaOSGiConfig.serviceName();
        serviceURL=bofaOSGiConfig.serviceURL();
    }
    @Override
    public int getServiceId() {
        return serviceId;
    }
    @Override
    public String getServiceName() {
        return serviceName;
    }
    @Override
    public String getServiceURL() {
        return serviceURL;
    }
}
