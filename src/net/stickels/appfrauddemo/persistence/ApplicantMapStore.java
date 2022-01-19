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

import java.sql.Date;
import java.sql.*;
import java.util.*;

public class ApplicantMapStore implements MapStore<ApplicantKey, Applicant>
{

    private final static ILogger log = Logger.getLogger(ApplicantMapStore.class);

    private Connection conn;
    
    private static final String insertApplicantTemplate =
            "insert into Applicant (ApplicantID, ApplicationNumber, ApplicantRole, CustomerID, ApplicantName, ApplicantSSN, ApplicantDOB, ApplicantAddress, ApplicantCity, ApplicantState, ApplicantZip, ApplicantEmailAddress, ApplicantPhoneNumber, ApplicantMonthlyIncome, ApplicantOccupation, ApplicantEmployer)" +
                    " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String updateApplicantTemplate =
            "update Applicant set ApplicationNumber = ?, ApplicantRole = ?, CustomerID = ?, ApplicantName = ?, ApplicantSSN = ?, ApplicantDOB = ?, ApplicantAddress = ?, ApplicantCity = ?, ApplicantState = ?, ApplicantZip = ?, ApplicantEmailAddress = ?, ApplicantPhoneNumber = ?, ApplicantMonthlyIncome = ?, ApplicantOccupation = ?, ApplicantEmployer = ?" +
                    " where ApplicantID = ?";

    private static String selectApplicantTemplate =
            "select ApplicationNumber, ApplicantRole, CustomerID, ApplicantName, ApplicantSSN, ApplicantDOB, ApplicantAddress, ApplicantCity, ApplicantState, ApplicantZip, ApplicantEmailAddress, ApplicantPhoneNumber, ApplicantMonthlyIncome, ApplicantOccupation, ApplicantEmployer" +
                    " from Applicant where ApplicantID = ?";

    private static String selectApplicantIDsTemplate =
            "select ApplicantID, ApplicationNumber from Applicant";

    private PreparedStatement insertApplicantStatement;
    private PreparedStatement updateApplicantStatement;
    private PreparedStatement selectApplicantStatement;
    private PreparedStatement selectApplicantIDsStatement;

    public ApplicantMapStore()
    {
        AppFraudDemoDB db = new AppFraudDemoDB();
        conn = db.establishConnection();
    }

    // MapStore implementation

    @Override
    public void store(ApplicantKey ApplicantKey, Applicant Applicant)
    {
        writeApplicant(ApplicantKey, Applicant);
    }

    @Override
    public void storeAll(Map<ApplicantKey, Applicant> map)
    {
        for(ApplicantKey appKey: map.keySet())
        {
            Applicant app = map.get(appKey);
            store(appKey, app);
        }
    }

    @Override
    public void delete(ApplicantKey ApplicantKey)
    {
        log.info("Delete called for ApplicantID "+ApplicantKey.getApplicantId()+" but deletes aren't implemented");
    }

    @Override
    public void deleteAll(Collection<ApplicantKey> collection)
    {
        log.info("Delete all called for Applicant but deletes aren't implemented");
    }

    // MapLoader implementation

    @Override
    public Applicant load(ApplicantKey ApplicantKey)
    {
        return readApplicant(ApplicantKey);
    }

    @Override
    public Map<ApplicantKey, Applicant> loadAll(Collection<ApplicantKey> collection)
    {
        Map<ApplicantKey, Applicant> apps = new HashMap<ApplicantKey, Applicant>();
        for(ApplicantKey appKey: collection)
        {
            Applicant app = load(appKey);
            if(app != null)
                apps.put(appKey, app);
        }
        return apps;
    }

    @Override
    public Iterable<ApplicantKey> loadAllKeys()
    {
        List<ApplicantKey> keys = new ArrayList<ApplicantKey>();
        try
        {
            if(selectApplicantIDsStatement == null)
                selectApplicantIDsStatement = conn.prepareStatement(selectApplicantIDsTemplate);
            ResultSet rs = selectApplicantIDsStatement.executeQuery();
            if(rs == null)
                return null;
            while(rs.next())
            {
                ApplicantKey key = new ApplicantKey(rs.getInt(1), rs.getInt(2));
                keys.add(key);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        log.info("Returning "+keys.size()+" keys from Applicant table");
        return keys;
    }

    private synchronized void writeApplicant(ApplicantKey applicantKey, Applicant appl)
    {
        try
        {
            if(insertApplicantStatement == null)
                insertApplicantStatement = conn.prepareStatement(insertApplicantTemplate);
            insertApplicantStatement.setInt(1, applicantKey.getApplicantId());
            insertApplicantStatement.setInt(2, applicantKey.getApplicationNum());
            insertApplicantStatement.setString(3, appl.getApplicationRole());
            insertApplicantStatement.setInt(4, appl.getCustomerID());
            insertApplicantStatement.setString(5, appl.getName());
            insertApplicantStatement.setString(6, appl.getSsn());
            insertApplicantStatement.setDate(7, new Date(appl.getDob().getTime()));
            insertApplicantStatement.setString(8, appl.getAddress());
            insertApplicantStatement.setString(9, appl.getCity());
            insertApplicantStatement.setString(10, appl.getState());
            insertApplicantStatement.setString(11, appl.getZip());
            insertApplicantStatement.setString(12, appl.getEmail());
            insertApplicantStatement.setString(13, appl.getPhoneNumber());
            insertApplicantStatement.setDouble(14, appl.getIncome());
            insertApplicantStatement.setString(15, appl.getJobTitle());
            insertApplicantStatement.setString(16, appl.getEmployer());
            insertApplicantStatement.executeUpdate();
            log.info("ApplicantID: "+applicantKey.getApplicantId()+" added to DB");
        } catch (SQLIntegrityConstraintViolationException e)
        {
            try
            {
                if(updateApplicantStatement == null)
                    updateApplicantStatement = conn.prepareStatement(updateApplicantTemplate);
                updateApplicantStatement.setInt(1, applicantKey.getApplicationNum());
                updateApplicantStatement.setString(2, appl.getApplicationRole());
                updateApplicantStatement.setInt(3, appl.getCustomerID());
                updateApplicantStatement.setString(4, appl.getName());
                updateApplicantStatement.setString(5, appl.getSsn());
                updateApplicantStatement.setDate(6, new Date(appl.getDob().getTime()));
                updateApplicantStatement.setString(7, appl.getAddress());
                updateApplicantStatement.setString(8, appl.getCity());
                updateApplicantStatement.setString(9, appl.getState());
                updateApplicantStatement.setString(10, appl.getZip());
                updateApplicantStatement.setString(11, appl.getEmail());
                updateApplicantStatement.setString(12, appl.getPhoneNumber());
                updateApplicantStatement.setDouble(13, appl.getIncome());
                updateApplicantStatement.setString(14, appl.getJobTitle());
                updateApplicantStatement.setString(15, appl.getEmployer());
                updateApplicantStatement.setInt(16, applicantKey.getApplicantId());
                updateApplicantStatement.executeUpdate();
                log.info("ApplicantID: "+appl.getApplicantID()+" updated in DB");
            } catch (SQLException e2)
            {
                e2.printStackTrace();
            }
        }  catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized Applicant readApplicant(ApplicantKey applicantKey)
    {
        try
        {
            if (selectApplicantStatement == null)
                selectApplicantStatement = conn.prepareStatement(selectApplicantTemplate);
            selectApplicantStatement.setInt(1, applicantKey.getApplicantId());
            ResultSet rs = selectApplicantStatement.executeQuery();
            if (rs == null) return null;
            Applicant appl = new Applicant();
            while (rs.next())
            {
                appl.setApplicantKey(applicantKey);
                appl.setApplicantID(applicantKey.getApplicantId());
                appl.setApplicationNumber(rs.getInt("ApplicationNumber"));
                appl.setApplicationRole(rs.getString("ApplicantRole"));
                appl.setCustomerID(rs.getInt("CustomerID"));
                appl.setName(rs.getString("ApplicantName"));
                appl.setSsn(rs.getString("ApplicantSSN"));
                appl.setDob(new java.util.Date(rs.getDate("ApplicantDOB").getTime()));
                appl.setAddress(rs.getString("ApplicantAddress"));
                appl.setCity(rs.getString("ApplicantCity"));
                appl.setState(rs.getString("ApplicantState"));
                appl.setZip(rs.getString("ApplicantZip"));
                appl.setEmail(rs.getString("ApplicantEmailAddress"));
                appl.setPhoneNumber(rs.getString("ApplicantPhoneNumber"));
                appl.setIncome(rs.getDouble("ApplicantMonthlyIncome"));
                appl.setJobTitle(rs.getString("ApplicantOccupation"));
                appl.setEmployer(rs.getString("ApplicantEmployer"));
                log.info("Applicant: "+applicantKey.getApplicantId()+" read from DB");
            }
            return appl;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
