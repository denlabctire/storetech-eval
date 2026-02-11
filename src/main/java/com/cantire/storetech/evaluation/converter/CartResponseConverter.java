package com.cantire.storetech.evaluation.converter;

import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.TaxInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartResponseConverter {

    /**
     * Converts cart to response DTO.
     *
     * @param cart The cart entity
     * @return CartSaveResponse with populated data
     */
    public static CartSaveResponse toResponse(Cart cart, boolean success, String message) {
        CartSaveResponse response = new CartSaveResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(cart.getProductQuantities().values().stream().mapToInt(Integer::intValue).sum());
        response.setSubtotal(cart.getSubtotal());
        response.setCurrencyCode(cart.getCurrencyCode());
        response.setRegion(cart.getRegion());
        response.setSuccess(success);
        response.setMessage(message);

        // Build item responses
        List<CartSaveResponse.CartItemResponse> items = new ArrayList<>();
        for (Product product : cart.getProducts()) {
            CartSaveResponse.CartItemResponse item = new CartSaveResponse.CartItemResponse();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setSku(product.getSku());
            item.setQuantity(cart.getProductQuantity(product.getId()));

            Optional<BigDecimal> price = Product.findCurrentPrice(product, cart.getCurrencyCode());
            price.ifPresent(item::setPrice);
            item.setCurrencyCode(cart.getCurrencyCode());

            items.add(item);
        }
        response.setItems(items);

        // Build tax breakdown
        List<CartSaveResponse.TaxBreakdownResponse> taxes = getTaxBreakdownResponses(cart);
        response.setTaxBreakdown(taxes);

        return response;
    }

    private static List<CartSaveResponse.TaxBreakdownResponse> getTaxBreakdownResponses(Cart cart) {
        List<CartSaveResponse.TaxBreakdownResponse> taxes = new ArrayList<>();
        if (cart.getApplicableTaxes() != null) {
            for (TaxInfo tax : cart.getApplicableTaxes()) {
                CartSaveResponse.TaxBreakdownResponse taxResponse = new CartSaveResponse.TaxBreakdownResponse();
                taxResponse.setTaxType(tax.getTaxType().name());
                taxResponse.setPercentage(tax.getPercentage());
                taxResponse.setName(tax.getName());
                taxes.add(taxResponse);
            }
        }
        return taxes;
    }
}
