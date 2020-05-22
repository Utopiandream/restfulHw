package com.charter.restfulHw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerPointsServiceImplTest
{
    @Autowired
    private CustomerPointsServiceImpl customerPointsServiceImpl;

    @Before
    public void setup()
    {
        customerPointsServiceImpl = new CustomerPointsServiceImpl();
    }
    
    @After
    public void teardown()
    {
        this.customerPointsServiceImpl = null;
    }

    @Test 
    public void testCalculatePoints_nullValue(){
        assertThat(customerPointsServiceImpl.calculatePoints(null)).isZero();
    }

    @Test 
    public void testCalculatePoints_negativeValue(){
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("-1"))).isZero();
    }

    @Test 
    public void testCalculatePoints_AmountLessThan51(){
        //given
        BigDecimal minAmount = BigDecimal.ZERO;
        BigDecimal midAmount = new BigDecimal("25");
        BigDecimal maxAmount = new BigDecimal("50");
        //when
        long minResult = customerPointsServiceImpl.calculatePoints(minAmount);
        long midResult = customerPointsServiceImpl.calculatePoints(midAmount);
        long maxResult = customerPointsServiceImpl.calculatePoints(maxAmount);
        //then
        assertThat(minResult).isZero();
        assertThat(midResult).isZero();
        assertThat(maxResult).isZero();
    }

    @Test 
    public void testCalculatePoints_AmountBetween51And100(){
        //given
        BigDecimal minAmount = new BigDecimal("51");
        BigDecimal midAmount = new BigDecimal("75");
        BigDecimal maxAmount = new BigDecimal("100");
        //when
        long minResult = customerPointsServiceImpl.calculatePoints(minAmount);
        long midResult = customerPointsServiceImpl.calculatePoints(midAmount);
        long maxResult = customerPointsServiceImpl.calculatePoints(maxAmount);
        //then
        assertThat(minResult).isEqualTo(minAmount.subtract(new BigDecimal("50")).longValue());
        assertThat(midResult).isEqualTo(midAmount.subtract(new BigDecimal("50")).longValue());
        assertThat(maxResult).isEqualTo(maxAmount.subtract(new BigDecimal("50")).longValue());
    }

    @Test 
    public void testCalculatePoints_AmountGreaterThan100(){
        //given
        Long minAmount = 101L;
        Long midAmount = 120L;
        Long highAmount = 2342L;
        //when
        long minResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(minAmount));
        long midResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(midAmount));
        long highResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(highAmount));
        //then
        assertThat(minResult).isEqualTo(50 + ((minAmount - 100) * 2));
        assertThat(midResult).isEqualTo(50 + ((midAmount - 100) * 2));
        assertThat(highResult).isEqualTo(50 + ((highAmount - 100) * 2));
    }

    @Test 
    public void testCalculatePoints_truncsDecimal(){
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("51.0001"))).isEqualTo(1L);
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("51.9999"))).isEqualTo(1L);
    }

}

