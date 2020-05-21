package com.charter.restfulHw.service;

import java.math.BigDecimal;

import com.charter.restfulHw.model.Transaction;

import org.springframework.stereotype.Service;

@Service
public interface PointsService {
    
    Long calculatePoints(BigDecimal amount);
    
}