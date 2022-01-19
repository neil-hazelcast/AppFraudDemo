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

import java.io.Serializable;

import java.util.Date;
import java.util.List;

public class Application implements Serializable
{
    private int applicationNumber;
    private Date appDate;
    private String accountNumber;
    private int applicationSequence;
    private String channel;
    private String product;
    private Double requestedAmount;
    private Double amountFinanced;
    private String ipAddress;
    private String deviceId;
    private String applicationMethod;
    private int manualReview;
    private int fraudFlag;
    private int riskScore;
    private int ficoScore;
    private String status;
    private String declineReason;
    private int acceptance;
    private List<Integer> applicantIDs;

    public int getApplicationNumber()
    {
        return applicationNumber;
    }

    public void setApplicationNumber(int applicationNumber)
    {
        this.applicationNumber = applicationNumber;
    }

    public Date getAppDate()
    {
        return appDate;
    }

    public void setAppDate(Date appDate)
    {
        this.appDate = appDate;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public int getApplicationSequence()
    {
        return applicationSequence;
    }

    public void setApplicationSequence(int applicationSequence)
    {
        this.applicationSequence = applicationSequence;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct(String product)
    {
        this.product = product;
    }

    public Double getRequestedAmount()
    {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount)
    {
        this.requestedAmount = requestedAmount;
    }

    public Double getAmountFinanced()
    {
        return amountFinanced;
    }

    public void setAmountFinanced(Double amountFinanced)
    {
        this.amountFinanced = amountFinanced;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getApplicationMethod()
    {
        return applicationMethod;
    }

    public void setApplicationMethod(String applicationMethod)
    {
        this.applicationMethod = applicationMethod;
    }

    public int getManualReview()
    {
        return manualReview;
    }

    public void setManualReview(int manualReview)
    {
        this.manualReview = manualReview;
    }

    public int getFraudFlag()
    {
        return fraudFlag;
    }

    public void setFraudFlag(int fraudFlag)
    {
        this.fraudFlag = fraudFlag;
    }

    public int getRiskScore()
    {
        return riskScore;
    }

    public void setRiskScore(int riskScore)
    {
        this.riskScore = riskScore;
    }

    public int getFicoScore()
    {
        return ficoScore;
    }

    public void setFicoScore(int ficoScore)
    {
        this.ficoScore = ficoScore;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDeclineReason()
    {
        return declineReason;
    }

    public void setDeclineReason(String declineReason)
    {
        this.declineReason = declineReason;
    }

    public int getAcceptance()
    {
        return acceptance;
    }

    public void setAcceptance(int acceptance)
    {
        this.acceptance = acceptance;
    }

    public List<Integer> getApplicantIDs()
    {
        return applicantIDs;
    }

    public void setApplicantIDs(List<Integer> applicantIDs)
    {
        this.applicantIDs = applicantIDs;
    }
}
