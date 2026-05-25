package com.cantire.storetech.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Response DTO for cart save operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSaveResponse {

    private Long cartId;

    private Integer totalItems; // sum of all item quantities in the cart

    private BigDecimal subtotal;

    private ZonedDateTime createdAt;

    private String currencyCode;

    private String region;

    private List<CartItemResponse> items;

    private List<TaxBreakdownResponse> taxBreakdown;

    private String message;

    private Boolean success;

    /**
     * Represents a single item in the cart.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal price;
        private String currencyCode;
    }

    /**
     * Tax breakdown for the cart.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxBreakdownResponse {
        private String taxType;
        private Double percentage;
        private String name;
    }
}
