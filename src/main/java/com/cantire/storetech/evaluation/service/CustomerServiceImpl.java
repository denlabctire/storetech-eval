package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Customer getCustomer(Long id) {
        throw new RuntimeException("NYI");
    }

    @Override
    @Transactional
    public Customer updateCustomerAutoPay(Long id, boolean autoPayEnabled, String creditCardId) {
        throw new RuntimeException("NYI");
    }
}
