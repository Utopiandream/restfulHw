package com.charter.restfulHw.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.model.Transaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CustomerPointsServiceImpl implements CustomerPointsService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Map<Long, Customer> getCustomersPoints(QuarterlyTransactions quarterlyTransactions)
    {
        if (quarterlyTransactions == null) return null;
        //Verify Dates
        if (quarterlyTransactions.getStartDate() == null || quarterlyTransactions.getEndDate() == null || quarterlyTransactions.getTransactions() == null) 
            throw new IllegalArgumentException("StartDate, EndDate, and Transaction list cannot be null!");
            
        LocalDate startDate = quarterlyTransactions.getStartDate().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = quarterlyTransactions.getEndDate().with(TemporalAdjusters.lastDayOfMonth());
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("StartDate cannot be after EndDate!");
        if (ChronoUnit.MONTHS.between(startDate, endDate) > 2 )  throw new IllegalArgumentException("Date Range cannot exceed 3 months!");
            
        Map<Long, Customer> mappedCustomerPoints = new HashMap<Long, Customer>();
        for (Transaction transaction : quarterlyTransactions.getTransactions()) 
        {
            try { verifyTransaction(transaction, startDate, endDate); }
            catch (IllegalArgumentException e) {
                logger.warn("SKIPPED Transaction Id: {}, MemberId: {} Reason: {}", transaction.getTransactionId(), transaction.getCustomerId(), e.getMessage());
                continue;
            }

            long customerId = transaction.getCustomerId();
            // if not yet mapped, initialize customer and insert
            if (mappedCustomerPoints.get(customerId) == null) 
                mappedCustomerPoints.put(customerId, initializeCustomer(transaction));
            else { // else addon to existing customer points for that month
                Map<Integer, Long> monthlyPoints = mappedCustomerPoints.get(customerId).getMonthlyPoints();
                int month = transaction.getDate().getMonthValue();
                monthlyPoints.put(month, monthlyPoints.get(month) + calculatePoints(transaction.getAmount()));
            }
        }
        return mappedCustomerPoints;
    }

    private void verifyTransaction(Transaction transaction, LocalDate startDate, LocalDate endDate)
            throws IllegalArgumentException {
        if (transaction.getFirstName() == null || transaction.getLastName() == null  
            || transaction.getAmount() == null || transaction.getDate() == null) 
            throw new IllegalArgumentException("Transaction cannot have null values!");
        if (transaction.getDate().isBefore(startDate) || transaction.getDate().isAfter(endDate))
            throw new IllegalArgumentException("Transaction Date outside date range!");
    }

    protected Customer initializeCustomer(Transaction transaction) {
        Customer customerPoints = new Customer();
        customerPoints.setFirstName(transaction.getFirstName());
        customerPoints.setLastName(transaction.getLastName());
        customerPoints.setCustomerId(transaction.getCustomerId());
        Map<Integer, Long> monthlyPoints = new HashMap<Integer, Long>();
        monthlyPoints.put(transaction.getDate().getMonthValue(), calculatePoints(transaction.getAmount()));
        customerPoints.setMonthlyPoints(monthlyPoints);
        return customerPoints;
    }

    @Override
    public Long calculatePoints(BigDecimal amount) {
        if (amount == null)
            return 0L;

        long subTotal = amount.longValue();
        if (subTotal <= 50)
            return 0L;
        subTotal -= 50;
        if (subTotal <= 50)
            return subTotal;
        subTotal -= 50;
        return 50 + (subTotal * 2);
    }

}
