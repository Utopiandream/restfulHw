package com.charter.restfulHw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.model.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerPointsServiceImplTest {
    @Autowired
    private CustomerPointsServiceImpl customerPointsServiceImpl;

    @Before
    public void setup() {
        customerPointsServiceImpl = new CustomerPointsServiceImpl();
    }

    @After
    public void teardown() {
        this.customerPointsServiceImpl = null;
    }

    
    private Transaction createMockTranscation() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100"));
        transaction.setDate(LocalDate.now());
        transaction.setCustomerId(1L);
        transaction.setFirstName("Doug");
        transaction.setLastName("Stroer");
        return transaction;
    }

    // ---------------------------------------------------------------//
    // ---------------------Calculate Points--------------------------//
    // ---------------------------------------------------------------//

    @Test
    public void testCalculatePoints_nullValue() {
        assertThat(customerPointsServiceImpl.calculatePoints(null)).isZero();
    }

    @Test
    public void testCalculatePoints_negativeValue() {
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("-1"))).isZero();
    }

    @Test
    public void testCalculatePoints_AmountLessThan51() {
        // given
        BigDecimal minAmount = BigDecimal.ZERO;
        BigDecimal midAmount = new BigDecimal("25");
        BigDecimal maxAmount = new BigDecimal("50");
        // when
        long minResult = customerPointsServiceImpl.calculatePoints(minAmount);
        long midResult = customerPointsServiceImpl.calculatePoints(midAmount);
        long maxResult = customerPointsServiceImpl.calculatePoints(maxAmount);
        // then
        assertThat(minResult).isZero();
        assertThat(midResult).isZero();
        assertThat(maxResult).isZero();
    }

    @Test
    public void testCalculatePoints_AmountBetween51And100() {
        // given
        BigDecimal minAmount = new BigDecimal("51");
        BigDecimal midAmount = new BigDecimal("75");
        BigDecimal maxAmount = new BigDecimal("100");
        // when
        long minResult = customerPointsServiceImpl.calculatePoints(minAmount);
        long midResult = customerPointsServiceImpl.calculatePoints(midAmount);
        long maxResult = customerPointsServiceImpl.calculatePoints(maxAmount);
        // then
        assertThat(minResult).isEqualTo(minAmount.subtract(new BigDecimal("50")).longValue());
        assertThat(midResult).isEqualTo(midAmount.subtract(new BigDecimal("50")).longValue());
        assertThat(maxResult).isEqualTo(maxAmount.subtract(new BigDecimal("50")).longValue());
    }

    @Test
    public void testCalculatePoints_AmountGreaterThan100() {
        // given
        Long minAmount = 101L;
        Long midAmount = 120L;
        Long highAmount = 2342L;
        // when
        long minResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(minAmount));
        long midResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(midAmount));
        long highResult = customerPointsServiceImpl.calculatePoints(new BigDecimal(highAmount));
        // then
        assertThat(minResult).isEqualTo(50 + ((minAmount - 100) * 2));
        assertThat(midResult).isEqualTo(50 + ((midAmount - 100) * 2));
        assertThat(highResult).isEqualTo(50 + ((highAmount - 100) * 2));
    }

    @Test
    public void testCalculatePoints_truncsDecimal() {
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("51.0001"))).isEqualTo(1L);
        assertThat(customerPointsServiceImpl.calculatePoints(new BigDecimal("51.9999"))).isEqualTo(1L);
    }

    // ---------------------------------------------------------------//
    // ---------------------getCustomerPoints--------------------------//
    // ---------------------------------------------------------------//
    @Test
    public void testGetCustomerPoints_QuarterlyTranscationsNull() {
        assertThat(customerPointsServiceImpl.getCustomersPoints(null)).isNull();
    }

    @Test
    public void testGetCustomerPoints_transactionListNull() {
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(LocalDate.now());
        quarterlyTransactions.setEndDate(LocalDate.now());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions));
    }

    @Test
    public void testGetCustomerPoints_DateRangeGreaterThan3Months() {
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(LocalDate.now());
        quarterlyTransactions.setEndDate(LocalDate.now().plusMonths(3));
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        quarterlyTransactions.getTransactions().add(createMockTranscation());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions));
    }

    @Test
    public void testGetCustomerPoints_transactionsEmpty() {
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(LocalDate.now());
        quarterlyTransactions.setEndDate(LocalDate.now());
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        assertThat(customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions)).isEmpty();
    }

    @Test
    public void testGetCustomerPoints_startDateMonthAfterEnd() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusMonths(1);
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(startDate);
        quarterlyTransactions.setEndDate(endDate);
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        quarterlyTransactions.getTransactions().add(createMockTranscation());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions));
    }

    @Test
    public void testGetCustomerPoints_datesNull() {
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        quarterlyTransactions.getTransactions().add(createMockTranscation());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions));
    }

    @Test
    public void testGetCustomerPoints_transactionMonthNotWithinMonthlyDateRangeIgnored() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(startDate);
        quarterlyTransactions.setEndDate(endDate);
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        Transaction transaction = createMockTranscation();
        // Transaction before date range
        transaction.setDate(LocalDate.now().plusMonths(1));
        quarterlyTransactions.getTransactions().add(transaction);
        // Transaction after date range
        transaction = createMockTranscation();
        transaction.setDate(LocalDate.now().minusMonths(2));
        quarterlyTransactions.getTransactions().add(transaction);
        assertThat(customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions)).isEmpty();
    }

    @Test
    public void testGetCustomerPoints_sumsCustomerPointsPerMonth() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(2);
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        quarterlyTransactions.setStartDate(startDate);
        quarterlyTransactions.setEndDate(endDate);
        quarterlyTransactions.setTransactions(new ArrayList<Transaction>());
        Transaction transaction = createMockTranscation();
        Transaction transaction2 = createMockTranscation();
        transaction2.setCustomerId(2);
        quarterlyTransactions.getTransactions().add(transaction);
        quarterlyTransactions.getTransactions().add(transaction);
        quarterlyTransactions.getTransactions().add(transaction);
        quarterlyTransactions.getTransactions().add(transaction2);

        Map<Long, Customer> results = customerPointsServiceImpl.getCustomersPoints(quarterlyTransactions);
        assertThat(results.size()).isEqualTo(2);
        
        Map<Integer, Long> monthlyPonts1 = results.get(transaction.getCustomerId()).getMonthlyPoints();
        assertThat(monthlyPonts1.get(transaction.getDate().getMonthValue()))
                .isEqualTo(customerPointsServiceImpl.calculatePoints(transaction.getAmount()) * 3);

        Map<Integer, Long> monthlyPonts2 = results.get(transaction2.getCustomerId()).getMonthlyPoints();
        assertThat(monthlyPonts2.get(transaction2.getDate().getMonthValue()))
                .isEqualTo(customerPointsServiceImpl.calculatePoints(transaction2.getAmount()));

    }


    // ---------------------------------------------------------------//
    // ---------------------initializeCustomerPoints------------------//
    // ---------------------------------------------------------------//
    @Test
    public void testInitializeCustomerPoints_setsAllValues(){
        Transaction transaction = createMockTranscation();
        Customer customer = customerPointsServiceImpl.initializeCustomer(transaction);
        assertThat(customer.getFirstName()).isEqualTo(transaction.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(transaction.getLastName());
        assertThat(customer.getCustomerId()).isEqualTo(transaction.getCustomerId());
        assertThat(customer.getMonthlyPoints().get(transaction.getDate().getMonthValue())).isNotNull();
    }
}
