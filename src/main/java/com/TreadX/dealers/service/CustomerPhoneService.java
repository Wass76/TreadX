package com.TreadX.dealers.service;

import com.TreadX.dealers.dto.CustomerPhoneRequestDTO;
import com.TreadX.dealers.dto.CustomerPhoneResponseDTO;
import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.CustomerPhone;
import com.TreadX.dealers.enums.PhoneStatus;
import com.TreadX.dealers.enums.PhoneType;
import com.TreadX.dealers.repository.CustomerPhoneRepository;
import com.TreadX.dealers.repository.CustomerRepository;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerPhoneService {
    private final CustomerPhoneRepository customerPhoneRepository;
    private final CustomerRepository customerRepository;

    public List<CustomerPhoneResponseDTO> getAllCustomerPhones() {
        return customerPhoneRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerPhoneResponseDTO getCustomerPhoneById(Long id) {
        return customerPhoneRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Phone not found with id: " + id));
    }

    public List<CustomerPhoneResponseDTO> getCustomerPhonesByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return customerPhoneRepository.findAllByDealerCustomer(customer).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerPhoneResponseDTO createCustomerPhone(CustomerPhoneRequestDTO request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        CustomerPhone phone = new CustomerPhone();
        phone.setDealerCustomer(List.of(customer));
        phone.setPhoneNumber(request.getPhoneNumber());
        phone.setPhoneType(PhoneType.valueOf(request.getPhoneType()));
        phone.setPhoneStatus(PhoneStatus.valueOf(request.getPhoneStatus()));

        
        return toResponseDTO(customerPhoneRepository.save(phone));
    }

    public CustomerPhoneResponseDTO updateCustomerPhone(Long id, CustomerPhoneRequestDTO request) {
        CustomerPhone phone = customerPhoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Phone not found with id: " + id));
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        phone.setDealerCustomer(List.of(customer));
        phone.setPhoneNumber(request.getPhoneNumber());
        phone.setPhoneType(PhoneType.valueOf(request.getPhoneType()));
        phone.setPhoneStatus(PhoneStatus.valueOf(request.getPhoneStatus()));
        
        return toResponseDTO(customerPhoneRepository.save(phone));
    }

    public void deleteCustomerPhone(Long id) {
        CustomerPhone phone = customerPhoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Phone not found with id: " + id));
        customerPhoneRepository.delete(phone);
    }

    private CustomerPhoneResponseDTO toResponseDTO(CustomerPhone phone) {
        CustomerPhoneResponseDTO dto = new CustomerPhoneResponseDTO();
        dto.setPhoneId(phone.getId());
        dto.setCustomerId(phone.getDealerCustomer().get(0).getId());
        dto.setPhoneNumber(phone.getPhoneNumber());
        dto.setPhoneType(phone.getPhoneType().name());
        dto.setPhoneStatus(phone.getPhoneStatus().name());
        return dto;
    }
} 