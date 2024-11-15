package com.yrgo.dataaccess;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDaoJdbcTemplateImpl implements CustomerDao{
    private JdbcTemplate jdbcTemplate;

    public CustomerDaoJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public class CallRowMapper implements RowMapper<Call> {
        @Override
        public Call mapRow(ResultSet rs, int rowNum) throws SQLException {
            Call call = new Call();
            call.setTimeAndDate(new Date(rs.getTimestamp("TIME_AND_DATE").getTime()));
            call.setNotes(rs.getString("NOTES"));
            return call;
        }
    }

    private class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            String customerId = rs.getString(1);
            String companyName = rs.getString(2);
            String email = rs.getString(3);
            String phone = rs.getString(4);
            String notes = rs.getString(5);

            Customer customer = new Customer("" + customerId, companyName,
                    email, phone, notes);

            List<Call> calls = new ArrayList<>();

            while (rs.next() && rs.getString("CUSTOMER_ID").equals(customerId))
            {
                Call call = new Call(rs.getString("CALL_NOTES"), rs.getTimestamp("CALL_TIME"));
                if (call != null) calls.add(call);

            }

            customer.setCalls(calls);

            return customer;
        }
    }
    private void createTables()	{
        try{
            // CUSTOMER TABLE
            this.jdbcTemplate.update("CREATE TABLE CUSTOMER " +
                    "(CUSTOMER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1), " +
                    "COMPANY_NAME VARCHAR(255)," +
                    "EMAIL VARCHAR(255), " +
                    "TELEPHONE VARCHAR(50), " +
                    "NOTES VARCHAR(255)," +
                    "PRIMARY KEY (CUSTOMER_ID)");

            // CALL TABLE
            this.jdbcTemplate.update("CREATE TABLE CUSTOMER_CALL " +
                    "(CALL_ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1), " +
                    "CUSTOMER_ID VARCHAR(10)," +
                    "TIME_AND_DATE TIMESTAMP, " +
                    "NOTES VARCHAR(255), " +
                    "PRIMARY KEY (CALL_ID)," +
                    "FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(CUSTOMER_ID)");
        }catch (org.springframework.jdbc.BadSqlGrammarException e){
            System.err.println("The CUSTOMER or CALL table already exists.");
        }
    }
    @Override
    public void create(Customer customer) {
        jdbcTemplate.update(
                "INSERT INTO CUSTOMER (CUSTOMER_ID, COMPANY_NAME, EMAIL, TELEPHONE, NOTES) VALUES (?, ?, ?, ?, ?)",
                customer.getCustomerId(),
                customer.getCompanyName(),
                customer.getEmail(),
                customer.getTelephone(),
                customer.getNotes()
        );
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        return jdbcTemplate.queryForObject("SELECT CUSTOMER_ID, " +
                "COMPANY_NAME, " +
                "EMAIL, " +
                "PHONE, " +
                "NOTES " +
                "FROM CUSTOMER WHERE CUSTOMER_ID=?", new CustomerRowMapper(), customerId);
    }

    @Override
    public List<Customer> getByName(String name) throws RecordNotFoundException {

        List<Customer> result = jdbcTemplate.query("SELECT CUSTOMER_ID, " +
                "COMPANY_NAME, " +
                "EMAIL, " +
                "PHONE, " +
                "NOTES " +
                "FROM CUSTOMER WHERE COMPANY_NAME=?", new CustomerRowMapper(), name);

        if (result.size() < 1)
        {
            throw new RecordNotFoundException();
        }
        else
        {
            return result;
        }
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        jdbcTemplate.update(
                "UPDATE CUSTOMER " +
                        "SET COMPANY_NAME=?, " +
                        "EMAIL=?, " +
                        "PHONE=?, " +
                        "NOTES=? " +
                        "WHERE CUSTOMER_ID=?",
                customerToUpdate.getCompanyName(),
                customerToUpdate.getEmail(),
                customerToUpdate.getTelephone(), // Fixed: should be telephone, not email repeated
                customerToUpdate.getNotes(),
                customerToUpdate.getCustomerId()
        );
    }

    @Override
public void delete(Customer oldCustomer) throws RecordNotFoundException {
        jdbcTemplate.update("DELETE FROM CUSTOMER WHERE CUSTOMER_ID=?", oldCustomer.getCustomerId());
}

@Override
public List<Customer> getAllCustomers() {
    return jdbcTemplate.query("SELECT * FROM CUSTOMERS", new CustomerRowMapper());
}

@Override
public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {
    Customer customer = getById(customerId);

    List<Call> calls = jdbcTemplate.query(
            """
            SELECT CALL_ID, TIME_AND_DATE, NOTES
            FROM CUSTOMER_CALL
            WHERE CUSTOMER_ID = ?
            """,
            new CallRowMapper(),
            customerId
    );

    customer.setCalls(calls);
    return customer;
}


@Override
public void addCall(Call newCall, String customerId) throws RecordNotFoundException {

jdbcTemplate.update(
    "INSERT INTO CUSTOMER_CALL (CUSTOMER_ID, TIME_AND_DATE, NOTES) VALUES (?, ?, ?)",
    customerId,
    newCall.getTimeAndDate(),
    newCall.getNotes()
);
}
}

