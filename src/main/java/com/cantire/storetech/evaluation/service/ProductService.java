package com.cantire.storetech.evaluation.service;

import java.util.List;

import com.cantire.storetech.evaluation.dto.ProductResponse;
import com.cantire.storetech.evaluation.model.Product;

public interface ProductService {

    List<Product> getProducts();

    Product create(Product product);

    List<ProductResponse> getProductsWithPrices();
}
