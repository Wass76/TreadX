package com.TreadX.dealers.controller;

import com.TreadX.dealers.entity.DealerEmployee;
import com.TreadX.dealers.service.DealerEmployeeService;
import com.TreadX.user.request.AuthenticationRequest;
import com.TreadX.dealers.dto.DealerEmployeeAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@RestController
//@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class DealerEmployeeController {

//    @Autowired
    private final DealerEmployeeService dealerEmployeeService;

//    @GetMapping
    public ResponseEntity<List<DealerEmployee>> getAllDealerEmployees() {
        List<DealerEmployee> employees = dealerEmployeeService.getAllDealerEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

//    @GetMapping("/{id}")
    public ResponseEntity<DealerEmployee> getDealerEmployeeById(@PathVariable("id") Long id) {
        Optional<DealerEmployee> employee = dealerEmployeeService.getDealerEmployeeById(id);
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

//    @PostMapping
    public ResponseEntity<DealerEmployee> createDealerEmployee(@RequestBody DealerEmployee dealerEmployee) {
        DealerEmployee createdEmployee = dealerEmployeeService.createDealerEmployee(dealerEmployee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

//    @PutMapping("/{id}")
    public ResponseEntity<DealerEmployee> updateDealerEmployee(@PathVariable("id") Long id, @RequestBody DealerEmployee dealerEmployeeDetails) {
        DealerEmployee updatedEmployee = dealerEmployeeService.updateDealerEmployee(id, dealerEmployeeDetails);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDealerEmployee(@PathVariable("id") Long id) {
        dealerEmployeeService.deleteDealerEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PostMapping("/login")
    public ResponseEntity<DealerEmployeeAuthenticationResponse> login(@RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        DealerEmployeeAuthenticationResponse response = dealerEmployeeService.login(request, httpServletRequest);
        return ResponseEntity.ok(response);
    }
} 