package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.utils.ResolverUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.adobe.aem.guides.wknd.core.services.MetadataCSV;
import org.apache.commons.io.FileUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

@Component(
        service = MetadataCSV.class,
        immediate = true
)

public class MetadataCSVImpl implements MetadataCSV {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String CSV_EXTENSION = ".csv";
    private static final String CSV_TYPE = "text/csv";
    private static final String FILE_NAME = "_resources_metadata";
    //private static final String DOVER_RWSERVICE = "doverService";
    private static final String ASSET_JCR_NODE = "/jcr:content";
    private static final String ASSET_METADATA_NODE = "/metadata";
    String [] mimeIcons =  {"application/pdf=fa-file-pdf", "image/png=fa-file-image", "image/jpeg=fa-file-image", "text/plain=fa-file-alt", "application/msword=fa-file-word", "application/vnd.ms-excel=fa-file-excel"};

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    Externalizer externalizer;

    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss z");

    public String getMimeType(String mimeType) {
        for (String mimeIcon : mimeIcons) {
            if (mimeIcon.contains(mimeType)) {
                return mimeIcon.substring(mimeIcon.lastIndexOf("=") + 1);
            }
        }
        return "";
    }

    @Override
    public void createCsvFile(String damMapping, String fileLocation, String[] attributeMapping, Boolean debug) {
        String damPath = damMapping.substring(damMapping.lastIndexOf("=")+1);
        String externalizerName = damMapping.substring(0, damMapping.lastIndexOf("="));
        //String filePath = fileLocation + "/" + externalizerName + FILE_NAME + CSV_EXTENSION;
        String filePath = fileLocation + "/"+ externalizerName + FILE_NAME + CSV_EXTENSION;
        // Rebuild attribute mapping into separate lists
        List<String> headerAttributes = new ArrayList<>();
        List<String> propertyLocations = new ArrayList<>();
        for (String attribute : attributeMapping) {
            headerAttributes.add(attribute.substring(0, attribute.lastIndexOf("=")));
            propertyLocations.add(attribute.substring(attribute.lastIndexOf("=") + 1));
        }

        ResourceResolver resourceResolver = null;
        try {
             resourceResolver = ResolverUtil.newResolver(resolverFactory);

            //resourceResolver = resolverFactory.getServiceResourceResolver(getStringObjectMap());
            TagManager tm = resourceResolver.adaptTo(TagManager.class);

            if(resourceResolver != null && resourceResolver.getResource(damPath) != null) {
                this.externalizer = resourceResolver.adaptTo(Externalizer.class);
                AssetManager manager = resourceResolver.adaptTo(AssetManager.class);
                List<Hit> assets = queryAssets(damPath, debug);

                try (CSVPrinter printer = new CSVPrinter(new StringBuilder(), CSVFormat.EXCEL)) {
                    Iterable<String> headers = Arrays.asList(headerAttributes.toArray(new String[headerAttributes.size()]));
                    printer.printRecord(headers);

                    // Loop through each result
                    int rowCount = 0;
                    for (Hit asset : assets) {
                        if (resourceResolver.getResource(asset.getPath()) != null && resourceResolver.getResource(asset.getPath() + ASSET_JCR_NODE) != null && resourceResolver.getResource(asset.getPath() + ASSET_JCR_NODE + ASSET_METADATA_NODE) != null) {
                            Resource res = resourceResolver.getResource(asset.getPath());
                            ValueMap props = res.adaptTo(ValueMap.class);
                            Resource jcrResource = resourceResolver.getResource(asset.getPath() + ASSET_JCR_NODE);
                            ValueMap jcrProps = jcrResource.adaptTo(ValueMap.class);
                            Resource metadataResource = resourceResolver.getResource(jcrResource.getPath() + ASSET_METADATA_NODE);
                            ValueMap metadataProps = metadataResource.adaptTo(ValueMap.class);

                            // Begin looping through mapped property locations and build CSV rows
                            ArrayList<String> row = new ArrayList<>();
                            for (String property : propertyLocations) {
                                String cellValue = "";

                                // Custom mapping property condition for identifying date properties
                                boolean isBool = property.contains("{Boolean}");
                                property = isBool ? property.replace("{Boolean}", "") : property;
                                boolean isDate = property.contains("{Date}");
                                property = isDate ? property.replace("{Date}", "") : property;
                                boolean isEpoch = property.contains("{Epoch}");
                                property = isEpoch ? property.replace("{Epoch}", "") : property;
                                boolean isLocale = property.contains("{Locale}");
                                property = isLocale ? property.replace("{Locale}", "") : property;
                                boolean isByte = property.contains("{Byte}");
                                property = isByte ? property.replace("{Byte}", "") : property;
                                boolean isIcon = property.contains("{Icon}");
                                property = isIcon ? property.replace("{Icon}", "") : property;
                                boolean isTagArray = property.contains("{Tag[]}");
                                property = isTagArray ? property.replace("{Tag[]}", "") : property;

                                // Custom mapping property condition for inserting asset URL/path
                                if (property.equals("path")) {
                                    // Write asset URL
                                    cellValue = externalizer.externalLink(resourceResolver, externalizerName, asset.getPath());
                                } else {
                                    // If mapping property is for metadata node
                                    if (property.contains("metadata")) {
                                        // metadata props will begin with "metadata/", pull property name only from metadataProps
                                        property = property.replace("jcr:content/metadata/", "");

                                        if (metadataProps.get(property.substring(property.lastIndexOf("/") + 1)) != null) {
                                            if (isBool) {
                                                cellValue = String.valueOf(metadataProps.get(property, Boolean.class));
                                            } else if (isDate) {
                                                Calendar dateProp = metadataProps.get(property, Calendar.class);
                                                cellValue = sdf.format(dateProp.getTime());
                                            } else if (isEpoch) {
                                                Date dateProp = metadataProps.get(property, Date.class);
                                                cellValue = String.valueOf(dateProp.getTime());
                                            } else if (isLocale) {
                                                String localeStr = metadataProps.get(property).toString().replace("_", "-");
                                                if (Locale.forLanguageTag(localeStr) != null) {
                                                    Locale localeProp = Locale.forLanguageTag(localeStr);
                                                    cellValue = localeProp.getDisplayName();
                                                } else {
                                                    cellValue = localeStr;
                                                }
                                            } else if (isIcon) {
                                                cellValue = getMimeType(metadataProps.get(property).toString());
                                            } else if (isByte) {
                                                cellValue = (metadataProps.get(property) instanceof Long) ? FileUtils.byteCountToDisplaySize(metadataProps.get(property, Long.class).longValue()) : metadataProps.get(property).toString();
                                            } else if (isTagArray) {
                                                String[] tagArrayProp = metadataProps.get(property, String[].class);
                                                for (String tagProp : tagArrayProp) {
                                                    Tag tag = tm.resolve(tagProp);
                                                    if (tag != null) {
                                                        cellValue += getTranslatedTagValuesAsString(tag.getLocalizedTitles(),tag.getTitle()) + "|";
                                                    }
                                                }
                                                if (cellValue.contains("|")) {
                                                    cellValue = cellValue.substring(0, cellValue.length() - 1);
                                                }
                                            } else {
                                                if (!metadataProps.get(property).toString().isEmpty()) {
                                                    cellValue = metadataProps.get(property).toString();
                                                }
                                            }
                                        }

                                        // If mapping property is for jcr:content node
                                    } else if (property.contains("jcr:content")) {
                                        property = property.replace("jcr:content/", "");
                                        if (jcrProps.get(property) != null) {
                                            if (isBool) {
                                                cellValue = String.valueOf(jcrProps.get(property, Boolean.class));
                                            } else if (isDate) {
                                                Calendar dateProp = jcrProps.get(property, Calendar.class);
                                                cellValue = sdf.format(dateProp.getTime());
                                            } else if (isEpoch) {
                                                Date dateProp = jcrProps.get(property, Date.class);
                                                cellValue = String.valueOf(dateProp.getTime());
                                            } else if (isLocale) {
                                                String localeStr = jcrProps.get(property).toString().replace("_", "-");
                                                if (Locale.forLanguageTag(localeStr) != null) {
                                                    Locale localeProp = Locale.forLanguageTag(localeStr);
                                                    cellValue = localeProp.getDisplayName();
                                                } else {
                                                    cellValue = localeStr;
                                                }
                                            } else if (isIcon) {
                                                cellValue = getMimeType(jcrProps.get(property).toString());
                                            } else if (isByte) {
                                                cellValue = (jcrProps.get(property) instanceof Long) ? FileUtils.byteCountToDisplaySize(jcrProps.get(property, Long.class).longValue()) : "";
                                            } else if (isTagArray) {
                                                String[] tagArrayProp = jcrProps.get(property, String[].class);
                                                for (String tagProp : tagArrayProp) {
                                                    Tag tag = tm.resolve(tagProp);
                                                    if (tag != null) {
                                                        cellValue += getTranslatedTagValuesAsString(tag.getLocalizedTitles(),tag.getTitle()) + "|";
                                                    }
                                                }
                                                if (cellValue.contains("|")) {
                                                    cellValue = cellValue.substring(0, cellValue.length() - 1);
                                                }
                                            } else {
                                                if (!jcrProps.get(property).toString().isEmpty()) {
                                                    cellValue = jcrProps.get(property).toString();
                                                }
                                            }
                                        }

                                        // Otherwise assume property is on the root asset node
                                    } else {
                                        if (props.get(property) != null) {
                                            if (isBool) {
                                                cellValue = String.valueOf(props.get(property, Boolean.class));
                                            } else if (isDate) {
                                                Calendar dateProp = props.get(property, Calendar.class);
                                                cellValue = sdf.format(dateProp.getTime());
                                            } else if (isEpoch) {
                                                Date dateProp = props.get(property, Date.class);
                                                cellValue = String.valueOf(dateProp.getTime());
                                            } else if (isLocale) {
                                                String localeStr = props.get(property).toString().replace("_", "-");
                                                if (Locale.forLanguageTag(localeStr) != null) {
                                                    Locale localeProp = Locale.forLanguageTag(localeStr);
                                                    cellValue = localeProp.getDisplayName();
                                                } else {
                                                    cellValue = localeStr;
                                                }
                                            } else if (isIcon) {
                                                cellValue = getMimeType(props.get(property).toString());
                                            } else if (isByte) {
                                                cellValue = (props.get(property) instanceof Long) ? FileUtils.byteCountToDisplaySize(props.get(property, Long.class).longValue()) : "";
                                            } else if (isTagArray) {
                                                String[] tagArrayProp = props.get(property, String[].class);
                                                for (String tagProp : tagArrayProp) {
                                                    Tag tag = tm.resolve(tagProp);
                                                    if (tag != null) {
                                                        cellValue += getTranslatedTagValuesAsString(tag.getLocalizedTitles(),tag.getTitle()) + "|";
                                                    }
                                                }
                                                if (cellValue.contains("|")) {
                                                    cellValue = cellValue.substring(0, cellValue.length() - 1);
                                                }
                                            } else {
                                                if (!props.get(property).toString().isEmpty()) {
                                                    cellValue = props.get(property).toString();
                                                }
                                            }
                                        }
                                    }
                                }

                                row.add(cellValue);
                            }

                            // Validate row list size; if invalid skip
                            if (row.size() == propertyLocations.size()) {
                                Iterable<String> rowData = Arrays.asList(row.toArray(new String[row.size()]));
                                printer.printRecord(rowData);
                                rowCount++;
                            }
                        }
                    }

                    printer.println();

                    // Logging row count for debugging
                    log.info("Writing CSV with " + rowCount + " rows.");

                    InputStream inputStream = new ByteArrayInputStream(printer.getOut().toString().getBytes(StandardCharsets.UTF_8));
                    manager.createAsset(filePath, inputStream, CSV_TYPE, true);
                    resourceResolver.commit();
                }
            }
        } catch (LoginException e) {
            log.warn("generateInitialCsv() is having a login issue.  {}", e.getMessage());
        } catch (RepositoryException | IOException ex) {
            log.error(ex.getMessage());
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    /**
     *
     * @param damPath
     * @return List<Hit> searchResults
     */
    private List<Hit> queryAssets(String damPath, Boolean debug){
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = ResolverUtil.newResolver(resolverFactory);
            //resourceResolver = resolverFactory.getServiceResourceResolver(getStringObjectMap());
            if(resourceResolver != null) {
                SearchResult searchResults;
                Map<String, Object> queryMap = new HashMap<>();

                queryMap.put("path", damPath.substring(damPath.lastIndexOf("=") + 1));
                queryMap.put("1_property", "jcr:primaryType");
                queryMap.put("1_property.value", "dam:Asset");
                queryMap.put("2_property", "jcr:content/metadata/searchable");
                queryMap.put("2_property.value", true);
                // If debug mode enabled, ignore replication action for local developer use
                if (!debug) {
                    queryMap.put("3_property", "jcr:content/cq:lastReplicationAction");
                    queryMap.put("3_property.value", "Activate");
                }

                queryMap.put("p.limit", "-1");

                QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
                if(queryBuilder != null){
                    Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
                    searchResults = query.getResult();
                    List<Hit> resultSet = searchResults.getHits();
                    return resultSet;
                }

                return new ArrayList<>();
            }
        } catch (LoginException e) {
            log.warn(getClass().getName() + " is having a login issue.  {}", e.getMessage());
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return null;
    }

    private String getTranslatedTagValuesAsString(Map<Locale,String> localeStringMap, String englishTagTitle) {

        Locale locale = new Locale("en");
        localeStringMap.put(locale,englishTagTitle);
        //Set<Locale> localeSet = localeStringMap.keySet();
        for (Map.Entry<Locale,String> entry : localeStringMap.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());

        return localeStringMap.toString();
    }
}