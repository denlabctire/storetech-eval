package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;

public interface CartService {

    /**
     * Add a product to a cart (create cart if needed) and return save response.
     *
     * @param request request payload
     * @return response with cart details and status
     */
    CartSaveResponse addProductToCart(CartSaveRequest request);

    static void purgeStaleCarts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'purgeStaleCarts'");
    }

}
