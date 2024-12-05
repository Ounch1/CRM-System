package com.yrgo.test;

import com.yrgo.domain.Customer;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerManagementServiceIntegrationTest {
    @Test
    public void testFindingById() {
        ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("application.xml");
        CustomerManagementService customerService = factory.getBean(CustomerManagementService.class);
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
        ClassPathXmlApplicationContext factory = new
                ClassPathXmlApplicationContext("application.xml");
        CustomerManagementService customerService = factory.getBean(CustomerManagementService.class);

        customerService.newCustomer(new Customer("DT03640", "TestCorp", "Test Customer 1"));
        customerService.newCustomer(new Customer("EU03640", "TestCorp", "Test Customer 2"));

        int booksInDb = customerService.getAllCustomers().size();
        assertEquals (2, booksInDb , "There should be two books in the database!");
    }

    @Test
    public void testFindingNonExistentBook() throws CustomerNotFoundException{
        ClassPathXmlApplicationContext factory = new
                ClassPathXmlApplicationContext("application.xml");
        CustomerManagementService customerService = factory.getBean(CustomerManagementService.class);
        assertThrows(CustomerNotFoundException.class, () -> {
            Customer customer = customerService.findCustomerById("Huzaahhh wrong ID");
        });
    }
}
