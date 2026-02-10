package com.cantire.storetech.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a product to a cart.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSaveRequest {

    private Long cartId;  // Optional - if null, a new cart will be created

    private Long productId;

    private Integer quantity;

    private String region;  // Province abbreviation (e.g., "AB", "ON", "BC")

    private String currencyCode;  // e.g., "CAD"
}
