package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product create(Product product);
}
