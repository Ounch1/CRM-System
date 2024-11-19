package com.yrgo.services.customers;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;

public class CustomerManagementMockImpl implements CustomerManagementService {
	private HashMap<String,Customer> customerMap;

	public CustomerManagementMockImpl() {
		customerMap = new HashMap<String,Customer>();
		customerMap.put("OB74", new Customer("OB74" ,"Fargo Ltd", "some notes"));
		customerMap.put("NV10", new Customer("NV10" ,"North Ltd", "some other notes"));
		customerMap.put("RM210", new Customer("RM210" ,"River Ltd", "some more notes"));
	}


	@Override
	public void newCustomer(Customer newCustomer) {
		if (!customerMap.containsKey(newCustomer.getCustomerId()))
		{
			customerMap.put(newCustomer.getCustomerId(), newCustomer);
		}
	}

	@Override
	public void updateCustomer(Customer changedCustomer) {
		if (customerMap.containsKey(changedCustomer.getCustomerId()))
		{
			customerMap.replace(changedCustomer.getCustomerId(), changedCustomer);
		}
	}

	@Override
	public void deleteCustomer(Customer oldCustomer) {
		if (customerMap.containsKey(oldCustomer.getCustomerId()))
		{
			customerMap.remove(oldCustomer.getCustomerId());
		}
	}

	@Override
	public Customer findCustomerById(String customerId) throws CustomerNotFoundException {

		if (customerMap.containsKey(customerId))
		{
			return customerMap.get(customerId);
		}
		else
		{
			throw new CustomerNotFoundException();
		}
	}

	@Override
	public List<Customer> findCustomersByName(String name) {
		return customerMap.values().stream()
				.filter(customer -> customer.getCompanyName().contains(name))
				.collect(Collectors.toList());
	}

	@Override
	public List<Customer> getAllCustomers() {
		return customerMap.values().stream().toList();
	}

	@Override
	public Customer getFullCustomerDetail(String customerId) throws CustomerNotFoundException {
		if (customerMap.containsKey(customerId))
		{
			return customerMap.get(customerId);
		}
		else
		{
			throw new CustomerNotFoundException();
		}
	}

	@Override
	public void recordCall(String customerId, Call callDetails) throws CustomerNotFoundException {
		//First find the customer
		Customer customer = findCustomerById(customerId);

		//Call the addCall on the customer
		customer.addCall(callDetails);
	}

}
