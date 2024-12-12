package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.helper.MultifieldHelper;
import com.adobe.aem.guides.wknd.core.helper.NastedHalper;
import com.adobe.aem.guides.wknd.core.models.AuthorBooks;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = AuthorBooks.class,
        resourceType = AuthorBooksImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AuthorBooksImpl implements AuthorBooks {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorBooksImpl.class);
    final protected static String RESOURCE_TYPE="wknd/components/author-books";

    @SlingObject
    private Resource resource;

    @ValueMapValue
    @Default(values = "AEM")
    private String authorname;

    @ValueMapValue
    private List<String> books;


    @Override
    public String getAuthorName() {
        return authorname;
    }

    @Override
    public List<String> getAuthorBooks() {
        if(books!=null){
            return new ArrayList<String>(books);
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, String>> getBookDetailsWithMap() {
        List<Map<String, String>> bookDetailsMap=new ArrayList<>();
        try {
            Resource bookDetail=resource.getChild("bookdetailswithmap");
            if(bookDetail!=null){
                for (Resource book : bookDetail.getChildren()) {
                    Map<String,String> bookMap=new HashMap<>();
                    bookMap.put("bookname",book.getValueMap().get("bookname",String.class));
                    bookMap.put("booksubject",book.getValueMap().get("booksubject",String.class));
                    bookMap.put("publishyear",book.getValueMap().get("publishyear",String.class));
                    bookDetailsMap.add(bookMap);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details {} ",e.getMessage());
        }
        LOG.info("\n SIZE {} ",bookDetailsMap.size());
        return bookDetailsMap;
    }

    @Override
    public List<MultifieldHelper> getBookDetailsWithBean(){
        List<MultifieldHelper> bookDetailsBean=new ArrayList<>();
        try {
            Resource bookDetailBean=resource.getChild("bookdetailswithbean");
            if(bookDetailBean!=null){
                for (Resource bookBean : bookDetailBean.getChildren()) {
                    LOG.info("\n PATH Bean {} ",bookBean.getPath());
                    LOG.info("\n BEAN PRO {} ",bookBean.getValueMap().get("bookname",String.class));

                    bookDetailsBean.add(new MultifieldHelper(bookBean));
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details With Bean {} ",e.getMessage());
        }
        return bookDetailsBean;
    }


    @Override
    public List<MultifieldHelper> getBookDetailsWithNastedMultifield() {
        List<MultifieldHelper> bookDetailsNasted=new ArrayList<>();
        try {
            Resource bookDetailNasted=resource.getChild("bookdetailswithnastedmultifield");
            if(bookDetailNasted!=null){
                for (Resource bookNasted : bookDetailNasted.getChildren()) {
                    MultifieldHelper multifieldHelper=new MultifieldHelper(bookNasted);
                    if(bookNasted.hasChildren()){
                        List<NastedHalper> bookNastedList=new ArrayList<>();
                        Resource nastedResource=bookNasted.getChild("bookeditons");
                        for(Resource nasted : nastedResource.getChildren()){
                            bookNastedList.add(new NastedHalper(nasted));
                        }
                        multifieldHelper.setBookEditons(bookNastedList);
                    }
                    bookDetailsNasted.add(multifieldHelper);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details With Nasted Multifield {} ",e.getMessage());
        }
        LOG.info("\n SIZE Multifield {} ",bookDetailsNasted.size());
        return bookDetailsNasted;
    }

}
