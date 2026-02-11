package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.exception.InvalidCurrencyCodeException;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.repo.CartRepository;
import com.cantire.storetech.evaluation.repo.ProductRepository;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SpringBootTest for CartService.
 * Tests adding valid and invalid products to cart.
 */
@SpringBootTest
@Testcontainers
class CartServiceTest {

    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2:latest"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists");
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TaxInfoRepository taxInfoRepository;
    private Product validProduct;
    private long validProductId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Use in-memory H2 for testing - no need to connect to container
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @BeforeEach
    void setUp() {

        validProduct = productRepository.findAll().get(0);
        validProductId = validProduct.getId();

    }

    /**
     * Test: Adding a valid product to a new cart (no cart ID provided).
     * Expected: New cart is created with product, subtotal is calculated correctly.
     */
    @Test
    void testAddValidProductToNewCart() {
        CartSaveRequest request = new CartSaveRequest();
        request.setCartId(null);  // New cart
        request.setProductId(validProductId);
        request.setQuantity(2);
        request.setRegion("ON");
        request.setCurrencyCode("CAD");

        CartSaveResponse response = cartService.addProductToCart(request);
        BigDecimal productPrice = validProduct.getPriceInfos().get(0).getPrice();
        assertNotNull(response);
        assertTrue(response.getSuccess(), "Response should be successful");
        assertNotNull(response.getCartId(), "Cart ID should be assigned");
        assertEquals(2, response.getTotalItems(), "Cart should have 2 items");
        assertEquals(productPrice.multiply(new BigDecimal(2)), response.getSubtotal(), "Subtotal should be %s * 2".formatted(productPrice));
        assertEquals("CAD", response.getCurrencyCode());
        assertEquals("ON", response.getRegion());
        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());
    }

    /**
     * Test: Adding a valid product to an existing cart.
     * Expected: Product is added to existing cart, subtotal is updated.
     */
    @Test
    void testAddValidProductToExistingCart() {
        // First, create a cart with one product
        CartSaveRequest firstRequest = new CartSaveRequest();
        firstRequest.setCartId(null);
        firstRequest.setProductId(validProductId);
        firstRequest.setQuantity(1);
        firstRequest.setRegion("ON");
        firstRequest.setCurrencyCode("CAD");

        CartSaveResponse firstResponse = cartService.addProductToCart(firstRequest);
        Long cartId = firstResponse.getCartId();

        Product otherProduct = productRepository.findAll().get(1);

        CartSaveRequest secondRequest = new CartSaveRequest();
        secondRequest.setCartId(cartId);
        secondRequest.setProductId(otherProduct.getId());
        secondRequest.setQuantity(3);
        secondRequest.setRegion("ON");
        secondRequest.setCurrencyCode("CAD");

        CartSaveResponse secondResponse = cartService.addProductToCart(secondRequest);

        BigDecimal calculatedSubtotal = validProduct.getPriceInfos().get(0).getPrice()
                .add(otherProduct.getPriceInfos().get(0).getPrice().multiply(new BigDecimal(3)));

        assertNotNull(secondResponse);
        assertTrue(secondResponse.getSuccess());
        assertEquals(cartId, secondResponse.getCartId(), "Cart ID should remain the same");
        assertEquals(4, secondResponse.getTotalItems(), "Should have 4 product type");
        assertEquals(calculatedSubtotal, secondResponse.getSubtotal(), "Subtotal should reflect total quantity");
    }

    /**
     * Test: Adding an invalid product (product does not exist).
     * Expected: Cart operation should fail with appropriate error message.
     */
    @Test
    void testAddInvalidProductToCart() {
        CartSaveRequest request = new CartSaveRequest();
        request.setCartId(null);
        request.setProductId(1234567890L);  // Non-existent product
        request.setQuantity(1);
        request.setRegion("ON");
        request.setCurrencyCode("CAD");

        Assertions.assertThrows(ObjectNotFoundException.class, () -> cartService.addProductToCart(request));
    }

    /**
     * Test: Adding product with invalid currency.
     * Expected: Cart operation should fail if price not available for currency.
     */
    @Test
    void testAddProductWithUnavailableCurrency() {
        CartSaveRequest request = new CartSaveRequest();
        request.setCartId(null);
        request.setProductId(validProductId);
        request.setQuantity(1);
        request.setRegion("ON");

        request.setCurrencyCode("USD");  // No USD price info exists

        Assertions.assertThrows(InvalidCurrencyCodeException.class, () -> cartService.addProductToCart(request));
    }
}
