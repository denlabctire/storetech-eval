package com.cantire.storetech.evaluation.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cantire.storetech.evaluation.converter.CartResponseConverter;
import com.cantire.storetech.evaluation.dto.CartSaveRequest;
import com.cantire.storetech.evaluation.dto.CartSaveResponse;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.model.TaxInfo;
import com.cantire.storetech.evaluation.repo.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final TaxService taxService;

    @Override
    @Transactional
    public CartSaveResponse addProductToCart(CartSaveRequest request) {
        Product product = productService.getProduct(request.getProductId())
                .orElseThrow(() -> new ObjectNotFoundException(request.getProductId(), "Product"));

        List<TaxInfo> taxes = taxService.getTaxesForRegion(request.getRegion(), request.getCurrencyCode());

        Cart cart;
        if (request.getCartId() != null) {
            cart = cartRepository.findById(request.getCartId())
                    .orElseThrow(() -> new ObjectNotFoundException(request.getCartId(), "Cart"));
            cart.setApplicableTaxes(taxes);
        } else {
            cart = Cart.create(request, taxes);
        }

        cart.addProduct(product, request.getQuantity());
        cart = cartRepository.save(cart);

        return CartResponseConverter.toResponse(cart, true, "Product added to cart");
    }
}
