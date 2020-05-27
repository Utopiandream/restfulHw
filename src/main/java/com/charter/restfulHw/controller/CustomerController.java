package com.charter.restfulHw.controller;

import java.util.List;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.charter.restfulHw.service.CustomerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CustomerController.API_PATH)
public class CustomerController {

    public static final String API_PATH = "/customer";
    
    private CustomerService customerPointService;

    public CustomerController(CustomerService customerPointService) {
        this.customerPointService = customerPointService;
    }

    @PostMapping("/perMonth")
    public List<Customer> getCustomers(@RequestBody(required = true) List<Transaction> transactions) {
        return customerPointService.getCustomers(transactions);
    }

    @GetMapping("/perMonth")
    public List<Customer> getCustomersFromFile() {
        return customerPointService.getCustomersFromFile();
    }

}