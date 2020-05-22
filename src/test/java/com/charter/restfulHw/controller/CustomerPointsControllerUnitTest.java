package com.charter.restfulHw.controller;

import java.util.ArrayList;
import java.util.List;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.service.CustomerPointsService;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerPointsControllerUnitTest {
    
    @Test
    public void testCalculateCustomerPoints(){
        List<CustomerPoints> customerPoints = new ArrayList<CustomerPoints>();
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        CustomerPointsService customerPointService = EasyMock.createMock(CustomerPointsService.class);
        CustomerPointsController controller = new CustomerPointsController(customerPointService);
        EasyMock.expect(customerPointService.getCustomerPoints(quarterlyTransactions)).andReturn(customerPoints).once();
        EasyMock.replay(customerPointService);
        assertThat(controller.getCustomerPoints(quarterlyTransactions)).isSameAs(customerPoints);
        EasyMock.verify(customerPointService);
    }
}