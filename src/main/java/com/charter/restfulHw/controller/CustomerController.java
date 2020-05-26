package com.charter.restfulHw.controller;

import java.util.List;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.charter.restfulHw.service.CustomerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    // private static final String ERROR_PATH = "/error";

    private CustomerService customerPointService;

    public CustomerController(CustomerService customerPointService) {
        this.customerPointService = customerPointService;
    }

    // TODO remove after intial loading
    @GetMapping("/")
    public String home() {
        return "Hello World";
    }

    @PostMapping("/perMonth")
    public List<Customer> getCustomers(@RequestBody(required = true) List<Transaction> transactions) {
        return customerPointService.getCustomers(transactions);
    }

    @GetMapping("/perMonth")
    public List<Customer> getStaticCustomers() {
        return customerPointService.getStaticCustomers();
    }

}