package com.cantire.storetech.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cantire.storetech.evaluation.dto.ProductResponse;
import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.repo.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getProductsWithPrices() {
        return productRepository.findAll().stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProduct(Long productId) {
        return productRepository.findById(productId);
    }

    private ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setQuantity(product.getQuantity());

        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
        }

        List<ProductResponse.PricingInfo> prices = new ArrayList<>();
        if (product.getPriceInfos() != null) {
            prices = product.getPriceInfos().stream()
                    .map(price -> new ProductResponse.PricingInfo(
                            price.getCurrencyCode(),
                            price.getPrice()
                    ))
                    .collect(Collectors.toList());
        }
        response.setPrices(prices);

        return response;
    }
}
