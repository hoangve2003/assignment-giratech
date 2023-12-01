package com.example.productmanager.service.impl;

import com.example.productmanager.model.Customer;
import com.example.productmanager.repository.CustomerRepository;
import com.example.productmanager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private static final String URL_GET_CUSTOMER = "https://650d8c41a8b42265ec2c5ef1.mockapi.io/api/customer/";
    private static final String URL_POST_CUSTOMER = "https://650d8c41a8b42265ec2c5ef1.mockapi.io/api/customer";
    private static final String URL_PUT_CUSTOMER = "https://650d8c41a8b42265ec2c5ef1.mockapi.io/api/customer/{id}";
    private static final String URL_DELETE_CUSTOMER = "https://650d8c41a8b42265ec2c5ef1.mockapi.io/api/customer/{id}";
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;

    /**
     * Retrieve all customers.
     *
     * @return a ResponseEntity containing a List of Customer objects
     */
    public ResponseEntity<List<Customer>> findAllCustomer() {
        List<Customer> customers = restTemplate.exchange(
                URL_GET_CUSTOMER,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Customer>>() {
                }).getBody();
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customers);
    }

    /**
     * Add a new customer.
     *
     * @param customer the Customer object to add
     * @return a ResponseEntity containing the created Customer object
     */
    public ResponseEntity addCustomer(Customer customer) {
        Customer createCustomer = restTemplate.postForObject(URL_POST_CUSTOMER, customer, Customer.class);
        customerRepository.save(createCustomer);
        return new ResponseEntity<>(createCustomer, HttpStatus.CREATED);
    }


    /**
     * Update a customer with the given id.
     *
     * @param id       the id of the customer to update
     * @param customer the updated Customer object
     * @return a ResponseEntity containing the updated Customer object
     */
    public ResponseEntity updateCustomer(Long id, Customer customer) {
        restTemplate.put(URL_PUT_CUSTOMER.replace("{id}", String.valueOf(id)), customer);
        Customer updateCustomer = restTemplate.getForObject(URL_GET_CUSTOMER + id, Customer.class);
        customerRepository.save(updateCustomer);
        return new ResponseEntity<>(updateCustomer, HttpStatus.OK);
    }


    /**
     * Delete a customer with the given id.
     *
     * @param id the id of the customer to delete
     * @return a ResponseEntity with a message indicating the customer has been deleted
     */
    public ResponseEntity deleteCustomer(Long id) {
        restTemplate.delete(URL_DELETE_CUSTOMER.replace(" {id}", String.valueOf(id)));
        customerRepository.deleteById(id);
        return new ResponseEntity<>("Deleted customer", HttpStatus.OK);
    }


}
