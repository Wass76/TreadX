package com.TreadX.dealers.service;

import com.TreadX.address.dto.AddressRequestDTO;
import com.TreadX.address.entity.*;
import com.TreadX.dealers.dto.CustomerRequestDTO;
import com.TreadX.dealers.dto.CustomerResponseDTO;
import com.TreadX.dealers.entity.Customer;
import com.TreadX.dealers.entity.Dealer;
import com.TreadX.dealers.entity.CustomerPhone;
import com.TreadX.dealers.enums.PhoneStatus;
import com.TreadX.dealers.enums.PhoneType;
import com.TreadX.dealers.repository.CustomerRepository;
import com.TreadX.dealers.repository.DealerRepository;
import com.TreadX.dealers.repository.CustomerPhoneRepository;
import com.TreadX.address.service.AddressService;
import com.TreadX.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final DealerRepository dealerRepository;
    private final CustomerPhoneRepository customerPhoneRepository;
    private final AddressService addressService;

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public List<CustomerResponseDTO> getCustomersByDealer(Long dealerId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + dealerId));
        return customerRepository.findByDealer(dealer).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setDealer(dealer);
        customer.setCustomerUniqueId(request.getCustomerUniqueId());

        // Create address if provided
        if (request.getAddress() != null) {
            Address address = addressService.createOrReturnAddress(request.getAddress());
            customer.setAddress(address);
        }

        // Save customer first to get the ID
        customer = customerRepository.save(customer);

        // Create and save customer phones if provided
        if (request.getCustomerPhone() != null) {
            createCustomerPhone(customer, request.getCustomerPhone(), PhoneType.CUSTOMER);
        }
        if (request.getHomePhone() != null) {
            createCustomerPhone(customer, request.getHomePhone(), PhoneType.HOME);
        }
        if (request.getBusinessPhone() != null) {
            createCustomerPhone(customer, request.getBusinessPhone(), PhoneType.BUSINESS);
        }

        return toResponseDTO(customer);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        Dealer dealer = dealerRepository.findById(request.getDealerId())
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + request.getDealerId()));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setDealer(dealer);
        customer.setCustomerUniqueId(request.getCustomerUniqueId());

        // Update address if provided
        if (request.getAddress() != null) {
            Address address = addressService.createOrReturnAddress(request.getAddress());
            customer.setAddress(address);
        }

        // Update or create phone numbers
        updateCustomerPhone(customer, request.getCustomerPhone(), PhoneType.CUSTOMER);
        updateCustomerPhone(customer, request.getHomePhone(), PhoneType.HOME);
        updateCustomerPhone(customer, request.getBusinessPhone(), PhoneType.BUSINESS);

        return toResponseDTO(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Delete associated phone numbers first
        customerPhoneRepository.deleteByDealerCustomer(customer);
        
        customerRepository.delete(customer);
    }

    private void createCustomerPhone(Customer customer, String phoneNumber, PhoneType phoneType) {
        CustomerPhone phone = new CustomerPhone();
        phone.setDealerCustomer(List.of(customer));
        phone.setPhoneNumber(phoneNumber);
        phone.setPhoneType(phoneType);
        phone.setPhoneStatus(PhoneStatus.ACTIVE);
        customerPhoneRepository.save(phone);
    }

    private void updateCustomerPhone(Customer customer, String phoneNumber, PhoneType phoneType) {
        if (phoneNumber != null) {
            CustomerPhone phone = customerPhoneRepository.findByDealerCustomerAndPhoneType(customer, phoneType)
                    .orElse(new CustomerPhone());
            phone.setDealerCustomer(List.of(customer));
            phone.setPhoneNumber(phoneNumber);
            phone.setPhoneType(phoneType);
            phone.setPhoneStatus(PhoneStatus.ACTIVE);
            customerPhoneRepository.save(phone);
        }
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setDealerId(customer.getDealer() != null ? customer.getDealer().getId() : null);
        dto.setCustomerUniqueId(customer.getCustomerUniqueId());
        
        // Generate dealerCustomerUniqueId
        if (customer.getDealer() != null && customer.getCustomerUniqueId() != null) {
            String dealerCustomerUniqueId = String.format("%s-%d-%s",
                    customer.getDealer().getDealerUniqueId(),
                    customer.getId(),
                    customer.getCustomerUniqueId());
            dto.setDealerCustomerUniqueId(dealerCustomerUniqueId);
        }

        // Set address fields if address exists
        if (customer.getAddress() != null) {
            Address address = customer.getAddress();
            dto.setStreetNumber(address.getStreetNumber());
            dto.setStreetName(address.getStreetName());
            dto.setPostalCode(address.getPostalCode());
            dto.setUnitNumber(address.getUnitNumber());
            dto.setSpecialInstructions(address.getSpecialInstructions());
            dto.setCountryName(address.getCountry().getCountry());
            dto.setStateName(address.getProvince().getProvince());
            dto.setCityName(address.getCity().getCity());
        }

        // Get all phone numbers
        List<CustomerPhone> phones = customerPhoneRepository.findAllByDealerCustomer(customer);
        for (CustomerPhone phone : phones) {
            switch (phone.getPhoneType()) {
                case CUSTOMER -> dto.setCustomerPhone(phone.getPhoneNumber());
                case HOME -> dto.setHomePhone(phone.getPhoneNumber());
                case BUSINESS -> dto.setBusinessPhone(phone.getPhoneNumber());
            }
        }

        return dto;
    }
} 