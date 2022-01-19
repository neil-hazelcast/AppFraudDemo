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

public class Account implements Serializable {

    private int accountNumber;
    private String product;
    private String status;
    private Double balance;
    private Double creditLine;
    private int successfulPayments;
    private int missedPayments;
    private Double chargeoffPrinciple;
    private Date openDate;
    private Date closedDate;
    private int consecutiveMissedMonths;
    private int fraudFlag;
    private List<Integer> customerIDList;

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getCreditLine() {
        return creditLine;
    }

    public void setCreditLine(Double creditLine) {
        this.creditLine = creditLine;
    }

    public int getSuccessfulPayments() {
        return successfulPayments;
    }

    public void setSuccessfulPayments(int successfulPayments) {
        this.successfulPayments = successfulPayments;
    }

    public int getMissedPayments() {
        return missedPayments;
    }

    public void setMissedPayments(int missedPayments) {
        this.missedPayments = missedPayments;
    }

    public Double getChargeoffPrinciple() {
        return chargeoffPrinciple;
    }

    public void setChargeoffPrinciple(Double chargeoffPrinciple) {
        this.chargeoffPrinciple = chargeoffPrinciple;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public void setSqlClosedDate(java.sql.Date closedDate)
    {
        if(closedDate != null)
            this.closedDate = new Date(closedDate.getTime());
    }

    public int getConsecutiveMissedMonths() {
        return consecutiveMissedMonths;
    }

    public void setConsecutiveMissedMonths(int consecutiveMissedMonths) {
        this.consecutiveMissedMonths = consecutiveMissedMonths;
    }

    public int getFraudFlag() {
        return fraudFlag;
    }

    public void setFraudFlag(int fraudFlag) {
        this.fraudFlag = fraudFlag;
    }

    public List<Integer> getCustomerIDList() {
        return customerIDList;
    }

    public void setCustomerIDList(List<Integer> customerList) {
        this.customerIDList = customerList;
    }
}
