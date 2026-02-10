package com.cantire.storetech.evaluation.converter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.PriceInfo;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.model.TaxInfo.TaxType;

/**
 * Unit tests for CartResponseConverter.
 */
class CartResponseConverterTest {

    private Cart cart;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Create products with price info
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setSku("SKU-001");
        product1.setQuantity(100);

        PriceInfo priceInfo1 = new PriceInfo();
        priceInfo1.setId(1L);
        priceInfo1.setProduct(product1);
        priceInfo1.setCurrencyCode("CAD");
        priceInfo1.setPrice(new BigDecimal("19.99"));
        priceInfo1.setEffectiveDate(ZonedDateTime.now().minusDays(1));
        priceInfo1.setExpiryDate(ZonedDateTime.now().plusDays(30));
        product1.setPriceInfos(new ArrayList<>(List.of(priceInfo1)));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setSku("SKU-002");
        product2.setQuantity(50);

        PriceInfo priceInfo2 = new PriceInfo();
        priceInfo2.setId(2L);
        priceInfo2.setProduct(product2);
        priceInfo2.setCurrencyCode("CAD");
        priceInfo2.setPrice(new BigDecimal("29.99"));
        priceInfo2.setEffectiveDate(ZonedDateTime.now().minusDays(1));
        priceInfo2.setExpiryDate(ZonedDateTime.now().plusDays(30));
        product2.setPriceInfos(new ArrayList<>(List.of(priceInfo2)));

        // Create cart
        cart = new Cart();
        cart.setId(1L);
        cart.setRegion("ON");
        cart.setCurrencyCode("CAD");
        cart.setSubtotal(new BigDecimal("69.97"));

        Set<Product> products = new HashSet<>();
        products.add(product1);
        products.add(product2);
        cart.setProducts(products);

        cart.getProductQuantities().put(1L, 2);
        cart.getProductQuantities().put(2L, 1);

        // Create tax info
        TaxInfo hst = new TaxInfo();
        hst.setId(1L);
        hst.setLocale("CA");
        hst.setStateProvince("ON");
        hst.setPercentage(13.0);
        hst.setTaxType(TaxType.HST);
        hst.setName("Ontario HST");

        cart.setApplicableTaxes(new ArrayList<>(List.of(hst)));
    }

    @Test
    void testToResponse_WithValidCart_ReturnsPopulatedResponse() {
        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getCartId());
        assertEquals(2, response.getTotalItems());
        assertEquals(new BigDecimal("69.97"), response.getSubtotal());
        assertEquals("CAD", response.getCurrencyCode());
        assertEquals("ON", response.getRegion());
        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
    }

    @Test
    void testToResponse_ContainsCorrectItems() {
        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response.getItems());
        assertEquals(2, response.getItems().size());

        // Find product1 item
        CartSaveResponse.CartItemResponse item1 = response.getItems().stream()
                .filter(item -> item.getProductId().equals(1L))
                .findFirst()
                .orElse(null);

        assertNotNull(item1);
        assertEquals("Test Product 1", item1.getProductName());
        assertEquals("SKU-001", item1.getSku());
        assertEquals(2, item1.getQuantity());
        assertEquals(new BigDecimal("19.99"), item1.getPrice());
        assertEquals("CAD", item1.getCurrencyCode());

        // Find product2 item
        CartSaveResponse.CartItemResponse item2 = response.getItems().stream()
                .filter(item -> item.getProductId().equals(2L))
                .findFirst()
                .orElse(null);

        assertNotNull(item2);
        assertEquals("Test Product 2", item2.getProductName());
        assertEquals("SKU-002", item2.getSku());
        assertEquals(1, item2.getQuantity());
        assertEquals(new BigDecimal("29.99"), item2.getPrice());
        assertEquals("CAD", item2.getCurrencyCode());
    }

    @Test
    void testToResponse_ContainsCorrectTaxBreakdown() {
        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response.getTaxBreakdown());
        assertEquals(1, response.getTaxBreakdown().size());

        CartSaveResponse.TaxBreakdownResponse taxBreakdown = response.getTaxBreakdown().get(0);
        assertEquals("HST", taxBreakdown.getTaxType());
        assertEquals(13.0, taxBreakdown.getPercentage());
        assertEquals("Ontario HST", taxBreakdown.getName());
    }

    @Test
    void testToResponse_WithMultipleTaxes_ReturnsAllTaxes() {
        // Given - Add PST and GST for BC
        TaxInfo gst = new TaxInfo();
        gst.setId(2L);
        gst.setLocale("CA");
        gst.setStateProvince("BC");
        gst.setPercentage(5.0);
        gst.setTaxType(TaxType.GST);
        gst.setName("BC GST");

        TaxInfo pst = new TaxInfo();
        pst.setId(3L);
        pst.setLocale("CA");
        pst.setStateProvince("BC");
        pst.setPercentage(7.0);
        pst.setTaxType(TaxType.PST);
        pst.setName("BC PST");

        cart.setApplicableTaxes(new ArrayList<>(List.of(gst, pst)));
        cart.setRegion("BC");

        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response.getTaxBreakdown());
        assertEquals(2, response.getTaxBreakdown().size());

        assertTrue(response.getTaxBreakdown().stream()
                .anyMatch(t -> t.getTaxType().equals("GST") && t.getPercentage().equals(5.0)));
        assertTrue(response.getTaxBreakdown().stream()
                .anyMatch(t -> t.getTaxType().equals("PST") && t.getPercentage().equals(7.0)));
    }

    @Test
    void testToResponse_WithNoTaxes_ReturnsEmptyTaxBreakdown() {
        // Given
        cart.setApplicableTaxes(null);

        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response.getTaxBreakdown());
        assertTrue(response.getTaxBreakdown().isEmpty());
    }

    @Test
    void testToResponse_WithFailure_ReturnsFalseSuccess() {
        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, false, "Error occurred");

        // Then
        assertNotNull(response);
        assertEquals(false, response.getSuccess());
        assertEquals("Error occurred", response.getMessage());
    }

    @Test
    void testToResponse_WithEmptyCart_ReturnsEmptyItems() {
        // Given
        cart.setProducts(new HashSet<>());

        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Empty cart");

        // Then
        assertNotNull(response);
        assertEquals(0, response.getTotalItems());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void testToResponse_WithNoPriceForCurrency_ItemHasNullPrice() {
        // Given - Product with USD price, but cart uses CAD
        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("USD Only Product");
        product3.setSku("SKU-003");
        product3.setQuantity(10);

        PriceInfo usdPrice = new PriceInfo();
        usdPrice.setId(3L);
        // Note: Not setting product reference to avoid circular reference in hashCode
        usdPrice.setCurrencyCode("USD");
        usdPrice.setPrice(new BigDecimal("15.00"));
        usdPrice.setEffectiveDate(ZonedDateTime.now().minusDays(1));
        usdPrice.setExpiryDate(ZonedDateTime.now().plusDays(30));
        product3.setPriceInfos(new ArrayList<>(List.of(usdPrice)));

        cart.setProducts(new HashSet<>(Set.of(product3)));
        cart.getProductQuantities().clear();
        cart.getProductQuantities().put(3L, 1);

        // When
        CartSaveResponse response = CartResponseConverter.toResponse(cart, true, "Success");

        // Then
        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());

        CartSaveResponse.CartItemResponse item = response.getItems().get(0);
        assertEquals(3L, item.getProductId());
        // Price should be null since no CAD price exists
        assertEquals(null, item.getPrice());
    }
}
