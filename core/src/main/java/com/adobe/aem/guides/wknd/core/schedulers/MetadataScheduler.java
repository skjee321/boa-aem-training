package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.services.MetadataCSV;
import com.adobe.aem.guides.wknd.core.utils.ResolverUtil;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MetadataScheduler.class, immediate = true,property = {
	"scheduler.runOn=SINGLE"
	})
@Designate(ocd = MetadataScheduler.Configuration.class, factory=false)
public class MetadataScheduler implements Runnable {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@ObjectClassDefinition(name = "BofA - Asset Metadata Scheduler Configuration")
	public @interface Configuration {
		@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
		String schedulerName() default "Asset Metadata Scheduler";

		@AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
		boolean schedulerConcurrent() default false;

		@AttributeDefinition(name = "Enabled", description = "Enable scheduler", type = AttributeType.BOOLEAN)
		boolean serviceEnabled() default false;

		@AttributeDefinition(name = "Debug Mode", description = "Enable debugger for scheduler", type = AttributeType.BOOLEAN)
		boolean serviceDebug() default false;

		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default run daily at 1AM (0 0 1 * * ?).", type = AttributeType.STRING)
		String schedulerExpression() default "* 0/10 * * * ?";

		@AttributeDefinition(name = "Data File Location", description = "Path which has the hierarchies of assets for each opco or brand eg. /content/dam/opw", type = AttributeType.STRING)
		String fileLocation() default "/content/dam/data/assets/metadata";

		@AttributeDefinition(name = "Site to Assets Mapping", description = "Mapping of site externalizer and asset hierarchies for each opco or brand eg. inpro-seal=/content/dam/dpc/inpro-seal", type = AttributeType.STRING)
		String damMapping() default "/content/dam/wknd-shared/en";

		@AttributeDefinition(name = "Attribute to Property Mapping", description = "Mapping of algolia attributes to asset properties. eg. language=dc:language, type=documentType", type = AttributeType.STRING)
		String[] attributeMapping() default {"url=asset-path", "date=cq:lastReplicated", "language=metadata/dc:language"};
	}

	@Reference
	private transient ResolverUtil resolverUtil;

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Reference
	private Scheduler scheduler;

	@Reference
	private MetadataCSV metadataCSV;
	
	private int schedulerID;
	private String schedulerExpression;
	private Boolean serviceDebug;
	private String fileLocation;
	private String damMapping;
	private String[] attributeMapping;

	@Activate
	protected void activate(Configuration cfg) {
		this.schedulerID = cfg.schedulerName().hashCode();
		this.schedulerExpression = cfg.schedulerExpression();
		this.serviceDebug = cfg.serviceDebug();
		this.fileLocation = cfg.fileLocation();
		this.damMapping = cfg.damMapping();
		this.attributeMapping = cfg.attributeMapping();

		addScheduler(cfg);
	}

	@Modified
	protected void modified(Configuration cfg) {
		log.info("Modified Metadata Scheduler, reactivating...");
		removeScheduler();
		activate(cfg);
	}

	@Deactivate
	protected void deactivate(Configuration cfg) {
		log.info("Deactivated Metadata Scheduler, deactivating...");
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */
	private void removeScheduler() {
		log.info("Removing scheduler job '{}'", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(Configuration cfg) {
		if (cfg.serviceEnabled()) {
			ScheduleOptions scheduleOptions = scheduler.EXPR(cfg.schedulerExpression());
			scheduleOptions.name(String.valueOf(schedulerID));
			if(cfg.schedulerConcurrent()) {
				scheduleOptions.canRunConcurrently(true);
				log.info("This MetadataScheduler is configured to run concurrently! ");
			}
			else {
				scheduleOptions.canRunConcurrently(false);
			}
			
			scheduleOptions.onLeaderOnly(true);
			scheduler.schedule(this, scheduleOptions);
			log.info("MetadataScheduler added successfully.");
		} 
		else {
			log.info("MetadataScheduler is disabled, no scheduler job created.");
			removeScheduler();
		}
	}

	@Override
	public void run() {
		log.info("Start MetadataScheduler, [cron {}]", schedulerExpression);

        ResourceResolver resolver = null;
        try {
            resolver = ResolverUtil.newResolver(resourceResolverFactory);
			if (resolver != null) {
				metadataCSV.createCsvFile(getDamMapping(), getFileLocation(), getAttributeMapping(), getServiceDebug());
			}
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

	}

	private Boolean getServiceDebug() {
		return serviceDebug;
	}
	private void setServiceDebug(Boolean serviceDebug) {
		this.serviceDebug = serviceDebug;
	}

	private String getFileLocation() {
		return fileLocation;
	}
	private void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	private String getDamMapping() {
		return damMapping;
	}
	private void setDamMapping(String damMapping) {
		this.damMapping = damMapping;
	}

	private String[] getAttributeMapping() { return attributeMapping; }
	private void setAttributeMapping(String[] attributeMapping) {
		this.attributeMapping = attributeMapping;
	}
}