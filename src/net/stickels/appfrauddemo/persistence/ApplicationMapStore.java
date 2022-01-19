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

public class ApplicationMapStore implements MapStore<Integer, Application>
{
    private final static ILogger log = Logger.getLogger(AccountMapStore.class);

    private final Connection conn;

    private static final String insertApplicationTemplate =
            "insert into Application(ApplicationNumber, ApplicationDate, AccountNumber, ApplicationSequence, ApplicationChannel, ApplicationProduct, ApplicationRequestedAmount, ApplicationAmountFinanced, ApplicationIPAddress, ApplicationDeviceID, ApplicationMethod, ApplicationManualReview, ApplicationFraudFlag, ApplicationRiskScore, FICO_SCORE, ApplicationStatus, ApplicationDeclineReason, ApplicationAcceptance)"+
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String updateApplicationTemplate =
            "update Application set ApplicationDate = ?, AccountNumber = ?, ApplicationSequence = ?, ApplicationChannel = ?, ApplicationProduct = ?, ApplicationRequestedAmount = ?, ApplicationAmountFinanced = ?, ApplicationIPAddress = ?, ApplicationDeviceID = ?, ApplicationMethod = ?, ApplicationManualReview = ?, ApplicationFraudFlag = ?, ApplicationRiskScore = ?, FICO_SCORE = ?, ApplicationStatus = ?, ApplicationDeclineReason = ?, ApplicationAcceptance = ?"+
            " where ApplicationNumber = ?";
    private static final String selectApplicationTemplate =
            "select ApplicationDate, AccountNumber, ApplicationSequence, ApplicationChannel, ApplicationProduct, ApplicationRequestedAmount, ApplicationAmountFinanced, ApplicationIPAddress, ApplicationDeviceID, ApplicationMethod, ApplicationManualReview, ApplicationFraudFlag, ApplicationRiskScore, FICO_SCORE, ApplicationStatus, ApplicationDeclineReason, ApplicationAcceptance"+
            " from Application where ApplicationNumber = ?";
    private static final String selectApplicantIDsTemplate =
            "select ApplicantID from Applicant where ApplicationNumber = ?";
    private static final String selectApplicationIDsTemplate =
            "select ApplicationNumber from Application";

    private PreparedStatement insertApplicationStatement;
    private PreparedStatement updateApplicationStatement;
    private PreparedStatement selectApplicationStatement;
    private PreparedStatement selectApplicantIDsStatement;
    private PreparedStatement selectApplicationIDsStatement;

    public ApplicationMapStore()
    {
        AppFraudDemoDB db = new AppFraudDemoDB();
        conn = db.establishConnection();
    }

    @Override
    public void store(Integer integer, Application application)
    {
        writeApplication(integer, application);
    }

    @Override
    public void storeAll(Map<Integer, Application> map)
    {
        for(Integer appNum: map.keySet())
        {
            Application app = map.get(appNum);
            store(appNum, app);
        }
    }

    @Override
    public void delete(Integer integer)
    {
        log.info("Delete called for ApplicationNumber "+integer+" but deletes aren't implemented");
    }

    @Override
    public void deleteAll(Collection<Integer> collection)
    {
        log.info("Delete all called for Application but deletes aren't implemented");
    }

    @Override
    public Application load(Integer integer)
    {
        return readApplication(integer);
    }

    @Override
    public Map<Integer, Application> loadAll(Collection<Integer> collection)
    {
        Map<Integer, Application> apps = new HashMap<Integer, Application>();
        for(Integer appNum: collection)
        {
            Application app = load(appNum);
            if(app != null)
                apps.put(appNum, app);
        }
        return apps;
    }

    @Override
    public Iterable<Integer> loadAllKeys()
    {
        List<Integer> keys = new ArrayList<Integer>();
        try
        {
            if(selectApplicationIDsStatement == null)
                selectApplicationIDsStatement = conn.prepareStatement(selectApplicationIDsTemplate);
            ResultSet rs = selectApplicationIDsStatement.executeQuery();
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
        log.info("Returning "+keys.size()+" keys from Application table");
        return keys;
    }

    // my methods

    private synchronized void writeApplication(Integer appNumber, Application app)
    {
        try
        {
            if(insertApplicationStatement == null)
                insertApplicationStatement = conn.prepareStatement(insertApplicationTemplate);
            insertApplicationStatement.setInt(1, appNumber);
            insertApplicationStatement.setDate(2, new java.sql.Date(app.getAppDate().getTime()));
            insertApplicationStatement.setString(3, app.getAccountNumber());
            insertApplicationStatement.setInt(4, app.getApplicationSequence());
            insertApplicationStatement.setString(5, app.getChannel());
            insertApplicationStatement.setString(6, app.getProduct());
            insertApplicationStatement.setDouble(7, app.getRequestedAmount());
            insertApplicationStatement.setDouble(8, app.getAmountFinanced());
            insertApplicationStatement.setString(9, app.getIpAddress());
            insertApplicationStatement.setString(10, app.getDeviceId());
            insertApplicationStatement.setString(11, app.getApplicationMethod());
            insertApplicationStatement.setInt(12, app.getManualReview());
            insertApplicationStatement.setInt(13, app.getFraudFlag());
            insertApplicationStatement.setInt(14, app.getRiskScore());
            insertApplicationStatement.setInt(15, app.getFicoScore());
            insertApplicationStatement.setString(16, app.getStatus());
            insertApplicationStatement.setString(17, app.getDeclineReason());
            insertApplicationStatement.setInt(18, app.getAcceptance());
            int rowsAffected = insertApplicationStatement.executeUpdate();
            log.info("ApplicationNumber: "+appNumber+" added to DB");

        } catch (SQLIntegrityConstraintViolationException e)
        {
            try
            {
                if (updateApplicationStatement == null)
                    updateApplicationStatement = conn.prepareStatement(updateApplicationTemplate);
                updateApplicationStatement.setDate(1, new java.sql.Date(app.getAppDate().getTime()));
                updateApplicationStatement.setString(2, app.getAccountNumber());
                updateApplicationStatement.setInt(3, app.getApplicationSequence());
                updateApplicationStatement.setString(4, app.getChannel());
                updateApplicationStatement.setString(5, app.getProduct());
                updateApplicationStatement.setDouble(6, app.getRequestedAmount());
                updateApplicationStatement.setDouble(7, app.getAmountFinanced());
                updateApplicationStatement.setString(8, app.getIpAddress());
                updateApplicationStatement.setString(9, app.getDeviceId());
                updateApplicationStatement.setString(10, app.getApplicationMethod());
                updateApplicationStatement.setInt(11, app.getManualReview());
                updateApplicationStatement.setInt(12, app.getFraudFlag());
                updateApplicationStatement.setInt(13, app.getRiskScore());
                updateApplicationStatement.setInt(14, app.getFicoScore());
                updateApplicationStatement.setString(15, app.getStatus());
                updateApplicationStatement.setString(16, app.getDeclineReason());
                updateApplicationStatement.setInt(17, app.getAcceptance());
                updateApplicationStatement.setInt(18, appNumber);
                int rowsAffected = updateApplicationStatement.executeUpdate();
                log.info("ApplicationNumber: "+appNumber+" updated in DB");
            } catch (SQLException e2)
            {
                e2.printStackTrace();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized Application readApplication(Integer appNum)
    {
        try
        {
            if(selectApplicationStatement == null)
                selectApplicationStatement = conn.prepareStatement(selectApplicationTemplate);
            selectApplicationStatement.setInt(1, appNum);
            ResultSet rs = selectApplicationStatement.executeQuery();
            Application app = new Application();
            if(rs == null)
                return null;
            while(rs.next())
            {
                app.setApplicationNumber(appNum);
                app.setAppDate(new java.util.Date(rs.getDate("ApplicationDate").getTime()));
                app.setAccountNumber(rs.getString("AccountNumber"));
                app.setApplicationSequence(rs.getInt("ApplicationSequence"));
                app.setChannel(rs.getString("ApplicationChannel"));
                app.setProduct(rs.getString("ApplicationProduct"));
                app.setRequestedAmount(rs.getDouble("ApplicationRequestedAmount"));
                app.setAmountFinanced(rs.getDouble("ApplicationAmountFinanced"));
                app.setIpAddress(rs.getString("ApplicationIPAddress"));
                app.setDeviceId(rs.getString("ApplicationDeviceID"));
                app.setApplicationMethod(rs.getString("ApplicationMethod"));
                app.setManualReview(rs.getInt("ApplicationManualReview"));
                app.setFraudFlag(rs.getInt("ApplicationFraudFlag"));
                app.setRiskScore(rs.getInt("ApplicationRiskScore"));
                app.setFicoScore(rs.getInt("FICO_SCORE"));
                app.setStatus(rs.getString("ApplicationStatus"));
                app.setDeclineReason(rs.getString("ApplicationDeclineReason"));
                app.setAcceptance(rs.getInt("ApplicationAcceptance"));
                log.info("ApplicationNumber: "+appNum+" read from DB");
            }
            if(selectApplicantIDsStatement == null)
                selectApplicantIDsStatement = conn.prepareStatement(selectApplicantIDsTemplate);
            selectApplicantIDsStatement.setInt(1, appNum);
            ResultSet rs2 = selectApplicantIDsStatement.executeQuery();
            if(rs2 != null)
            {
                List<Integer> appIdList = new ArrayList<Integer>();
                while(rs2.next())
                {
                    appIdList.add(rs2.getInt(1));
                }
                app.setApplicantIDs(appIdList);
            }
            return app;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
