package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.Author2;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

//defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL - Apply Optional @ class level
//defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED - Apply Required @ class level

//adaptables = SlingHttpServletRequest.class - Page level Binding (currentPage)
@Model(adaptables = SlingHttpServletRequest.class, adapters = Author2.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AuthorImpl2 implements Author2 {

    private static final Logger log = LoggerFactory.getLogger(AuthorImpl2.class);

    @Inject
    @Via("resource") // fetch authored value else default value
    //@Required -- converts this attribute as Mandatory to author
    //@ValueMapValue // Injects authored values
    @Default(values = "AEM")
    String firstName;

    @Inject
    @Via("resource")
    //@Optional -- converts this attribute as Optional to author
    //@ValueMapValue // Injects authored values
    @Default(values = "Trainer")
    String lastName;

    @Inject
    @Via("resource")
    boolean programmer;

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy= InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    @Self // Injecting Object itself
    SlingHttpServletRequest request;

    private String message;

    @ScriptVariable // Used To Inject Resource, Page, Component, ComponentContext
    Page currentPage;

    @RequestAttribute(name = "rAttribute") //Passed from Sightly
    private String requestAttribute;

    @ResourcePath(path="/content/wknd/language-masters/en") @Via("resource")
    Resource res;

    @OSGiService // To Inject services
    QueryBuilder queryBuilder;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        message = "Author Model !\n"
                + "Resource type is: " + resourceType + "\n"
                + "Current page is:  " + currentPagePath + "\n";

        log.info("Message : ", message);
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean getProgrammer() {
        return programmer;
    }

    public String getMessage() {
        return message;
    }

    public String getPageTitle() {
        return currentPage.getTitle();
    }

    public String getRequestAttribute() {
        return requestAttribute;
    }

    public String getHomePageName() {
        return res.getName();
    }
}
