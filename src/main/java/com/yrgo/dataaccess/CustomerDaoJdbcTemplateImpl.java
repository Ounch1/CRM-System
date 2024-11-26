package com.yrgo.dataaccess;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CustomerDaoJdbcTemplateImpl implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;

    // INSERT UPDATE DELETE SQL
    private static final String INSERT_SQL
            = "INSERT INTO CUSTOMER_TBL(CUSTOMER_ID, COMPANY_NAME, EMAIL, PHONE, NOTES) VALUES(?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL
            = "UPDATE CUSTOMER_TBL SET COMPANY_NAME=?, EMAIL=?, PHONE=?, NOTES=? WHERE CUSTOMER_ID=?";
    private static final String DELETE_SQL
            = "DELETE FROM CUSTOMER_TBL WHERE CUSTOMER_ID=?";

    // GET SQL
    private static final String GET_ID_SQL
            = "SELECT CUSTOMER_ID, COMPANY_NAME, EMAIL, PHONE, NOTES FROM CUSTOMER_TBL WHERE CUSTOMER_ID=?";
    private static final String GET_NAME_SQL
            = "SELECT CUSTOMER_ID, COMPANY_NAME, EMAIL, PHONE, NOTES FROM CUSTOMER_TBL WHERE COMPANY_NAME=?";
    private static final String GET_ALL_SQL
            = "SELECT CUSTOMER_ID, COMPANY_NAME, EMAIL, PHONE FROM CUSTOMER_TBL";
    private static final String ADD_CALL_SQL
            = "INSERT INTO CALL_TBL(CUSTOMER_ID, TIME_AND_DATE, NOTES) VALUES(?, ?, ?)";
    @Autowired
    public CustomerDaoJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ROW MAPPING AREA
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

    private class CustomerDetailsRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            String customerId = rs.getString("CUSTOMER_ID");
            String companyName = rs.getString("COMPANY_NAME");
            String email = rs.getString("EMAIL");
            String phone = rs.getString("PHONE");
            String customerNotes = rs.getString("NOTES");

            Customer customer = new Customer(customerId, companyName, email, phone, customerNotes);
            List<Call> calls = new ArrayList<>();

            // Collect all the calls for this customer
            do {
                Date callTime = rs.getTimestamp("CALL_TIME");
                String callNotes = rs.getString("CALL_NOTES");

                if (callTime != null) {
                    calls.add(new Call(callNotes, callTime));
                }
            } while (rs.next() && rs.getString("CUSTOMER_ID").equals(customerId));

            customer.setCalls(calls);
            return customer;
        }
    }

    // TABLE MANIPULATION AREA
    @PostConstruct
    private void createTables() {
        createCustomerTableIfNotExists();
        createCallTableIfNotExists();
    }

    private void createCustomerTableIfNotExists() {
        String checkCustomerTableSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMER_TBL'";
        int customerTableCount = jdbcTemplate.queryForObject(checkCustomerTableSql, Integer.class);

        if (customerTableCount == 0) {
            jdbcTemplate.update("""
                CREATE TABLE CUSTOMER_TBL (
                    CUSTOMER_ID VARCHAR(50),
                    COMPANY_NAME VARCHAR(255),
                    EMAIL VARCHAR(200),
                    PHONE VARCHAR(50),
                    NOTES VARCHAR(255),
                    PRIMARY KEY (CUSTOMER_ID)
                )
            """);
        }
    }

    private void createCallTableIfNotExists() {
        String checkCallTableSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CALL_TBL'";
        int callTableCount = jdbcTemplate.queryForObject(checkCallTableSql, Integer.class);

        if (callTableCount == 0) {
            jdbcTemplate.update("""
                CREATE TABLE CALL_TBL (
                    CALL_ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1),
                    CUSTOMER_ID VARCHAR(50),
                    TIME_AND_DATE TIMESTAMP,
                    NOTES VARCHAR(255),
                    PRIMARY KEY (CALL_ID),
                    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER_TBL(CUSTOMER_ID)
                )
            """);
        }
    }

    // CUSTOMER CRUD OPERATION AREA
    @Override
    public void create(Customer customer) {
        jdbcTemplate.update(INSERT_SQL, customer.getCustomerId(), customer.getCompanyName(),
                customer.getEmail(), customer.getTelephone(), customer.getNotes());
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        return jdbcTemplate.queryForObject(GET_ID_SQL, new CustomerRowMapper(), customerId);
    }

    @Override
    public List<Customer> getByName(String name) {
        return jdbcTemplate.query(GET_NAME_SQL, new CustomerRowMapper(), name);
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        jdbcTemplate.update(UPDATE_SQL, customerToUpdate.getCompanyName(), customerToUpdate.getEmail(),
                customerToUpdate.getTelephone(), customerToUpdate.getNotes(), customerToUpdate.getCustomerId());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        jdbcTemplate.update(DELETE_SQL, oldCustomer.getCustomerId());
    }

    @Override
    public List<Customer> getAllCustomers() {
        return jdbcTemplate.query(GET_ALL_SQL, new CustomerDetailsRowMapper(), false);
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
        jdbcTemplate.update(ADD_CALL_SQL, customerId, newCall.getTimeAndDate(), newCall.getNotes());
    }
}