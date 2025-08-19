package com.TreadX.district.vendors.service;

import com.TreadX.district.vendors.dto.CustomerRequestDTO;
import com.TreadX.district.vendors.dto.CustomerResponseDTO;
import com.TreadX.district.vendors.entity.Customer;
import com.TreadX.district.vendors.entity.Vendor;
import com.TreadX.district.vendors.mapper.CustomerMapper;
import com.TreadX.district.vendors.repository.CustomerRepository;
import com.TreadX.district.vendors.repository.VendorRepository;
import com.TreadX.user.entity.User;
import com.TreadX.user.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDTO customerRequest;
    private Customer customer;
    private Vendor vendor;
    private User currentUser;

    @BeforeEach
    void setUp() {
        // Setup test data
        customerRequest = new CustomerRequestDTO();
        customerRequest.setFirstName("John");
        customerRequest.setLastName("Doe");
        customerRequest.setEmail("john.doe@example.com");
        customerRequest.setStreetNumber("123");
        customerRequest.setStreetName("Main Street");
        customerRequest.setCity("Toronto");
        customerRequest.setProvince("Ontario");
        customerRequest.setCountry("Canada");
        customerRequest.setPostalCode("M5V 3A8");
        customerRequest.setCellPhone("+1-416-555-0101");
        customerRequest.setVendorId(1L);
        customerRequest.setVendorCustomerId("CUST001");

        vendor = new Vendor();
        customer = new Customer();
        currentUser = new User();
    }

    @Test
    void testCreateCustomer_VendorNotFound() {
        // Given
        when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(com.TreadX.utils.exception.ResourceNotFoundException.class, () -> {
            customerService.createCustomer(customerRequest);
        });
    }

    @Test
    void testCreateCustomer_DuplicateCustomer() {
        // Given
        when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.of(vendor));
        when(customerRepository.existsDuplicateCustomer(any(), any(), any(), any(), any())).thenReturn(true);

        // When & Then
        assertThrows(com.TreadX.utils.exception.ConflictException.class, () -> {
            customerService.createCustomer(customerRequest);
        });
    }

    @Test
    void testCreateCustomer_DuplicateVendorCustomerId() {
        // Given
        when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.of(vendor));
        when(customerRepository.existsDuplicateCustomer(any(), any(), any(), any(), any())).thenReturn(false);
        when(customerRepository.existsByVendorCustomerIdAndVendorId(any(), any())).thenReturn(true);

        // When & Then
        assertThrows(com.TreadX.utils.exception.ConflictException.class, () -> {
            customerService.createCustomer(customerRequest);
        });
    }
}
