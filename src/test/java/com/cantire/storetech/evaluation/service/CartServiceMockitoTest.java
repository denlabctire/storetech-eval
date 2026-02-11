package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.exception.InvalidCurrencyCodeException;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.PriceInfo;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.model.TaxInfo.TaxType;
import com.cantire.storetech.evaluation.repo.CartRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CartService using Mockito for stubbing and verification.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceMockitoTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private TaxService taxService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Product validProduct;
    private Product otherProduct;
    private List<TaxInfo> ontarioTaxes;

    @BeforeEach
    void setUp() {
        validProduct = createProduct(1L, "Test Product 1", "SKU-001", new BigDecimal("29.99"));
        otherProduct = createProduct(2L, "Test Product 2", "SKU-002", new BigDecimal("49.99"));
        ontarioTaxes = createOntarioTaxes();
    }

    /**
     * Test: Adding a valid product to a new cart (no cart ID provided).
     * Expected: New cart is created with product, subtotal is calculated correctly.
     */
    @Test
    void testAddValidProductToNewCart() {
        // Given
        CartSaveRequest request = createCartSaveRequest(null, 1L, 2, "ON", "CAD");

        when(productService.getProduct(1L)).thenReturn(Optional.of(validProduct));
        when(taxService.getTaxesForRegion("ON", "CAD")).thenReturn(ontarioTaxes);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(100L);
            return cart;
        });

        // When
        CartSaveResponse response = cartService.addProductToCart(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getSuccess(), "Response should be successful");
        assertNotNull(response.getCartId(), "Cart ID should be assigned");
        assertEquals(2, response.getTotalItems(), "Cart should have 2 items");
        assertEquals(new BigDecimal("59.98"), response.getSubtotal(), "Subtotal should be 29.99 * 2");
        assertEquals("CAD", response.getCurrencyCode());
        assertEquals("ON", response.getRegion());
        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());

        verify(productService).getProduct(1L);
        verify(taxService).getTaxesForRegion("ON", "CAD");
        verify(cartRepository).save(any(Cart.class));
        verify(cartRepository, never()).findById(anyLong());
    }

    /**
     * Test: Adding a valid product to an existing cart.
     * Expected: Product is added to existing cart, subtotal is updated.
     */
    @Test
    void testAddValidProductToExistingCart() {
        // Given - First create a cart with one product
        Cart existingCart = createExistingCart(100L, validProduct, 1, "ON", "CAD");

        CartSaveRequest request = createCartSaveRequest(100L, 2L, 3, "ON", "CAD");

        when(productService.getProduct(2L)).thenReturn(Optional.of(otherProduct));
        when(taxService.getTaxesForRegion("ON", "CAD")).thenReturn(ontarioTaxes);
        when(cartRepository.findById(100L)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CartSaveResponse response = cartService.addProductToCart(request);

        // Then
        BigDecimal expectedSubtotal = new BigDecimal("29.99")
                .add(new BigDecimal("49.99").multiply(new BigDecimal(3)));

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(100L, response.getCartId(), "Cart ID should remain the same");
        assertEquals(4, response.getTotalItems(), "Should have 4 total items (1 + 3)");
        assertEquals(expectedSubtotal, response.getSubtotal(), "Subtotal should reflect total quantity");

        verify(cartRepository).findById(100L);
        verify(productService).getProduct(2L);
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Test: Adding an invalid product (product does not exist).
     * Expected: Cart operation should fail with appropriate error message.
     */
    @Test
    void testAddInvalidProductToCart() {
        // Given
        CartSaveRequest request = createCartSaveRequest(null, 1234567890L, 1, "ON", "CAD");

        when(productService.getProduct(1234567890L)).thenReturn(Optional.empty());

        // When/Then
        Assertions.assertThrows(ObjectNotFoundException.class, () -> cartService.addProductToCart(request));

        verify(productService).getProduct(1234567890L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * Test: Adding product with invalid currency.
     * Expected: Cart operation should fail if price not available for currency.
     */
    @Test
    void testAddProductWithUnavailableCurrency() {
        // Given
        Product productWithNoPriceForCurrency = createProductWithNoPriceForCurrency(1L, "No USD Price", "SKU-003");
        CartSaveRequest request = createCartSaveRequest(null, 1L, 1, "ON", "USD");

        when(productService.getProduct(1L)).thenReturn(Optional.of(productWithNoPriceForCurrency));
        when(taxService.getTaxesForRegion(anyString(), anyString()))
                .thenThrow(new InvalidCurrencyCodeException("No price available for currency: USD"));

        // When/Then
        Assertions.assertThrows(InvalidCurrencyCodeException.class, () -> cartService.addProductToCart(request));

        verify(productService).getProduct(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    // ========== Private Helper Methods for Test Data Creation ==========

    private CartSaveRequest createCartSaveRequest(Long cartId, Long productId, int quantity,
                                                   String region, String currencyCode) {
        CartSaveRequest request = new CartSaveRequest();
        request.setCartId(cartId);
        request.setProductId(productId);
        request.setQuantity(quantity);
        request.setRegion(region);
        request.setCurrencyCode(currencyCode);
        return request;
    }

    private Product createProduct(Long id, String name, String sku, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSku(sku);
        product.setQuantity(100);

        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setId(id);
        priceInfo.setCurrencyCode("CAD");
        priceInfo.setPrice(price);
        priceInfo.setEffectiveDate(ZonedDateTime.now().minusDays(30));
        priceInfo.setExpiryDate(ZonedDateTime.now().plusDays(30));

        product.setPriceInfos(new ArrayList<>(List.of(priceInfo)));
        return product;
    }

    private Product createProductWithNoPriceForCurrency(Long id, String name, String sku) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSku(sku);
        product.setQuantity(100);

        // Only CAD price, no USD
        PriceInfo cadPrice = new PriceInfo();
        cadPrice.setId(id);
        cadPrice.setCurrencyCode("CAD");
        cadPrice.setPrice(new BigDecimal("19.99"));
        cadPrice.setEffectiveDate(ZonedDateTime.now().minusDays(30));
        cadPrice.setExpiryDate(ZonedDateTime.now().plusDays(30));

        product.setPriceInfos(new ArrayList<>(List.of(cadPrice)));
        return product;
    }

    private Cart createExistingCart(Long cartId, Product product, int quantity,
                                     String region, String currencyCode) {
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setRegion(region);
        cart.setCurrencyCode(currencyCode);
        cart.addProduct(product, quantity);
        return cart;
    }

    private List<TaxInfo> createOntarioTaxes() {
        TaxInfo hst = new TaxInfo();
        hst.setId(1L);
        hst.setCountryCode("CA");
        hst.setStateProvince("ON");
        hst.setPercentage(13.0);
        hst.setTaxType(TaxType.HST);
        hst.setName("Ontario HST");
        return new ArrayList<>(List.of(hst));
    }
}
