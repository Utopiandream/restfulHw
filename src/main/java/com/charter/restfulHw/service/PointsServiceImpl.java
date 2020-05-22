package com.charter.restfulHw.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class PointsServiceImpl implements PointsService{

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