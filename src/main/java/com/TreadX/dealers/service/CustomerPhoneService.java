package com.TreadX.dealers.service;


import com.TreadX.dealers.entity.CustomerPhone;
import com.TreadX.dealers.repository.CustomerPhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerPhoneService {

    private final CustomerPhoneRepository customerPhoneRepository;

    @Autowired
    public CustomerPhoneService(CustomerPhoneRepository customerPhoneRepository) {
        this.customerPhoneRepository = customerPhoneRepository;
    }

    public CustomerPhone createCustomerPhone(CustomerPhone customerPhone) {
        return customerPhoneRepository.save(customerPhone);
    }

    public List<CustomerPhone> getAllCustomerPhones() {
        return customerPhoneRepository.findAll();
    }

    public Optional<CustomerPhone> getCustomerPhoneById(Long id) {
        return customerPhoneRepository.findById(id);
    }

    public CustomerPhone updateCustomerPhone(Long id, CustomerPhone customerPhoneDetails) {
        CustomerPhone customerPhone = customerPhoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Phone not found with id: " + id));
        
        customerPhone.setPhoneNumber(customerPhoneDetails.getPhoneNumber());
        customerPhone.setPhoneType(customerPhoneDetails.getPhoneType());
        customerPhone.setDealerCustomer(customerPhoneDetails.getDealerCustomer());
        
        return customerPhoneRepository.save(customerPhone);
    }

    public void deleteCustomerPhone(Long id) {
        CustomerPhone customerPhone = customerPhoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Phone not found with id: " + id));
        customerPhoneRepository.delete(customerPhone);
    }
} 