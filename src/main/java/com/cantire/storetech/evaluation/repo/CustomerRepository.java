package com.cantire.storetech.evaluation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cantire.storetech.evaluation.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
