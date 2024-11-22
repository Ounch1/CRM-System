package com.yrgo.services.customers;

import com.yrgo.dataaccess.CustomerDao;
import com.yrgo.dataaccess.RecordNotFoundException;
import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CustomerManagementServiceProductionImpl implements CustomerManagementService{
    CustomerDao customerDAO;

    public CustomerManagementServiceProductionImpl(CustomerDao customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public void newCustomer(Customer newCustomer) {
        customerDAO.create(newCustomer);
    }

    @Override
    public void updateCustomer(Customer changedCustomer) throws CustomerNotFoundException {
        try {
            customerDAO.update(changedCustomer);
        } catch (RecordNotFoundException ex) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void deleteCustomer(Customer oldCustomer) throws CustomerNotFoundException {
        try {
            customerDAO.delete(oldCustomer);
        } catch (RecordNotFoundException ex) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public Customer findCustomerById(String customerId) throws CustomerNotFoundException {
        try {
            return customerDAO.getById(customerId);
        }
        catch (RecordNotFoundException ex)
        {
            throw new CustomerNotFoundException();
        }

    }

    @Override
    public List<Customer> findCustomersByName(String name) throws CustomerNotFoundException {
        try {
            return customerDAO.getByName(name);
        } catch (RecordNotFoundException ex) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws CustomerNotFoundException {
        try {
            return customerDAO.getFullCustomerDetail(customerId);
        } catch (RecordNotFoundException ex) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void recordCall(String customerId, Call callDetails) throws CustomerNotFoundException {
        try {
            customerDAO.addCall(callDetails, customerId);
        } catch (RecordNotFoundException ex) {
            throw new CustomerNotFoundException();
        }
    }
}
