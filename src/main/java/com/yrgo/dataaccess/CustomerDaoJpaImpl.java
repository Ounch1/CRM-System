package com.yrgo.dataaccess;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomerDaoJpaImpl implements CustomerDao{

    @PersistenceContext
    private EntityManager em;
    @Transactional
    @Override
    public void create(Customer customer) {
        if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()) {
            em.persist(customer);  // Persist if the customer is new and not detached
        } else {
            em.merge(customer);  // Merge if the customer exists and is detached
        }
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        try {
            return em.createQuery("SELECT customer FROM Customer AS customer WHERE customer.customerId=:customerId", Customer.class)
                    .setParameter("customerId", customerId)
                    .getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            throw new RecordNotFoundException();
        }
    }

    @Override
    public List<Customer> getByName(String name) throws RecordNotFoundException {
        try {
            return em.createQuery("SELECT customer FROM Customer AS customer WHERE customer.companyName=:name", Customer.class)
                    .setParameter("name", name)
                    .getResultList();
        } catch (javax.persistence.NoResultException e)
        {
            throw new RecordNotFoundException();
        }
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        Customer customer = em.find(Customer.class, customerToUpdate.getCustomerId());
        if (customer == null) {throw new RecordNotFoundException();}

        customer.setCompanyName(customerToUpdate.getCompanyName());
        customer.setEmail(customerToUpdate.getEmail());
        customer.setTelephone(customerToUpdate.getTelephone());
        customer.setNotes(customerToUpdate.getNotes());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        Customer customer = em.find(Customer.class, oldCustomer.getCustomerId());
        em.remove(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return em.createQuery("SELECT customer FROM Customer AS customer", Customer.class).getResultList();
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {

        Customer customer = em.createQuery("SELECT customer FROM Customer AS customer LEFT JOIN FETCH customer.calls WHERE customer.customerId =:customerId", Customer.class)
                .setParameter("customerId", customerId)
                .getSingleResult();
        if (customer == null) {throw new RecordNotFoundException();}
        return customer;
    }

    @Override
    public void addCall(Call newCall, String customerId) throws RecordNotFoundException {
        Customer customer = em.find(Customer.class, customerId);
        if (customer == null) {throw new RecordNotFoundException();}
        customer.addCall(newCall);
    }
}

