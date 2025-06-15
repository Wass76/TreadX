package com.TreadX.dealers.controller;

import com.TreadX.dealers.dto.CustomerRequestDTO;
import com.TreadX.dealers.dto.CustomerResponseDTO;
import com.TreadX.dealers.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerController {

    private final CustomerService customerService;

//    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

//    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable("id") Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

//    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByDealer(@PathVariable("dealerId") Long dealerId) {
        List<CustomerResponseDTO> customers = customerService.getCustomersByDealer(dealerId);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

//    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(request);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

//    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable("id") Long id,
            @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, request);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 