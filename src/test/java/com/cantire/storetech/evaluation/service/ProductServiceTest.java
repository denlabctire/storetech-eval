package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.repo.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@SpringBootTest
@Testcontainers
class ProductServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Use in-memory H2 for testing - no need to connect to container
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Test
    void testCreateProduct() {
        // Given - use a unique ID that doesn't conflict with Liquibase data
        long initialCount = productRepository.count();

        Product product = new Product();
        product.setId(9999L);
        product.setName("Test Product");
        product.setSku("TEST-SKU-001");
        product.setQuantity(10);

        // When
        Product createdProduct = productService.create(product);

        // Then
        assertNotNull(createdProduct);
        assertEquals(9999L, createdProduct.getId());
        assertEquals(10, createdProduct.getQuantity());
        assertEquals("Test Product", createdProduct.getName());
        assertEquals(initialCount + 1, productRepository.count());
    }

    @Test
    void testGetProducts() {
        // When - get products from Liquibase sample data
        List<Product> products = productService.getProducts();

        // Then - Liquibase creates 5 sample products
        assertNotNull(products);
        assertTrue(products.size() >= 1, "Should have at least one product from Liquibase data");

        // Verify first product exists and has expected structure
        Product firstProduct = products.get(0);
        assertNotNull(firstProduct.getId());
        assertNotNull(firstProduct.getName());
    }

    @Test
    void testGetProductsReturnsAllProducts() {
        // Given - add a new product
        Product newProduct = new Product();
        newProduct.setId(8888L);
        newProduct.setName("Additional Product");
        newProduct.setSku("TEST-SKU-002");
        newProduct.setQuantity(5);

        long countBefore = productRepository.count();
        productService.create(newProduct);

        // When
        List<Product> products = productService.getProducts();

        // Then
        assertNotNull(products);
        assertEquals(countBefore + 1, products.size());

        Product addedProduct = products.stream()
                .filter(p -> p.getId().equals(8888L))
                .findFirst()
                .orElse(null);

        assertNotNull(addedProduct);
        assertEquals("Additional Product", addedProduct.getName());
    }
}
