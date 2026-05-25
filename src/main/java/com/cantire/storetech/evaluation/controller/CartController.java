package com.cantire.storetech.evaluation.controller;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for cart operations.
 */
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    /**
     * Adds a product to a cart (creates new cart if needed).
     *
     * @param request Cart save request with product and cart details
     * @return ResponseEntity with CartSaveResponse
     */
    @PostMapping
    public ResponseEntity<CartSaveResponse> addProductToCart(@RequestBody CartSaveRequest request) {
            // TODO: Call a service to add product to cart and return appropriate response
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
}