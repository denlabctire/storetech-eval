package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.model.TaxInfo.TaxType;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class TaxServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");
    @Autowired
    private TaxService taxService;
    @Autowired
    private TaxInfoRepository taxInfoRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Test
    void testGetTaxesForRegion_ReturnsMatchingTaxes() {
        // When - use existing Liquibase data for Ontario (has HST)
        List<TaxInfo> ontarioTaxes = taxService.getTaxesForRegion("ON", Currency.getInstance("CAD").getCurrencyCode());

        // Then - Ontario should have HST from Liquibase sample data
        assertNotNull(ontarioTaxes);
        assertTrue(ontarioTaxes.size() >= 1, "Ontario should have at least one tax (HST)");
        assertTrue(ontarioTaxes.stream().anyMatch(t -> t.getTaxType() == TaxType.HST),
                "Ontario should have HST");
    }

    @Test
    void testGetTaxesForRegion_ReturnsEmptyListWhenNoMatch() {
        // When - use a region that doesn't exist in Liquibase data
        List<TaxInfo> nonExistentRegionTaxes = taxService.getTaxesForRegion("XX", Currency.getInstance("CAD").getCurrencyCode());

        // Then
        assertNotNull(nonExistentRegionTaxes);
        assertTrue(nonExistentRegionTaxes.isEmpty());
    }

    @Test
    void testGetTaxesForRegion_ReturnsMultipleTaxTypesForSameRegion() {
        // When - BC should have both GST and PST from Liquibase sample data
        List<TaxInfo> bcTaxes = taxService.getTaxesForRegion("BC", Currency.getInstance("CAD").getCurrencyCode());

        // Then - BC should have GST and PST
        assertNotNull(bcTaxes);
        assertTrue(bcTaxes.size() >= 2, "BC should have at least 2 taxes (GST and PST)");
        assertTrue(bcTaxes.stream().anyMatch(t -> t.getTaxType() == TaxType.GST),
                "BC should have GST");
        assertTrue(bcTaxes.stream().anyMatch(t -> t.getTaxType() == TaxType.PST),
                "BC should have PST");
    }

    @Test
    void testGetTaxesForRegion_CanAddNewTaxAndRetrieve() {
        // Given - Add a new tax for a unique region
        TaxInfo newTax = new TaxInfo();
        newTax.setCountryCode(Locale.CANADA.getCountry());
        newTax.setStateProvince("YT"); // Yukon - unlikely to be in Liquibase data
        newTax.setPercentage(5.0);
        newTax.setTaxType(TaxType.GST);
        newTax.setName("Yukon GST");
        taxInfoRepository.save(newTax);

        // When
        List<TaxInfo> yukonTaxes = taxService.getTaxesForRegion("YT", Currency.getInstance("CAD").getCurrencyCode());

        // Then
        assertNotNull(yukonTaxes);
        assertTrue(yukonTaxes.size() >= 1);
        assertTrue(yukonTaxes.stream().anyMatch(t -> t.getName().equals("Yukon GST")));
    }
}
