package com.adobe.aem.guides.wknd.core.models;

import java.util.List;
import java.util.Map;

public interface AuthorBio {
    String getFirstName();
    String getLastName();
    boolean getIsProfessor();
    String getPageTitle();
    String getRequestAttribute();
    String getHomePageName();
    String getLastModifiedBy();
    List<String> getBooks();
    List<Map<String, String>> getBookDetailsWithMap();
}
