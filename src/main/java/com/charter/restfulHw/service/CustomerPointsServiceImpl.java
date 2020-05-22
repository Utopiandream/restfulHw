package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.model.Transaction;

import org.springframework.stereotype.Service;

@Service
public class CustomerPointsServiceImpl implements CustomerPointsService {

    @Override
    public Map<Long, CustomerPoints> getCustomerPoints(QuarterlyTransactions quarterlyTransactions) {
        
        if(quarterlyTransactions == null || quarterlyTransactions.getStartDate() == null || quarterlyTransactions.getEndDate() == null || quarterlyTransactions.getTransactions() == null)
            throw new IllegalArgumentException("StartDate, EndDate, and Transaction List cannot be null!");
        LocalDate startDate = quarterlyTransactions.getStartDate().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = quarterlyTransactions.getEndDate().with(TemporalAdjusters.lastDayOfMonth());
        if(startDate.isAfter(endDate)) throw new IllegalArgumentException("StartDate cannot be after EndDate!");
        
        for (Transaction transaction : quarterlyTransactions.getTransactions()) {
            LocalDate transactionDate = transaction.getDate();
            if(transactionDate.isBefore(startDate) || transactionDate.isAfter(endDate)) continue;
            
        }

        Map<Long, CustomerPoints> customerPoints = new HashMap<Long, CustomerPoints>();
        
        return customerPoints;
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