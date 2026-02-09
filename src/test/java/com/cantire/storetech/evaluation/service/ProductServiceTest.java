package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.HiearchyLevel;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.ProductCategory;
import com.cantire.storetech.evaluation.repo.ProductRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class ProductServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Use in-memory H2 for testing - no need to connect to container
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        productRepository.deleteAll();
    }

    @Test
    void testCreateProduct() {
        // Given
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setName("Electronics");
        category.setHierarchyLevel(HiearchyLevel.LEVEL_1);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(10);
        product.setCategory(category);

        // When
        Product createdProduct = productService.create(product);

        // Then
        assertNotNull(createdProduct);
        assertEquals(1L, createdProduct.getId());
        assertEquals(10, createdProduct.getQuantity());
        assertNotNull(createdProduct.getCategory());
        assertEquals("Electronics", createdProduct.getCategory().getName());
        assertEquals(HiearchyLevel.LEVEL_1, createdProduct.getCategory().getHierarchyLevel());
    }

    @Test
    void testGetProducts() {
        // Given
        ProductCategory category1 = new ProductCategory();
        category1.setId(1L);
        category1.setName("Electronics");
        category1.setHierarchyLevel(HiearchyLevel.LEVEL_1);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setQuantity(10);
        product1.setCategory(category1);

        ProductCategory category2 = new ProductCategory();
        category2.setId(2L);
        category2.setName("Home & Garden");
        category2.setHierarchyLevel(HiearchyLevel.LEVEL_2);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setQuantity(5);
        product2.setCategory(category2);

        productService.create(product1);
        productService.create(product2);

        // When
        List<Product> products = productService.getProducts();

        // Then
        assertNotNull(products);
        assertEquals(2, products.size());

        Product retrievedProduct1 = products.stream()
                .filter(p -> p.getId().equals(1L))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedProduct1);
        assertEquals(10, retrievedProduct1.getQuantity());
        assertEquals("Electronics", retrievedProduct1.getCategory().getName());

        Product retrievedProduct2 = products.stream()
                .filter(p -> p.getId().equals(2L))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedProduct2);
        assertEquals(5, retrievedProduct2.getQuantity());
        assertEquals("Home & Garden", retrievedProduct2.getCategory().getName());
    }

    @Test
    void testGetProductsWhenEmpty() {
        // When
        List<Product> products = productService.getProducts();

        // Then
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }
}
