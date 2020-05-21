package com.charter.restfulHw.controller;

import java.util.List;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.service.CustomerPointService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerPointController {

    private CustomerPointService customerPointService;

    public CustomerPointController(CustomerPointService customerPointService){
        this.customerPointService = customerPointService;
    }

    // TODO remove after intial loading
    @GetMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    @GetMapping("/quarterly")
    public List<CustomerPoints> getCustomerPoints(QuarterlyTransactions quarterlyTransactions)
    {
        return customerPointService.getCustomerPoints(quarterlyTransactions);
    }

}