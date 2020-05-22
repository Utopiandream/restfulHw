package com.charter.restfulHw.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import com.charter.restfulHw.model.Customer;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.service.CustomerPointsService;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class CustomerPointsControllerUnitTest {
    
    @Test
    public void testCalculateCustomerPoints(){
        Map<Long, Customer> customerPoints = new HashMap<Long, Customer>();
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        CustomerPointsService customerPointService = EasyMock.createMock(CustomerPointsService.class);
        CustomerPointsController controller = new CustomerPointsController(customerPointService);
        EasyMock.expect(customerPointService.getCustomersPoints(quarterlyTransactions)).andReturn(customerPoints).once();
        EasyMock.replay(customerPointService);
        assertThat(controller.getCustomersPoints(quarterlyTransactions)).isSameAs(customerPoints);
        EasyMock.verify(customerPointService);
    }
}