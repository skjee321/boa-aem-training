package com.adobe.aem.guides.wknd.core.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Author2 {

    public Logger log = LoggerFactory.getLogger(Author2.class);

    String getFirstName();
    String getLastName();
    boolean getProgrammer();
}
