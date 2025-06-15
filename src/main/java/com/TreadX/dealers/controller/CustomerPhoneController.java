package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.CustomerPhoneRequestDTO;
import com.TreadX.dealers.dto.CustomerPhoneResponseDTO;
import com.TreadX.dealers.service.CustomerPhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/v1/customer-phones")
@RequiredArgsConstructor
public class CustomerPhoneController {

    private final CustomerPhoneService customerPhoneService;

//    @GetMapping
    public ResponseEntity<List<CustomerPhoneResponseDTO>> getAllCustomerPhones() {
        List<CustomerPhoneResponseDTO> phones = customerPhoneService.getAllCustomerPhones();
        return new ResponseEntity<>(phones, HttpStatus.OK);
    }

//    @GetMapping("/{id}")
    public ResponseEntity<CustomerPhoneResponseDTO> getCustomerPhoneById(@PathVariable("id") Long id) {
        CustomerPhoneResponseDTO phone = customerPhoneService.getCustomerPhoneById(id);
        return new ResponseEntity<>(phone, HttpStatus.OK);
    }

//    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerPhoneResponseDTO>> getCustomerPhonesByCustomer(@PathVariable("customerId") Long customerId) {
        List<CustomerPhoneResponseDTO> phones = customerPhoneService.getCustomerPhonesByCustomer(customerId);
        return new ResponseEntity<>(phones, HttpStatus.OK);
    }

//    @PostMapping
    public ResponseEntity<CustomerPhoneResponseDTO> createCustomerPhone(@RequestBody CustomerPhoneRequestDTO request) {
        CustomerPhoneResponseDTO createdPhone = customerPhoneService.createCustomerPhone(request);
        return new ResponseEntity<>(createdPhone, HttpStatus.CREATED);
    }

//    @PutMapping("/{id}")
    public ResponseEntity<CustomerPhoneResponseDTO> updateCustomerPhone(
            @PathVariable("id") Long id,
            @RequestBody CustomerPhoneRequestDTO request) {
        CustomerPhoneResponseDTO updatedPhone = customerPhoneService.updateCustomerPhone(id, request);
        return new ResponseEntity<>(updatedPhone, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomerPhone(@PathVariable("id") Long id) {
        customerPhoneService.deleteCustomerPhone(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 