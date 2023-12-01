package com.example.productmanager.controller;

import com.example.productmanager.model.Customer;
import com.example.productmanager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService service;

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return service.findAllCustomer();
    }

    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        return service.addCustomer(customer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) {
        return service.updateCustomer(id, customer);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> addCustomer(@PathVariable("id") Long id) {
        return service.deleteCustomer(id);
    }
}
