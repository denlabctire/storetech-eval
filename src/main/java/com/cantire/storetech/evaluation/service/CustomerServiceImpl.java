package com.cantire.storetech.evaluation.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cantire.storetech.evaluation.model.Customer;
import com.cantire.storetech.evaluation.repo.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Customer"));
    }

    @Override
    @Transactional
    public Customer updateCustomerAutoPay(Long id, boolean autoPayEnabled, String creditCardId) {
        Customer customer = getCustomer(id);
        customer.setAutoPayEnabled(autoPayEnabled);

        if (autoPayEnabled && creditCardId != null
                && !customer.getAssociatedCreditCardIds().contains(creditCardId)) {
            customer.getAssociatedCreditCardIds().add(creditCardId);
        }

        return customerRepository.save(customer);
    }
}
