package com.adobe.aem.guides.wknd.core.services;

import com.day.cq.wcm.api.Page;

import java.util.Iterator;

public interface DemoService {
    public Iterator<Page> getPages();
}
