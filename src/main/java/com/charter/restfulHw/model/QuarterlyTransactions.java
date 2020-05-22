package com.charter.restfulHw.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QuarterlyTransactions {

    private LocalDate startDate;

    private LocalDate endDate;

    private List<Transaction> transactions;
    
}