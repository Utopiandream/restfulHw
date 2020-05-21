package com.charter.restfulHw.controller;

import java.util.ArrayList;
import java.util.List;

import com.charter.restfulHw.model.CustomerPoints;
import com.charter.restfulHw.model.QuarterlyTransactions;
import com.charter.restfulHw.service.CustomerPointService;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerPointControllerUnitTest {
    
    @Test
    public void testCalculateCustomerPoints(){
        List<CustomerPoints> customerPoints = new ArrayList<CustomerPoints>();
        QuarterlyTransactions quarterlyTransactions = new QuarterlyTransactions();
        CustomerPointService customerPointService = EasyMock.createMock(CustomerPointService.class);
        CustomerPointController controller = new CustomerPointController(customerPointService);
        EasyMock.expect(customerPointService.getCustomerPoints(quarterlyTransactions)).andReturn(customerPoints).once();
        EasyMock.replay(customerPointService);
        assertThat(controller.getCustomerPoints(quarterlyTransactions)).isSameAs(customerPoints);
        EasyMock.verify(customerPointService);
    }
}