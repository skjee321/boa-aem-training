package com.adobe.aem.guides.wknd.core.services.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class OSGiConfigImplTest {

    AemContext aemContext = new AemContext();

    OSGiConfigImpl configTest;

    @BeforeEach
    void setUp() {
        configTest = aemContext.registerService(new OSGiConfigImpl());
        OSGiConfigImpl.ServiceConfig config = mock(OSGiConfigImpl.ServiceConfig.class);
        when(config.serviceName()).thenReturn("WKND OSGi Service");
        when(config.getServiceCount()).thenReturn(2);
        when(config.getRunMode()).thenReturn("author");
        when(config.getLiveData()).thenReturn(true);
        when(config.getCountries()).thenReturn(new String[]{"in","de"});
        configTest.activate(config);
    }

    @Test
    void activate() {

    }

    @Test
    void getServiceName() {
        assertEquals("WKND OSGi Service",configTest.getServiceName());
    }

    @Test
    void getServiceCount() {
        assertEquals(2, configTest.getServiceCount());
    }

    @Test
    void isLiveData() {
        assertEquals(true, configTest.isLiveData());
    }

    @Test
    void getCountries() {
        assertAll(
                () -> assertEquals(2, configTest.getCountries().length),
                () -> assertEquals("in", configTest.getCountries()[0])
        );
    }

    @Test
    void getRunModes() {
        assertEquals("author", configTest.getRunModes());
    }
}