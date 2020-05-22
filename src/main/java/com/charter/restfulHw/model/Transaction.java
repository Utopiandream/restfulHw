package com.charter.restfulHw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Transaction {
    
    private long transactionId;

    private LocalDate date;

    private long customerId;

    private String firstName;

    private String lastName;

    private BigDecimal Amount;
}