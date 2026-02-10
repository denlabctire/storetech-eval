package com.cantire.storetech.evaluation.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.PriceInfo;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.repo.CartRepository;
import com.cantire.storetech.evaluation.repo.ProductRepository;
import com.cantire.storetech.evaluation.repo.TaxInfoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing shopping cart operations.
 * CANDIDATE TASK: Implement the logic to add products to cart and calculate subtotal.
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final TaxInfoRepository taxInfoRepository;

    /**
     * Adds a product to a cart. Creates a new cart if it doesn't exist.
     *
     * CANDIDATE: Implement this method with the following requirements:
     * 1. If cartId is null, create a new Cart with the provided region and currencyCode
     * 2. If cartId exists, retrieve the persisted Cart
     * 3. Validate that the product exists in the database
     * 4. Add the product to the cart with the specified quantity
     * 5. Calculate the subtotal based on the product's current price for the specified currency
     * 6. Retrieve applicable taxes for the region
     * 7. Persist the cart to database
     * 8. Return a CartSaveResponse with appropriate status and data
     *
     * @param request The cart save request containing product and cart details
     * @return CartSaveResponse with cart details and status
     */
    @Override
    @Transactional
    public CartSaveResponse addProductToCart(CartSaveRequest request) {
        // TODO: Implement this method
        // Placeholder implementation
        throw new UnsupportedOperationException("This method must be implemented by the candidate");
    }

    /**
     * Helper method to find current price for a product in a specific currency.
     *
     * @param product The product
     * @param currencyCode The currency code
     * @return Optional containing the current price, or empty if not found
     */
    protected Optional<BigDecimal> findCurrentPrice(Product product, String currencyCode) {
        ZonedDateTime now = ZonedDateTime.now();
        return product.getPriceInfos().stream()
                .filter(price -> price.getCurrencyCode().equals(currencyCode))
                .filter(price -> price.getEffectiveDate().isBefore(now) || price.getEffectiveDate().isEqual(now))
                .filter(price -> price.getExpiryDate().isAfter(now) || price.getExpiryDate().isEqual(now))
                .map(PriceInfo::getPrice)
                .findFirst();
    }

    /**
     * Helper method to get applicable taxes for a region.
     * 
     * @param region The region (province abbreviation)
     * @return List of applicable TaxInfo for the region
     */
    protected List<TaxInfo> getTaxesForRegion(String region) {
        return taxInfoRepository.findByStateProvince(region);
    }

    /**
     * Helper method to calculate subtotal from products in cart.
     *
     * @param cart The cart
     * @return Calculated subtotal
     */
    protected BigDecimal calculateSubtotal(Cart cart) {
        return cart.getProducts().stream()
                .map(product -> {
                    Integer quantity = cart.getProductQuantity(product.getId());
                    Optional<BigDecimal> price = findCurrentPrice(product, cart.getCurrencyCode());
                    return price.map(p -> p.multiply(new BigDecimal(quantity)))
                            .orElse(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Converts cart to response DTO.
     *
     * @param cart The cart entity
     * @return CartSaveResponse with populated data
     */
    protected CartSaveResponse toResponse(Cart cart, boolean success, String message) {
        CartSaveResponse response = new CartSaveResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(cart.getProducts().size());
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
            
            Optional<BigDecimal> price = findCurrentPrice(product, cart.getCurrencyCode());
            price.ifPresent(item::setPrice);
            item.setCurrencyCode(cart.getCurrencyCode());
            
            items.add(item);
        }
        response.setItems(items);

        // Build tax breakdown
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
        response.setTaxBreakdown(taxes);

        return response;
    }
}
