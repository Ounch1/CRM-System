package com.yrgo.services.customers;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;

import java.util.List;

public class CustomerManagementServiceTimingProxy implements CustomerManagementService {
    private CustomerManagementService originalCustomerManagementService;

    public void setOriginalCustomerManagementService(CustomerManagementService original) {
        this.originalCustomerManagementService = original;
    }

    @Override
    public void newCustomer(Customer newCustomer) {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            originalCustomerManagementService.newCustomer(newCustomer);
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public void updateCustomer(Customer changedCustomer) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            originalCustomerManagementService.updateCustomer(changedCustomer);
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public void deleteCustomer(Customer oldCustomer) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            originalCustomerManagementService.deleteCustomer(oldCustomer);
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public Customer findCustomerById(String customerId) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            Customer foundCustomer = originalCustomerManagementService.findCustomerById(customerId);
            return foundCustomer;
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public List<Customer> findCustomersByName(String name) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            List<Customer> foundCustomers = originalCustomerManagementService.findCustomersByName(name);
            return foundCustomers;
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            List<Customer> foundCustomers = originalCustomerManagementService.getAllCustomers();
            return foundCustomers;
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            Customer foundCustomer = originalCustomerManagementService.getFullCustomerDetail(customerId);
            return foundCustomer;
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }

    @Override
    public void recordCall(String customerId, Call callDetails) throws CustomerNotFoundException {
        //start the clock
        long timeThen = System.nanoTime();

        //do the work
        try {
            originalCustomerManagementService.recordCall(customerId, callDetails);
        } finally {
            //stop the clock
            long timeNow = System.nanoTime();
            long timeTaken = timeNow - timeThen;
            System.out.println("getBookByIsbn took " + timeTaken /
                    1000000 + " milliseconds" );
        }
    }
}
