package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "BofA - Scheduler Configuration",
        description = "Sling scheduler configuration"
)
public @interface SchedulerConfiguration {

    @AttributeDefinition(
            name = "Scheduler name",
            description = "Name of the scheduler",
            type = AttributeType.STRING)
    public String schedulerName() default "Custom Sling Scheduler Configuration";

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression used by the scheduler",
            type = AttributeType.STRING)
    public String cronExpression() default "0/30 * * * * ?";
    //"0 0 12 1/1 * ? *" --- Daily once
    // 0 0 12 1/1 * ? * --- runs every day at 12 am
    //0 0/10 * 1/1 * ? * --- every 10 mins
    // 0 0 0/1 1/1 * ? * every 1 hour starting at 12 am
}

