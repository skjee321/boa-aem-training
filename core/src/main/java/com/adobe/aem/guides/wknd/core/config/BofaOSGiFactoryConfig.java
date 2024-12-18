package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="Bofa Training - OSGi Factory Configuration",
        description = "OSGi Factory Configuration demo.")
public @interface BofaOSGiFactoryConfig {
    @AttributeDefinition(
            name = "Config ID",
            description = "Enter service id.",
            type = AttributeType.INTEGER)
    int configID();

    @AttributeDefinition(
            name = "Service Name",
            description = "Enter service name.",
            type = AttributeType.STRING)
    public String serviceName() default "Service #";

    @AttributeDefinition(
            name = "Service URL",
            description = "Add Service URL.",
            type = AttributeType.STRING
    )
    String serviceURL() default "URL #";
}
