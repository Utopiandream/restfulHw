package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.util.Map;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.QuarterlyTransactions;

import org.springframework.stereotype.Service;

@Service
public interface CustomerPointsService 
{
    public Map<Long, Customer> getCustomersPoints(QuarterlyTransactions quarterlyTransactions);

    public Long calculatePoints(BigDecimal amount);
}