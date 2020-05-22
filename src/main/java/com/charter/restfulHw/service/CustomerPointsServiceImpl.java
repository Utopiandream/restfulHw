package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.util.List;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;

import org.springframework.stereotype.Service;

@Service
public class CustomerPointsServiceImpl implements CustomerPointsService {

    @Override
    public List<CustomerPoints> getCustomerPoints(QuarterlyTransactions quarterlyTransactions) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public Long calculatePoints(BigDecimal amount) {
        if(amount == null) return 0L;

        long subTotal = amount.longValue();
        if(subTotal <= 50) return 0L;
        subTotal -= 50;
        if(subTotal <= 50) return subTotal;
        subTotal -= 50;
        return 50 + (subTotal * 2);
    }
    
    
}