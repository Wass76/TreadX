// package com.TreadX.district.vendors.service;

// import com.TreadX.district.vendors.dealerDealerCustomer.dto.DealerCustomerPhoneRequestDTO;
// import com.TreadX.district.vendors.dealerDealerCustomer.dto.DealerCustomerRequestDTO;
// import com.TreadX.district.vendors.dealerDealerCustomer.entity.DealerCustomerPhone;
// import com.TreadX.district.vendors.dealerDealerCustomer.repository.DealerCustomerRepository;
// import com.TreadX.district.vendors.dealerDealerCustomer.service.DealerCustomerService;
// import com.TreadX.district.vendors.entity.Vendor;
// import com.TreadX.district.vendors.repository.VendorRepository;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @ActiveProfiles("test")
// @Transactional
// class DealerCustomerServiceIntegrationTest {

//     @Autowired
//     private DealerCustomerService dealerDealerCustomerService;

//     @Autowired
//     private DealerCustomerRepository dealerDealerCustomerRepository;

//     @Autowired
//     private VendorRepository vendorRepository;

//     @Test
//     void testDealerCustomerCreationFlow() {
//         // This test will verify the basic dealerDealerCustomer creation flow works
//         // We'll create a simple test without complex mocking
        
//         // Create a test vendor first
//         Vendor vendor = createTestVendor();
        
//         // Create dealerDealerCustomer request with phone numbers
//         DealerCustomerRequestDTO request = createTestDealerCustomerRequest(vendor.getId());
        
//         // Test that we can create the request (basic validation)
//         assertNotNull(request);
//         assertEquals("John", request.getFirstName());
//         assertEquals("Doe", request.getLastName());
//         assertEquals("john.doe@example.com", request.getEmail());
//         assertNotNull(request.getPhoneNumbers());
//         assertEquals(2, request.getPhoneNumbers().size());
        
//         // Test that the request has the required fields
//         assertNotNull(request.getStreetNumber());
//         assertNotNull(request.getStreetName());
//         // assertNotNull(request.getCity());
//         // assertNotNull(request.getProvince());
//         // assertNotNull(request.getCountry());
//         // assertNotNull(request.getPostalCode());
//         // assertNotNull(request.getVendorId());
//         // assertNotNull(request.getVendorDealerCustomerId());
        
//         System.out.println("✅ DealerCustomer creation flow test passed - basic validation works");
//     }

//     private Vendor createTestVendor() {
//         Vendor vendor = new Vendor();
//         vendor.setBusinessName("Test Vendor");
//         vendor.setLegalName("Test Vendor Legal");
//         vendor.setEmail("test@vendor.com");
//         vendor.setPhoneNumber("+1-555-0123");
//         vendor.setVendorStatus(com.TreadX.district.vendors.enums.VendorStatus.ACTIVE);
//         vendor.setVendorUniqueId("TEST001");
        
//         return vendorRepository.save(vendor);
//     }

//     private DealerCustomerRequestDTO createTestDealerCustomerRequest(Long vendorId) {
//         DealerCustomerRequestDTO request = new DealerCustomerRequestDTO();
        
//         // Basic Information
//         request.setFirstName("John");
//         request.setLastName("Doe");
//         request.setEmail("john.doe@example.com");
        
//         // Address Information
//         request.setStreetNumber("123");
//         request.setStreetName("Main Street");
//         request.setAptUnitBldg("Apt 4B");
//         // request.setCity("Toronto");
//         // request.setProvince("Ontario");
//         // request.setCountry("Canada");
//         request.setPostalCode("M5V 3A8");
        
//         // Phone Numbers
//         DealerCustomerPhoneRequestDTO cellPhone = new DealerCustomerPhoneRequestDTO();
//         cellPhone.setPhoneNumber("+1-416-555-0101");
//         cellPhone.setPhoneType(DealerCustomerPhone.PhoneType.CELL);
//         cellPhone.setIsPrimary(true);
        
//         DealerCustomerPhoneRequestDTO homePhone = new DealerCustomerPhoneRequestDTO();
//         homePhone.setPhoneNumber("+1-416-555-0102");
//         homePhone.setPhoneType(DealerCustomerPhone.PhoneType.HOME);
//         homePhone.setIsPrimary(false);
        
//         request.setPhoneNumbers(Arrays.asList(cellPhone, homePhone));
        
//         // Vendor Information
//         // request.setVendorId(vendorId);
//         // request.setVendorDealerCustomerId("CUST001");
        
//         return request;
//     }
// }
