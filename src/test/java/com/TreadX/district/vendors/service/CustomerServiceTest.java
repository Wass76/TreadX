// package com.TreadX.district.vendors.service;

// import com.TreadX.district.vendors.dealerDealerCustomer.dto.DealerCustomerRequestDTO;
// import com.TreadX.district.vendors.dealerDealerCustomer.dto.DealerCustomerResponseDTO;
// import com.TreadX.district.vendors.dealerDealerCustomer.entity.DealerCustomer;
// import com.TreadX.district.vendors.dealerDealerCustomer.mapper.DealerCustomerMapper;
// import com.TreadX.district.vendors.dealerDealerCustomer.repository.DealerCustomerRepository;
// import com.TreadX.district.vendors.dealerDealerCustomer.service.DealerCustomerPhoneService;
// import com.TreadX.district.vendors.dealerDealerCustomer.service.DealerCustomerService;
// import com.TreadX.district.vendors.entity.Vendor;
// import com.TreadX.district.vendors.repository.VendorRepository;
// import com.TreadX.user.entity.User;
// import com.TreadX.user.service.VendorContextService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class DealerCustomerServiceTest {

//     @Mock
//     private DealerCustomerRepository dealerDealerCustomerRepository;

//     @Mock
//     private DealerCustomerPhoneService dealerDealerCustomerPhoneService;

//     @Mock
//     private VendorRepository vendorRepository;

//     @Mock
//     private DealerCustomerMapper dealerDealerCustomerMapper;

//     @Mock
//     private VendorContextService vendorContextService;

//     @InjectMocks
//     private DealerCustomerService dealerDealerCustomerService;

//     private DealerCustomerRequestDTO dealerDealerCustomerRequest;
//     private DealerCustomer dealerDealerCustomer;
//     private Vendor vendor;
//     private User currentUser;

//     @BeforeEach
//     void setUp() {
//         // Setup test data
//         dealerDealerCustomerRequest = new DealerCustomerRequestDTO();
//         dealerDealerCustomerRequest.setFirstName("John");
//         dealerDealerCustomerRequest.setLastName("Doe");
//         dealerDealerCustomerRequest.setEmail("john.doe@example.com");
//         dealerDealerCustomerRequest.setStreetNumber("123");
//         dealerDealerCustomerRequest.setStreetName("Main Street");
//         dealerDealerCustomerRequest.setPostalCode("M5V 3A8");
//         dealerDealerCustomerRequest.setPhoneNumbers(java.util.List.of(
//             com.TreadX.district.vendors.dealerDealerCustomer.dto.DealerCustomerPhoneRequestDTO.builder()
//                 .phoneNumber("+1-416-555-0101")
//                 .phoneType(com.TreadX.district.vendors.dealerDealerCustomer.entity.DealerCustomerPhone.PhoneType.CELL)
//                 .build()
//         ));
//         // dealerDealerCustomerRequest.setCity("Toronto");
//         // dealerDealerCustomerRequest.setProvince("Ontario");
//         // dealerDealerCustomerRequest.setCountry("Canada");
//         // dealerDealerCustomerRequest.setPostalCode("M5V 3A8");
//         // dealerDealerCustomerRequest.setCellPhone("+1-416-555-0101");
//         // dealerDealerCustomerRequest.setVendorId(1L);
//         // dealerDealerCustomerRequest.setVendorDealerCustomerId("CUST001");

//         vendor = new Vendor();
//         dealerDealerCustomer = new DealerCustomer();
//         currentUser = new User();
//     }

//     @Test
//     void testCreateDealerCustomer_VendorNotFound() {
//         when(vendorContextService.getCurrentVendorId()).thenReturn(1L);
//         when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.empty());

//         assertThrows(com.TreadX.utils.exception.ResourceNotFoundException.class, () -> {
//             dealerDealerCustomerService.createDealerCustomer(dealerDealerCustomerRequest);
//         });
//     }

//     @Test
//     void testCreateDealerCustomer_DuplicateDealerCustomer() {
//         when(vendorContextService.getCurrentVendorId()).thenReturn(1L);
//         when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.of(vendor));
//         when(dealerDealerCustomerRepository.existsDuplicateDealerCustomer(any(), any(), any(), any(), any())).thenReturn(true);

//         assertThrows(com.TreadX.utils.exception.ConflictException.class, () -> {
//             dealerDealerCustomerService.createDealerCustomer(dealerDealerCustomerRequest);
//         });
//     }

//     @Test
//     void testCreateDealerCustomer_VendorWithoutUniqueId() {
//         when(vendorContextService.getCurrentVendorId()).thenReturn(1L);
//         when(vendorRepository.findById(1L)).thenReturn(java.util.Optional.of(vendor));
//         when(dealerDealerCustomerRepository.existsDuplicateDealerCustomer(any(), any(), any(), any(), any())).thenReturn(false);
//         when(dealerDealerCustomerMapper.toEntity(any())).thenReturn(dealerDealerCustomer);
//         when(dealerDealerCustomerRepository.save(any(DealerCustomer.class))).thenReturn(dealerDealerCustomer);
//         vendor.setVendorUniqueId(null);

//         assertThrows(IllegalStateException.class, () -> {
//             dealerDealerCustomerService.createDealerCustomer(dealerDealerCustomerRequest);
//         });
//     }
// }
