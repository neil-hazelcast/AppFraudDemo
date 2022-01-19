/*
 * Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.stickels.appfrauddemo.persistence;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapStore;

import java.sql.*;
import java.util.*;

public class CustomerMapStore implements MapStore<CustomerKey, Customer>
{

    private final static ILogger log = Logger.getLogger(CustomerMapStore.class);

    private Connection conn;

    private static final String insertCustomerAccountTemplate =
            "insert into CustomerAccount (AccountNumber, CustomerID) " +
                    " values (?, ?)";
    private static final String insertCustomerTemplate =
            "insert into Customer (CustomerID, CustomerName, CustomerSSN, CustomerDOB, CustomerAddress, CustomerCity, CustomerState, CustomerZip, CustomerEmail, CustomerPhoneNumber, CustomerLengthRelationship, CustomerNumAccounts, CustomerIncome, CustomerJobTitle, CustomerEmployer)" +
                    " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String updateCustomerTemplate =
            "update Customer set CustomerName = ?, CustomerSSN = ?, CustomerDOB = ?, CustomerAddress = ?, CustomerCity = ?, CustomerState = ?, CustomerZip = ?, CustomerEmail = ?, CustomerPhoneNumber = ?, CustomerLengthRelationship = ?, CustomerNumAccounts = ?, CustomerIncome = ?, CustomerJobTitle = ?, CustomerEmployer = ?" +
                    " where CustomerID = ?";

    private static String selectCustomerAccountsTemplate =
            "select AccountNumber,CustomerID from CustomerAccount";
    private static String selectCustomerTemplate =
            "select CustomerName, CustomerSSN, CustomerDOB, CustomerAddress, CustomerCity, CustomerState, CustomerZip, CustomerEmail, CustomerPhoneNumber, CustomerLengthRelationship, CustomerNumAccounts, CustomerIncome, CustomerJobTitle, CustomerEmployer" +
                    " from Customer where CustomerID = ?";

    private PreparedStatement insertCustomerAccountStatement;
    private PreparedStatement insertCustomerStatement;
    private PreparedStatement updateCustomerStatement;
    private PreparedStatement selectCustomerAccountsStatement;
    private PreparedStatement selectCustomerStatement;

    public CustomerMapStore()
    {
        AppFraudDemoDB db = new AppFraudDemoDB();
        conn = db.establishConnection();
    }

    // MapStore implementation

    @Override
    public void store(CustomerKey customerKey, Customer customer)
    {
        writeCustomer(customerKey, customer);
    }

    @Override
    public void storeAll(Map<CustomerKey, Customer> map)
    {
        for(CustomerKey custKey: map.keySet())
        {
            Customer cust = map.get(custKey);
            store(custKey, cust);
        }
    }

    @Override
    public void delete(CustomerKey customerKey)
    {
        log.info("Delete called for CustomerID "+customerKey.getCustomerId()+" but deletes aren't implemented");
    }

    @Override
    public void deleteAll(Collection<CustomerKey> collection)
    {
        log.info("Delete all called for Customer but deletes aren't implemented");
    }

    // MapLoader implementation

    @Override
    public Customer load(CustomerKey customerKey)
    {
        return readCustomer(customerKey);
    }

    @Override
    public Map<CustomerKey, Customer> loadAll(Collection<CustomerKey> collection)
    {
        Map<CustomerKey, Customer> custs = new HashMap<CustomerKey, Customer>();
        for(CustomerKey custKey: collection)
        {
            Customer cust = load(custKey);
            if(cust != null)
                custs.put(custKey, cust);
        }
        return custs;
    }

    @Override
    public Iterable<CustomerKey> loadAllKeys()
    {
        List<CustomerKey> keys = new ArrayList<CustomerKey>();
        try
        {
            if(selectCustomerAccountsStatement == null)
                selectCustomerAccountsStatement = conn.prepareStatement(selectCustomerAccountsTemplate);
            ResultSet rs = selectCustomerAccountsStatement.executeQuery();
            if(rs == null)
                return null;
            while(rs.next())
            {
                CustomerKey key = new CustomerKey(rs.getInt(2), rs.getInt(1));
                keys.add(key);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        log.info("Returning "+keys.size()+" keys from CustomerAccount table");
        return keys;
    }

    private synchronized void writeCustomer(CustomerKey customerKey, Customer cust)
    {
        try
        {
            writeCustomerAccount(customerKey.getAccountNum(), customerKey.getCustomerId());
            if(insertCustomerStatement == null)
                insertCustomerStatement = conn.prepareStatement(insertCustomerTemplate);
            insertCustomerStatement.setInt(1, customerKey.getCustomerId());
            insertCustomerStatement.setString(2, cust.getName());
            insertCustomerStatement.setString(3, cust.getSsn());
            insertCustomerStatement.setDate(4, new java.sql.Date(cust.getDob().getTime()));
            insertCustomerStatement.setString(5, cust.getAddress());
            insertCustomerStatement.setString(6, cust.getCity());
            insertCustomerStatement.setString(7, cust.getState());
            insertCustomerStatement.setString(8, cust.getZip());
            insertCustomerStatement.setString(9, cust.getEmail());
            insertCustomerStatement.setString(10, cust.getPhoneNumber());
            insertCustomerStatement.setInt(11, cust.getLengthRelationship());
            insertCustomerStatement.setInt(12, cust.getNumAccounts());
            insertCustomerStatement.setDouble(13, cust.getIncome());
            insertCustomerStatement.setString(14, cust.getJobTitle());
            insertCustomerStatement.setString(15, cust.getEmployer());
            insertCustomerStatement.executeUpdate();
            log.info("CustomerID: "+cust.getCustomerID()+" added to DB");
        } catch (SQLIntegrityConstraintViolationException e)
        {
            try
            {
                if(updateCustomerStatement == null)
                    updateCustomerStatement = conn.prepareStatement(insertCustomerTemplate);
                updateCustomerStatement.setString(1, cust.getName());
                updateCustomerStatement.setString(2, cust.getSsn());
                updateCustomerStatement.setDate(3, new java.sql.Date(cust.getDob().getTime()));
                updateCustomerStatement.setString(4, cust.getAddress());
                updateCustomerStatement.setString(5, cust.getCity());
                updateCustomerStatement.setString(6, cust.getState());
                updateCustomerStatement.setString(7, cust.getZip());
                updateCustomerStatement.setString(8, cust.getEmail());
                updateCustomerStatement.setString(9, cust.getPhoneNumber());
                updateCustomerStatement.setInt(10, cust.getLengthRelationship());
                updateCustomerStatement.setInt(11, cust.getNumAccounts());
                updateCustomerStatement.setDouble(12, cust.getIncome());
                updateCustomerStatement.setString(13, cust.getJobTitle());
                updateCustomerStatement.setString(14, cust.getEmployer());
                updateCustomerStatement.setInt(15, cust.getCustomerID());
                updateCustomerStatement.executeUpdate();
                log.info("CustomerID: "+cust.getCustomerID()+" updated in DB");
            } catch (SQLException e2)
            {
                e2.printStackTrace();
            }
        }  catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void writeCustomerAccount(Integer accountNum, Integer customerID)
    {
        try
        {
            if (insertCustomerAccountStatement == null)
                insertCustomerAccountStatement = conn.prepareStatement(insertCustomerAccountTemplate);
            insertCustomerAccountStatement.setInt(1, accountNum);
            insertCustomerAccountStatement.setInt(2, customerID);
            insertCustomerAccountStatement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e)
        {
            // do nothing, it's fine
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized Customer readCustomer(CustomerKey customerKey)
    {
        try
        {
            if (selectCustomerStatement == null)
                selectCustomerStatement = conn.prepareStatement(selectCustomerTemplate);
            selectCustomerStatement.setInt(1, customerKey.getCustomerId());
            ResultSet rs = selectCustomerStatement.executeQuery();
            Customer cust = new Customer();
            if (rs == null) return null;
            while (rs.next())
            {
                cust.setCustomerKey(customerKey);
                cust.setCustomerID(customerKey.getCustomerId());
                cust.setName(rs.getString("CustomerName"));
                cust.setSsn(rs.getString("CustomerSSN"));
                cust.setDob(new java.util.Date(rs.getDate("CustomerDOB").getTime()));
                cust.setAddress(rs.getString("CustomerAddress"));
                cust.setCity(rs.getString("CustomerCity"));
                cust.setState(rs.getString("CustomerState"));
                cust.setZip(rs.getString("CustomerZip"));
                cust.setEmail(rs.getString("CustomerEmail"));
                cust.setPhoneNumber(rs.getString("CustomerPhoneNumber"));
                cust.setLengthRelationship(rs.getInt("CustomerLengthRelationship"));
                cust.setNumAccounts(rs.getInt("CustomerNumAccounts"));
                cust.setIncome(rs.getDouble("CustomerIncome"));
                cust.setJobTitle(rs.getString("CustomerJobTitle"));
                cust.setEmployer(rs.getString("CustomerEmployer"));
                log.info("Customer: "+customerKey.getCustomerId()+" read from DB");
            }
            return cust;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
