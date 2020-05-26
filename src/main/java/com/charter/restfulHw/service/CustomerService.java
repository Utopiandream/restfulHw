package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.util.List;
import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;

import org.springframework.stereotype.Service;

@Service
public interface CustomerService 
{
    public List<Customer> getStaticCustomers();

    public List<Customer> getCustomers(List<Transaction> transactions);

    public Long calculatePoints(BigDecimal amount);
}