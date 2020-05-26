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
    private static final String FILE_NAME = "transactions.json";

    @Override
    public List<Customer> getStaticCustomers()
    {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<Transaction> transactions = null;
            try {
                transactions = objectMapper.readValue(inputStream, new TypeReference<List<Transaction>>() {});
            } catch (IOException e) {
                logger.warn("Could not parse json file {}, reason: {}", FILE_NAME, e.getMessage());
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
                logger.warn("SKIPPED Transaction for MemberId: {} Reason: {}", transaction.getCustomerId(), e.getMessage());
                continue;
            }
            long customerId = transaction.getCustomerId();

            // if not yet mapped, initialize customer and insert
            if (mappedCustomerPoints.get(customerId) == null) 
                mappedCustomerPoints.put(customerId, initializeCustomer(transaction));
            else { // else addon to existing customer total points, and points for that month
                Customer customer = mappedCustomerPoints.get(customerId);
                Map<String, Long> monthlyPoints = customer.getMonthlyPoints();
                String monthDate = transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString();
                Long transactionPoints = calculatePoints(transaction.getAmount());

                monthlyPoints.putIfAbsent(monthDate, 0L);
                monthlyPoints.put(monthDate, monthlyPoints.get(monthDate) + calculatePoints(transaction.getAmount()));
                customer.setTotalPoints(customer.getTotalPoints() + transactionPoints);
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
        Customer customer = new Customer();
        customer.setFirstName(transaction.getFirstName());
        customer.setLastName(transaction.getLastName());
        customer.setCustomerId(transaction.getCustomerId());
        Map<String, Long> monthlyPoints = new HashMap<String, Long>();
        Long currentPoints = calculatePoints(transaction.getAmount());
        customer.setTotalPoints(currentPoints);
        monthlyPoints.put(transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString(), currentPoints);
        customer.setMonthlyPoints(monthlyPoints);
        return customer;
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
