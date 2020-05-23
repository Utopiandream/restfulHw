package com.charter.restfulHw.controller;

import java.util.List;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.charter.restfulHw.service.CustomerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CustomerController.API_PATH)
public class CustomerController {

    private CustomerService customerPointService;

    public static final String         API_PATH = "/api/customerPoints";

    public CustomerController(CustomerService customerPointService){
        this.customerPointService = customerPointService;
    }

    // TODO remove after intial loading
    @GetMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    @GetMapping("/perMonth")
    public List<Customer> getCustomers(@RequestBody List<Transaction> transactions)
    {
            return customerPointService.getCustomers(transactions);
    }

}