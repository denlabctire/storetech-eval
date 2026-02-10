package com.cantire.storetech.evaluation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cantire.storetech.evaluation.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
