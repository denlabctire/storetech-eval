package com.cantire.storetech.evaluation.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Product with pricing information.
 * Includes price and currency but omits date information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String sku;

    private Integer quantity;

    private String categoryName;

    private List<PricingInfo> prices;

    /**
     * Pricing information for a product.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingInfo {
        private String currencyCode;
        private BigDecimal price;
    }
}
