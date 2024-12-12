package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.AuthorBio;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AuthorBioImplTest {

    AemContext aemContext = new AemContext();
    AuthorBio author;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(AuthorBioImpl.class);
        aemContext.load().json("/com/adobe/aem/guides/wknd/core/models/impl/AuthorBioImpl.json","/component");
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
    }

    @Test
    void getBooks() {
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals(3, author.getBooks().size());
        assertEquals("AEM Design", author.getBooks().get(0));
    }

    @Test
    void getBooks_EmptyBooks() {
        aemContext.currentResource("/component/authorbio-without-books");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals(0, author.getBooks().size());
    }

    @Test
    void getFirstName() {
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals("SHIVA", author.getFirstName());
    }

    @Test
    void getLastName() {
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals("J", author.getLastName());
    }

    @Test
    void getIsProfessor() {
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals(true, author.getIsProfessor());
    }


    @Test
    void getBookDetailsWithMap() {
        aemContext.currentResource("/component/authorbio");
        author = aemContext.request().adaptTo(AuthorBio.class);
        assertEquals(2,author.getBookDetailsWithMap().size());
        assertEquals("2024", author.getBookDetailsWithMap().get(0).get("publishyear"));
        assertEquals("AEM Design Patterns", author.getBookDetailsWithMap().get(0).get("booksubject"));
        assertEquals("AEM Design", author.getBookDetailsWithMap().get(0).get("bookname"));

    }
}