package com.cantire.storetech.evaluation.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.ProductCategory;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.repo.CartRepository;
import com.cantire.storetech.evaluation.repo.ProductRepository;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;

/**
 * SpringBootTest for CartService.
 * Tests adding valid and invalid products to cart.
 */
@Disabled
@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TaxInfoRepository taxInfoRepository;

    private Product validProduct;
    private ProductCategory category;
    private long validProductId;

    @BeforeEach
    void setUp() {

        // Create a product category
        category = new ProductCategory();
        category.setId(1L);
        category.setName("Tools");

        validProduct = productRepository.findAll().get(0);
        validProductId = validProduct.getId();

        // Create tax info for Ontario
        TaxInfo hstInfo = new TaxInfo();
        hstInfo.setLocale("en-CA");
        hstInfo.setStateProvince("ON");
        hstInfo.setPercentage(8.0);
        hstInfo.setTaxType(TaxInfo.TaxType.HST);
        hstInfo.setName("Harmonized Sales Tax");

        TaxInfo fstInfo = new TaxInfo();
        fstInfo.setLocale("en-CA");
        fstInfo.setStateProvince("ON");
        fstInfo.setPercentage(5.0);
        fstInfo.setTaxType(TaxInfo.TaxType.GST);
        fstInfo.setName("Goods and Services Tax");

        taxInfoRepository.save(hstInfo);
        taxInfoRepository.save(fstInfo);
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

        assertNotNull(response);
        assertTrue(response.getSuccess(), "Response should be successful");
        assertNotNull(response.getCartId(), "Cart ID should be assigned");
        assertEquals(2, response.getTotalItems(), "Cart should have 2 items");
        assertEquals(new BigDecimal("59.98"), response.getSubtotal(), "Subtotal should be 29.99 * 2");
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

        // Now add another unit of the same product to the existing cart
        CartSaveRequest secondRequest = new CartSaveRequest();
        secondRequest.setCartId(cartId);
        secondRequest.setProductId(validProductId);
        secondRequest.setQuantity(1);
        secondRequest.setRegion("ON");
        secondRequest.setCurrencyCode("CAD");

        CartSaveResponse secondResponse = cartService.addProductToCart(secondRequest);

        assertNotNull(secondResponse);
        assertTrue(secondResponse.getSuccess());
        assertEquals(cartId, secondResponse.getCartId(), "Cart ID should remain the same");
        assertEquals(1, secondResponse.getTotalItems(), "Should still have 1 product type");
        assertEquals(new BigDecimal("59.98"), secondResponse.getSubtotal(), "Subtotal should reflect total quantity");
    }

    /**
     * Test: Adding an invalid product (product does not exist).
     * Expected: Cart operation should fail with appropriate error message.
     */
    @Test
    void testAddInvalidProductToCart() {
        CartSaveRequest request = new CartSaveRequest();
        request.setCartId(null);
        request.setProductId(9999L);  // Non-existent product
        request.setQuantity(1);
        request.setRegion("ON");
        request.setCurrencyCode("CAD");

        CartSaveResponse response = cartService.addProductToCart(request);

        assertNotNull(response);
        assertFalse(response.getSuccess(), "Response should fail for invalid product");
        assertTrue(response.getMessage().contains("not found") || response.getMessage().contains("invalid") || response.getMessage().contains("Error"),
                "Error message should indicate product issue");
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

        CartSaveResponse response = cartService.addProductToCart(request);

        assertNotNull(response);
        assertFalse(response.getSuccess(), "Response should fail for unavailable currency");
    }
}
