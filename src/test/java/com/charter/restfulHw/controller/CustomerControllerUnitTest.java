package com.charter.restfulHw.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.Transaction;
import com.charter.restfulHw.service.CustomerService;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class CustomerControllerUnitTest {
    
    @Test
    public void testCalculateCustomerPoints(){
        List<Customer> customerPoints = new ArrayList<Customer>();
        List<Transaction> transactions = new ArrayList<Transaction>();
        CustomerService customerPointService = EasyMock.createMock(CustomerService.class);
        CustomerController controller = new CustomerController(customerPointService);
        EasyMock.expect(customerPointService.getCustomers(transactions)).andReturn(customerPoints).once();
        EasyMock.replay(customerPointService);
        assertThat(controller.getCustomers(transactions)).isSameAs(customerPoints);
        EasyMock.verify(customerPointService);
    }
}