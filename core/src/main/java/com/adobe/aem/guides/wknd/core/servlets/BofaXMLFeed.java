package com.adobe.aem.guides.wknd.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.Servlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Iterator;


@Component(service = Servlet.class)
@SlingServletPaths(
        value = {"/bin/xmlfeed"}
)
public class BofaXMLFeed extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BofaXMLFeed.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            //ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            final ResourceResolver resourceResolver = req.getResourceResolver();
            Page page = resourceResolver.adaptTo(PageManager.class).getPage("/content/wknd/language-masters/en");
            Iterator<Page> childPages = page.listChildren();
            Element root = document.createElement("allnews");
            document.appendChild(root);
            while (childPages.hasNext()) {
                Element news = document.createElement("news");
                root.appendChild(news);

                Page childPage = childPages.next();
                Element title = document.createElement("title");
                title.appendChild(document.createTextNode(childPage.getTitle()));
                news.appendChild(title);

                Element name = document.createElement("name");
                name.appendChild(document.createTextNode(childPage.getName()));
                news.appendChild(name);

            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            resp.setContentType("application/xml");
            StreamResult streamResult = new StreamResult(resp.getWriter());
            transformer.transform(domSource,streamResult);

        } catch (Exception e) {
            LOG.info("\n ERROR GET - {} ", e.getMessage());
        }
    }
}
