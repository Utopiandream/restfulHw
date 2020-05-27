package com.charter.restfulHw.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CustomerServiceImpl implements CustomerService {

    private static final String FILE_NAME = "transactions.json";

    @Override
    public List<Customer> getCustomersFromFile()
    {
        //Parses json file into List and calls getCustomers
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<Transaction> transactions = null;
            try {
                transactions = objectMapper.readValue(inputStream, new TypeReference<List<Transaction>>() {});
            } catch (IOException e) {
                log.warn("Could not parse json file {}; Reason: {}", FILE_NAME, e.getMessage());
                e.printStackTrace();
            }
            return getCustomers(transactions);
    }

    @Override
    public List<Customer> getCustomers(List<Transaction> transactions)
    {
        if (transactions == null) throw new IllegalArgumentException("Transactions cannot be null!");
            
        Map<Long, Customer> mappedCustomerPoints = new HashMap<Long, Customer>();
        for (Transaction transaction : transactions) 
        {
            //If transaction in list missing required value, throw exception with index (or a transcationId if it had one)
            try { verifyTransaction(transaction); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Transaction #" + (transactions.indexOf(transaction) + 1) + e.getMessage());
            }

            long customerId = transaction.getCustomerId();

            // If not yet mapped, initialize customer and insert
            if (mappedCustomerPoints.get(customerId) == null) 
                mappedCustomerPoints.put(customerId, initializeCustomer(transaction));
            // Else add to existing customer total points, and mapped points for that month
            else { 
                Customer customer = mappedCustomerPoints.get(customerId);
                Map<String, Long> monthlyPoints = customer.getMonthlyPoints();
                String monthDate = transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString();
                Long transactionPoints = calculatePoints(transaction.getAmount());

                monthlyPoints.putIfAbsent(monthDate, 0L);
                monthlyPoints.put(monthDate, monthlyPoints.get(monthDate) + transactionPoints);
                customer.setTotalPoints(customer.getTotalPoints() + transactionPoints);
            }
        }
        return sortAllCustomersMonthlyPointsMap(new ArrayList<Customer>(mappedCustomerPoints.values()));
    }

    private List<Customer> sortAllCustomersMonthlyPointsMap(ArrayList<Customer> customers){
        //Loop through all customers and sort each mapped monthly values by date
        for (Customer customer : customers) {
            customer.setMonthlyPoints(customer.getMonthlyPoints()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
        }
        return customers;
    }

    protected void verifyTransaction(Transaction transaction)
            throws IllegalArgumentException {
        if (transaction.getFirstName() == null || transaction.getLastName() == null  
            || transaction.getAmount() == null || transaction.getDate() == null || transaction.getCustomerId() == null) {
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append(" Missing values:");
                if(transaction.getCustomerId() == null) sBuilder.append(" customerId ");
                if(transaction.getDate() == null) sBuilder.append(" date ");
                if(transaction.getFirstName() == null) sBuilder.append(" firstName ");
                if(transaction.getLastName() == null) sBuilder.append(" lastName ");
                if(transaction.getAmount() == null) sBuilder.append(" amount ");                
                throw new IllegalArgumentException(sBuilder.toString());
            }
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
