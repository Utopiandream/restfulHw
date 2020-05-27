package com.charter.restfulHw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import com.charter.restfulHw.exception.NoDataFoundException;
import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles(profiles = "unitTest")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceImplTest {
    @Autowired
    private CustomerServiceImpl customerPointsServiceImpl;

    @Before
    public void setup() {
        customerPointsServiceImpl = new CustomerServiceImpl();
    }

    @After
    public void teardown() {
        this.customerPointsServiceImpl = null;
    }

    
    private Transaction createMockTranscation() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100"));
        transaction.setDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
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
    public void testGetCustomerPoints_transactionListNull() {
        Assertions.assertThrows(NoDataFoundException.class,
                () -> customerPointsServiceImpl.getCustomers(null));
    }

    @Test
    public void testGetCustomerPoints_transactionsEmpty() {
        assertThat(customerPointsServiceImpl.getCustomers(new ArrayList<Transaction>())).isEmpty();
    }

    @Test
    public void testGetCustomerPoints_transactionsWithNullValueIgnored() {
        //given
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(createMockTranscation().setFirstName(null));
        transactions.add(createMockTranscation().setLastName(null));
        transactions.add(createMockTranscation().setAmount(null));
        transactions.add(createMockTranscation().setDate(null));
        transactions.add(createMockTranscation().setCustomerId(null));
        //when
        List<Customer> results = customerPointsServiceImpl.getCustomers(transactions);
        //then
        assertThat(results).isEmpty();
    }

    @Test
    public void testGetCustomerPoints_GroupsCustomerById() {
        LocalDate dateMonth = createMockTranscation().getDate().with(TemporalAdjusters.firstDayOfMonth());
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction = createMockTranscation();
        Transaction transaction2 = createMockTranscation();
        transaction2.setCustomerId(2L);
        transactions.add(createMockTranscation());
        transactions.add(createMockTranscation());
        transactions.add(createMockTranscation());
        transactions.add(transaction2);

        List<Customer> results = customerPointsServiceImpl.getCustomers(transactions);
        assertThat(results.size()).isEqualTo(2);
        
        for (Customer customer : results) {
            if(customer.getCustomerId() == transaction.getCustomerId())
            {
                assertThat(customer.getMonthlyPoints().get(dateMonth.toString()))
                .isEqualTo(customerPointsServiceImpl.calculatePoints(transaction.getAmount()) * 3);
            }
            else {
                assertThat(customer.getMonthlyPoints().get(dateMonth.toString()))
                .isEqualTo(customerPointsServiceImpl.calculatePoints(transaction2.getAmount()));    
            }
        }
    }

    @Test
    public void testGetCustomerPoints_MapsPointsPerMonth() {
        //given
        LocalDate dateMonth = createMockTranscation().getDate().with(TemporalAdjusters.firstDayOfMonth());
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction1 = createMockTranscation().setDate(dateMonth);
        Transaction transaction2 = createMockTranscation().setDate(dateMonth.plusMonths(1));
        Transaction transaction3 = createMockTranscation().setDate(dateMonth.minusMonths(1));
        
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        //when
        List<Customer> results = customerPointsServiceImpl.getCustomers(transactions);
        //then
        assertThat(results.size()).isEqualTo(1);
        Customer customerResult = results.get(0);
        assertThat(customerResult.getMonthlyPoints().get(dateMonth.toString())).isNotNull();
        assertThat(customerResult.getMonthlyPoints().get(dateMonth.plusMonths(1).toString())).isNotNull();
        assertThat(customerResult.getMonthlyPoints().get(dateMonth.minusMonths(1).toString())).isNotNull();
    }

    @Test
    public void testGetCustomerPoints_TotalsMonth() {
        //given
        LocalDate dateMonth = createMockTranscation().getDate().with(TemporalAdjusters.firstDayOfMonth());
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction1 = createMockTranscation().setDate(dateMonth);
        Transaction transaction2 = createMockTranscation().setDate(dateMonth.plusMonths(1));
        Transaction transaction3 = createMockTranscation().setDate(dateMonth.minusMonths(1));
        
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        //when
        List<Customer> results = customerPointsServiceImpl.getCustomers(transactions);
        //then
        assertThat(results.size()).isEqualTo(1);
        Customer customerResult = results.get(0);
        assertThat(customerResult.getTotalPoints()).isEqualTo(50 * 3);
    }


    // ---------------------------------------------------------------//
    // ---------------------initializeCustomerPoints------------------//
    // ---------------------------------------------------------------//
    @Test
    public void testInitializeCustomerPoints_setsAllValues(){
        //given
        Transaction transaction = createMockTranscation();
        //when
        Customer customer = customerPointsServiceImpl.initializeCustomer(transaction);
        //then
        assertThat(customer.getFirstName()).isEqualTo(transaction.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(transaction.getLastName());
        assertThat(customer.getCustomerId()).isEqualTo(transaction.getCustomerId());
        assertThat(customer.getMonthlyPoints().get(transaction.getDate().with(TemporalAdjusters.firstDayOfMonth()).toString())).isNotNull();
        assertThat(customer.getTotalPoints()).isEqualTo(50);
    }

    
    @Test
    public void testGetStaticCustomers() {
        assertThat(customerPointsServiceImpl.getCustomersFromFile()).isNotNull();
    }
}
