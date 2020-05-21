package com.charter.restfulHw.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MonthlyPoints {
    
    private LocalDate date;

    private long points;
}