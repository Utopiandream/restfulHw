package com.charter.restfulHw.controller;

import java.util.Map;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.service.CustomerPointsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerPointsController {

    private CustomerPointsService customerPointService;

    public CustomerPointsController(CustomerPointsService customerPointService){
        this.customerPointService = customerPointService;
    }

    // TODO remove after intial loading
    @GetMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    @GetMapping("/quarterly")
    public Map<Long, Customer> getCustomersPoints(@RequestBody QuarterlyTransactions quarterlyTransactions)
    {
        return customerPointService.getCustomersPoints(quarterlyTransactions);
    }

}