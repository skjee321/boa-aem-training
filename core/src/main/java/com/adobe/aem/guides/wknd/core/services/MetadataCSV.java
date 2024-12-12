package com.adobe.aem.guides.wknd.core.services;

public interface MetadataCSV {
    void createCsvFile(String damMapping, String fileLocation, String[] attributeMapping, Boolean debug);
}