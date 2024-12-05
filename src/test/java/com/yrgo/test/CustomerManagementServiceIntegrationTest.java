package com.yrgo.test;

import com.yrgo.domain.Customer;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/other-tiers.xml", "/datasource-test.xml"})
@Transactional
public class CustomerManagementServiceIntegrationTest {
    @Autowired
    private CustomerManagementService customerService;


    @BeforeEach
    public void setUp() {
        ClassPathXmlApplicationContext factory = new
                ClassPathXmlApplicationContext("application.xml");
        customerService = factory.getBean(CustomerManagementService.class);
    }
    @Test
    public void testFindingById() {
        String id = "CS03640";
        Customer testCustomer = new Customer(id, "TestCorp", "Test Customer");
        customerService.newCustomer(testCustomer);

        Customer foundCustomer = null;
        try {
           foundCustomer = customerService.findCustomerById(id);
            assertEquals(testCustomer, foundCustomer, "The customer returned is the wrong one!");
        } catch (CustomerNotFoundException e) {
            fail("No customer was found when one should have been!");
        }
    }

    @Test
    public void testAddingCustomer(){
        customerService.newCustomer(new Customer("DT03640", "TestCorp", "Test Customer 1"));
        customerService.newCustomer(new Customer("EU03640", "TestCorp", "Test Customer 2"));

        int booksInDb = customerService.getAllCustomers().size();
        assertEquals (2, booksInDb , "There should be two customers in the database!");
    }
}
