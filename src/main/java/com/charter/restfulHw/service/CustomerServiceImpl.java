package com.charter.restfulHw.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charter.restfulHw.exception.NoDataFoundException;
import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<Customer> getStaticCustomers()
    {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("transactions.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<Transaction> transactions = null;
            try {
                transactions = objectMapper.readValue(inputStream, new TypeReference<List<Transaction>>() {
                });
            } catch (IOException e) {
                logger.warn("Could not parse transaction.json file, {}", e.getMessage());
                e.printStackTrace();
            }
            return getCustomers(transactions);
    }

    @Override
    public List<Customer> getCustomers(List<Transaction> transactions)
    {
        if (transactions == null) throw new NoDataFoundException();
            
        Map<Long, Customer> mappedCustomerPoints = new HashMap<Long, Customer>();
        for (Transaction transaction : transactions) 
        {
            try { verifyTransaction(transaction); }
            catch (IllegalArgumentException e) {
                logger.warn("SKIPPED Transaction Id: {}, MemberId: {} Reason: {}", transaction.getTransactionId(), transaction.getCustomerId(), e.getMessage());
                continue;
            }
            long customerId = transaction.getCustomerId();

            // if not yet mapped, initialize customer and insert
            if (mappedCustomerPoints.get(customerId) == null) 
                mappedCustomerPoints.put(customerId, initializeCustomer(transaction));
            else { // else addon to existing customer points for that month
                Map<String, Long> monthlyPoints = mappedCustomerPoints.get(customerId).getMonthlyPoints();
                String monthDate = transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString();
                monthlyPoints.putIfAbsent(monthDate, 0L);
                monthlyPoints.put(monthDate, monthlyPoints.get(monthDate) + calculatePoints(transaction.getAmount()));
            }
        }
        return new ArrayList<Customer>(mappedCustomerPoints.values());
    }

    private void verifyTransaction(Transaction transaction)
            throws IllegalArgumentException {
        if (transaction.getFirstName() == null || transaction.getLastName() == null  
            || transaction.getAmount() == null || transaction.getDate() == null) 
            throw new IllegalArgumentException("Transaction cannot have null values!");
        
    }

    protected Customer initializeCustomer(Transaction transaction) {
        Customer customerPoints = new Customer();
        customerPoints.setFirstName(transaction.getFirstName());
        customerPoints.setLastName(transaction.getLastName());
        customerPoints.setCustomerId(transaction.getCustomerId());
        Map<String, Long> monthlyPoints = new HashMap<String, Long>();
        monthlyPoints.put(transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString(), calculatePoints(transaction.getAmount()));
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
