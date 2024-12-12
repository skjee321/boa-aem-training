package com.adobe.aem.guides.wknd.core.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Author {

    public Logger log = LoggerFactory.getLogger(Author.class);

    String getFirstName();
    String getLastName();
    boolean getProgrammer();
}
