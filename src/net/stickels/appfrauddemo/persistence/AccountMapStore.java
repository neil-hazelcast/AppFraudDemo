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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountMapStore implements MapStore<Integer, Account> {

    private final static ILogger log = Logger.getLogger(AccountMapStore.class);

    private final Connection conn;

    private static final String insertAccountTemplate =
            "insert into Account (AccountNumber, AccountProduct, AccountStatus, AccountBalance, AccountCreditLine, AccountSuccessfulPayments, AccountMissedPayments, AccountChargeoffPrinciple, AccountOpenDate, AccountClosedDate, AccountConsecutiveMissedMonths, AccountFraudFlag)" +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String insertCustomerAccountTemplate =
            "insert into CustomerAccount (AccountNumber, CustomerID) " +
            " values (?, ?)";

    private static final String updateAccountTemplate =
            "update Account set AccountProduct = ?, AccountStatus = ?, AccountBalance = ?, AccountCreditLine = ?, AccountSuccessfulPayments = ?, AccountMissedPayments = ?, AccountChargeoffPrinciple = ?, AccountOpenDate = ?, AccountClosedDate = ?, AccountConsecutiveMissedMonths = ?, AccountFraudFlag = ?" +
            " where AccountNumber = ?";

    private static final String selectAccountTemplate =
            "select AccountProduct, AccountStatus, AccountBalance, AccountCreditLine, AccountSuccessfulPayments, AccountMissedPayments, AccountChargeoffPrinciple, AccountOpenDate, AccountClosedDate, AccountConsecutiveMissedMonths, AccountFraudFlag" +
            " from Account where AccountNumber = ?";
    private static final String selectCustomerAccountTemplate =
            "select CustomerID from CustomerAccount where AccountNumber = ?";
    private static final String selectAccountIDsTemplate =
            "select AccountNumber from Account";

    private PreparedStatement insertAccountStatement;
    private PreparedStatement insertCustomerAccountStatement;
    private PreparedStatement updateAccountStatement;
    private PreparedStatement selectAccountStatement;
    private PreparedStatement selectCustomerAccountStatement;
    private PreparedStatement selectAccountIDsStatement;

    public AccountMapStore()
    {
        AppFraudDemoDB db = new AppFraudDemoDB();
        conn = db.establishConnection();

    }

    // MapStore implementation

    @Override
    public void store(Integer accountNum, Account account)
    {
        writeAccount(accountNum, account);
    }

    @Override
    public void storeAll(Map<Integer, Account> map)
    {
        for(Integer acctNum: map.keySet())
        {
            Account acct = map.get(acctNum);
            store(acctNum, acct);
        }
    }

    @Override
    public void delete(Integer integer)
    {
        log.info("Delete called for AccountNumber "+integer+" but deletes aren't implemented");
    }

    @Override
    public void deleteAll(Collection<Integer> collection)
    {
        log.info("Delete all called for Account but deletes aren't implemented");
    }

    // MapLoader implementation

    @Override
    public Account load(Integer acctNum)
    {
        return readAccount(acctNum);
    }

    @Override
    public Map<Integer, Account> loadAll(Collection<Integer> collection)
    {
        Map<Integer, Account> accts = new HashMap<Integer, Account>();
        for(Integer acctNum: collection)
        {
            Account acct = load(acctNum);
            if(acct != null)
                accts.put(acctNum, acct);
        }
        return accts;
    }

    @Override
    public Iterable<Integer> loadAllKeys()
    {
        List<Integer> keys = new ArrayList<Integer>();
        try
        {
            if(selectAccountIDsStatement == null)
                selectAccountIDsStatement = conn.prepareStatement(selectAccountIDsTemplate);
            ResultSet rs = selectAccountIDsStatement.executeQuery();
            if(rs == null)
                return null;
            while(rs.next())
            {
                keys.add(rs.getInt(1));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        log.info("Returning "+keys.size()+" keys from Account table");
        return keys;
    }

    // my methods
    private synchronized void writeAccount(Integer accountNum, Account account)
    {
        try
        {
            if(insertAccountStatement == null)
                insertAccountStatement = conn.prepareStatement(insertAccountTemplate);
            insertAccountStatement.setInt(1, accountNum);
            insertAccountStatement.setString(2, account.getProduct());
            insertAccountStatement.setString(3, account.getStatus());
            insertAccountStatement.setDouble(4, account.getBalance());
            insertAccountStatement.setDouble(5, account.getCreditLine());
            insertAccountStatement.setInt(6, account.getSuccessfulPayments());
            insertAccountStatement.setInt(7, account.getMissedPayments());
            insertAccountStatement.setDouble(8, account.getChargeoffPrinciple());
            insertAccountStatement.setDate(9, new java.sql.Date(account.getOpenDate().getTime()));
            if(account.getClosedDate() != null)
                insertAccountStatement.setDate(10, new java.sql.Date(account.getClosedDate().getTime()));
            else
                insertAccountStatement.setNull(10, Types.DATE);
            insertAccountStatement.setInt(11, account.getConsecutiveMissedMonths());
            insertAccountStatement.setInt(12, account.getFraudFlag());
            int rowsAffected = insertAccountStatement.executeUpdate();
            log.info("AccountNumber: "+accountNum+" added to DB");
            List<Integer> customerList = account.getCustomerIDList();
            //log.info("customerIDList size is "+customerList.size());
            for(Integer custID : customerList)
            {
                log.info("calling writeCustomerAccount with "+accountNum+" and "+custID);
                writeCustomerAccount(accountNum, custID);
            }
        } catch (SQLIntegrityConstraintViolationException e)
        {
            try
            {
                if (updateAccountStatement == null)
                    updateAccountStatement = conn.prepareStatement(updateAccountTemplate);
                updateAccountStatement.setString(1, account.getProduct());
                updateAccountStatement.setString(2, account.getStatus());
                updateAccountStatement.setDouble(3, account.getBalance());
                updateAccountStatement.setDouble(4, account.getCreditLine());
                updateAccountStatement.setInt(5, account.getSuccessfulPayments());
                updateAccountStatement.setInt(6, account.getMissedPayments());
                updateAccountStatement.setDouble(7, account.getChargeoffPrinciple());
                updateAccountStatement.setDate(8, new java.sql.Date(account.getOpenDate().getTime()));
                if(account.getClosedDate() != null)
                    updateAccountStatement.setDate(9, new java.sql.Date(account.getClosedDate().getTime()));
                else
                    updateAccountStatement.setNull(9, Types.DATE);
                updateAccountStatement.setInt(10, account.getConsecutiveMissedMonths());
                updateAccountStatement.setInt(11, account.getFraudFlag());
                updateAccountStatement.setInt(12, accountNum);
                int rowsAffected = updateAccountStatement.executeUpdate();
                log.info("AccountNumber: "+accountNum+" updated in DB");
                List<Integer> customerList = account.getCustomerIDList();
                //log.info("customerIDList size is "+customerList.size());
                for(Integer custID : customerList)
                {
                    log.info("calling writeCustomerAccount with "+accountNum+" and "+custID);
                    writeCustomerAccount(accountNum, custID);
                }
            } catch (SQLException e2)
            {
                e2.printStackTrace();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void writeCustomerAccount(Integer accountNum, int customerID)
    {
        log.info("writeCustomerAccount called to write "+accountNum+" and "+customerID);
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

    private synchronized Account readAccount(Integer acctNum)
    {
        try
        {
            if(selectAccountStatement == null)
                selectAccountStatement = conn.prepareStatement(selectAccountTemplate);
            selectAccountStatement.setInt(1, acctNum);
            ResultSet rs = selectAccountStatement.executeQuery();
            Account acct = new Account();
            if(rs == null)
                return null;
            while(rs.next())
            {
                acct.setAccountNumber(acctNum);
                acct.setProduct(rs.getString("AccountProduct"));
                acct.setStatus(rs.getString("AccountStatus"));
                acct.setBalance(rs.getDouble("AccountBalance"));
                acct.setCreditLine(rs.getDouble("AccountCreditLine"));
                acct.setSuccessfulPayments(rs.getInt("AccountSuccessfulPayments"));
                acct.setMissedPayments(rs.getInt("AccountMissedPayments"));
                acct.setChargeoffPrinciple(rs.getDouble("AccountChargeoffPrinciple"));
                acct.setOpenDate(new java.util.Date(rs.getDate("AccountOpenDate").getTime()));
                acct.setSqlClosedDate(rs.getDate("AccountClosedDate"));
                acct.setConsecutiveMissedMonths(rs.getInt("AccountConsecutiveMissedMonths"));
                acct.setFraudFlag(rs.getInt("AccountFraudFlag"));
                log.info("AccountNumber: "+acctNum+" read from DB");
            }
            if(selectCustomerAccountStatement == null)
                selectCustomerAccountStatement = conn.prepareStatement(selectCustomerAccountTemplate);
            selectCustomerAccountStatement.setInt(1, acctNum);
            ResultSet rs2 = selectCustomerAccountStatement.executeQuery();
            if(rs2 != null)
            {
                List<Integer> custIdList = new ArrayList<Integer>();
                while(rs2.next())
                {
                    custIdList.add(rs2.getInt(1));
                }
                acct.setCustomerIDList(custIdList);
            }
            return acct;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
