package com.example.productmanager.service;

import com.example.productmanager.model.Customer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {
    ResponseEntity<List<Customer>> findAllCustomer();

    ResponseEntity<Customer> addCustomer(Customer customer);

    ResponseEntity<Customer> updateCustomer(Long id, Customer customer);

    ResponseEntity<String> deleteCustomer(Long id);
}
