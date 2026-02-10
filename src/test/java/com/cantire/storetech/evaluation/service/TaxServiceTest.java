package com.cantire.storetech.evaluation.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.model.TaxInfo.TaxType;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;

@SpringBootTest
@Testcontainers
class TaxServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Autowired
    private TaxService taxService;

    @Autowired
    private TaxInfoRepository taxInfoRepository;

    @BeforeEach
    void setUp() {
        taxInfoRepository.deleteAll();
    }

    @Test
    void testGetTaxesForRegion_ReturnsMatchingTaxes() {
        // Given
        TaxInfo ontarioHst = new TaxInfo();
        ontarioHst.setLocale("CA");
        ontarioHst.setStateProvince("ON");
        ontarioHst.setPercentage(13.0);
        ontarioHst.setTaxType(TaxType.HST);
        ontarioHst.setName("Ontario HST");
        taxInfoRepository.save(ontarioHst);

        TaxInfo bcGst = new TaxInfo();
        bcGst.setLocale("CA");
        bcGst.setStateProvince("BC");
        bcGst.setPercentage(5.0);
        bcGst.setTaxType(TaxType.GST);
        bcGst.setName("BC GST");
        taxInfoRepository.save(bcGst);

        TaxInfo bcPst = new TaxInfo();
        bcPst.setLocale("CA");
        bcPst.setStateProvince("BC");
        bcPst.setPercentage(7.0);
        bcPst.setTaxType(TaxType.PST);
        bcPst.setName("BC PST");
        taxInfoRepository.save(bcPst);

        // When
        List<TaxInfo> ontarioTaxes = taxService.getTaxesForRegion("ON");
        List<TaxInfo> bcTaxes = taxService.getTaxesForRegion("BC");

        // Then
        assertNotNull(ontarioTaxes);
        assertEquals(1, ontarioTaxes.size());
        assertEquals("Ontario HST", ontarioTaxes.get(0).getName());
        assertEquals(TaxType.HST, ontarioTaxes.get(0).getTaxType());
        assertEquals(13.0, ontarioTaxes.get(0).getPercentage());

        assertNotNull(bcTaxes);
        assertEquals(2, bcTaxes.size());
        assertTrue(bcTaxes.stream().anyMatch(t -> t.getTaxType() == TaxType.GST));
        assertTrue(bcTaxes.stream().anyMatch(t -> t.getTaxType() == TaxType.PST));
    }

    @Test
    void testGetTaxesForRegion_ReturnsEmptyListWhenNoMatch() {
        // Given
        TaxInfo ontarioHst = new TaxInfo();
        ontarioHst.setLocale("CA");
        ontarioHst.setStateProvince("ON");
        ontarioHst.setPercentage(13.0);
        ontarioHst.setTaxType(TaxType.HST);
        ontarioHst.setName("Ontario HST");
        taxInfoRepository.save(ontarioHst);

        // When
        List<TaxInfo> albertaTaxes = taxService.getTaxesForRegion("AB");

        // Then
        assertNotNull(albertaTaxes);
        assertTrue(albertaTaxes.isEmpty());
    }

    @Test
    void testGetTaxesForRegion_ReturnsMultipleTaxTypesForSameRegion() {
        // Given - Quebec has both GST and QST (PST)
        TaxInfo quebecGst = new TaxInfo();
        quebecGst.setLocale("CA");
        quebecGst.setStateProvince("QC");
        quebecGst.setPercentage(5.0);
        quebecGst.setTaxType(TaxType.GST);
        quebecGst.setName("Quebec GST");
        taxInfoRepository.save(quebecGst);

        TaxInfo quebecPst = new TaxInfo();
        quebecPst.setLocale("CA");
        quebecPst.setStateProvince("QC");
        quebecPst.setPercentage(9.975);
        quebecPst.setTaxType(TaxType.PST);
        quebecPst.setName("Quebec QST");
        taxInfoRepository.save(quebecPst);

        // When
        List<TaxInfo> quebecTaxes = taxService.getTaxesForRegion("QC");

        // Then
        assertNotNull(quebecTaxes);
        assertEquals(2, quebecTaxes.size());

        double totalTaxPercentage = quebecTaxes.stream()
                .mapToDouble(TaxInfo::getPercentage)
                .sum();
        assertEquals(14.975, totalTaxPercentage, 0.001);
    }
}
