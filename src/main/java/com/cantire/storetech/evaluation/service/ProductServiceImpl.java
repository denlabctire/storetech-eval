package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.Product;
import com.cantire.storetech.evaluation.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
