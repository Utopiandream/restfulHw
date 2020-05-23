package com.charter.restfulHw.model;

import java.time.LocalDate;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Customer {

    private long customerId;

    private String firstName;

    private String lastName;

    private Map<LocalDate, Long> monthlyPoints;
    
}