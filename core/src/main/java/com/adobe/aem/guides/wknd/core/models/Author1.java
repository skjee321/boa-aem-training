package com.adobe.aem.guides.wknd.core.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Author1 {

    public Logger log = LoggerFactory.getLogger(Author1.class);

    String getFirstName();
    String getLastName();
    boolean getProgrammer();
}
