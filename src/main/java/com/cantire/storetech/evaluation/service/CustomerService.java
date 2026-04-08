package com.cantire.storetech.evaluation.service;

import com.cantire.storetech.evaluation.model.Customer;

public interface CustomerService {

    Customer getCustomer(Long id);

    Customer updateCustomerAutoPay(Long id, boolean autoPayEnabled, String creditCardId);
}
