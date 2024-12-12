package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.helper.MultifieldHelper;

import java.util.List;
import java.util.Map;

public interface AuthorBooks {

    String getAuthorName();

    List<String> getAuthorBooks();

    List<Map<String,String>> getBookDetailsWithMap();

    List<MultifieldHelper> getBookDetailsWithBean();

    List<MultifieldHelper> getBookDetailsWithNastedMultifield();
}
