package com.charter.restfulHw.model;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerPoints {

    private long memberId;

    private String firstName;

    private String lastName;

    private Map<Long, Long> monthlyPoints;
    
}