package com.cantire.storetech.evaluation.controller;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.job.PurgingJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for cart operations.
 */
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final PurgingJob purgingJob;

    /**
     * Adds a product to a cart (creates new cart if needed).
     *
     * @param request Cart save request with product and cart details
     * @return ResponseEntity with CartSaveResponse
     */
    @PostMapping
    public ResponseEntity<CartSaveResponse> addProductToCart(@RequestBody CartSaveRequest request) {
        try {
            // TODO: Call a service to add product to cart and return appropriate response
            CartSaveResponse response = null;
            return ResponseEntity.status(response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                    .body(response);
        } catch (Exception e) {
            CartSaveResponse errorResponse = new CartSaveResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error processing cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/purge-stale")
    public ResponseEntity<Void> purgeStaleCarts(@RequestParam(value = "cutoffHours", required = false) Integer cutoffHours) {
        ZonedDateTime cutoffTime = ZonedDateTime.now().minusHours(Objects.requireNonNullElse(cutoffHours, 24));

        int purgedRecords = purgingJob.runPurge(cutoffTime);

        log.info("Purged {} stale carts not updated since {}", purgedRecords, cutoffTime);

        return ResponseEntity.noContent().build();
    }
}