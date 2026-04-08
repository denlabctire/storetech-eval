package com.cantire.storetech.evaluation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.cantire.storetech.evaluation.model.Customer;

@SpringBootTest
@Testcontainers
class CustomerServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");

    @Autowired
    private CustomerService customerService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Test
    void testGetCustomer_existing() {
        Customer customer = customerService.getCustomer(1L);

        assertNotNull(customer);
        assertEquals("Jane", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertTrue(customer.isAutoPayEnabled());
        assertEquals(1, customer.getAssociatedCreditCardIds().size());
        assertEquals("CC-1111-2222-3333", customer.getAssociatedCreditCardIds().get(0));
    }

    @Test
    void testGetCustomer_secondCustomer() {
        Customer customer = customerService.getCustomer(2L);

        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertFalse(customer.isAutoPayEnabled());
        assertTrue(customer.getAssociatedCreditCardIds().isEmpty());
    }

    @Test
    void testGetCustomer_notFound() {
        assertThrows(ObjectNotFoundException.class, () -> customerService.getCustomer(9999L));
    }

    @Test
    void testUpdateCustomerAutoPay_enableWithNewCard() {
        Customer updated = customerService.updateCustomerAutoPay(2L, true, "CC-9999-8888-7777");

        assertTrue(updated.isAutoPayEnabled());
        assertTrue(updated.getAssociatedCreditCardIds().contains("CC-9999-8888-7777"));
    }

    @Test
    void testUpdateCustomerAutoPay_disableAutoPay() {
        Customer updated = customerService.updateCustomerAutoPay(1L, false, null);

        assertFalse(updated.isAutoPayEnabled());
    }

    @Test
    void testUpdateCustomerAutoPay_duplicateCardNotAdded() {
        Customer before = customerService.getCustomer(1L);
        int cardCountBefore = before.getAssociatedCreditCardIds().size();

        Customer updated = customerService.updateCustomerAutoPay(1L, true, "CC-1111-2222-3333");

        assertEquals(cardCountBefore, updated.getAssociatedCreditCardIds().size());
    }

    @Test
    void testUpdateCustomerAutoPay_notFound() {
        assertThrows(ObjectNotFoundException.class,
                () -> customerService.updateCustomerAutoPay(9999L, true, "CC-0000"));
    }
}
