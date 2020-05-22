package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.util.List;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;

import org.springframework.stereotype.Service;

@Service
public interface CustomerPointsService 
{
    public List<CustomerPoints> getCustomerPoints(QuarterlyTransactions quarterlyTransactions);

    public Long calculatePoints(BigDecimal amount);
}