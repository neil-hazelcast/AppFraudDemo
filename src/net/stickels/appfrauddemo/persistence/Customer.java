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

public class Customer implements Serializable {

    public int customerID;
    private CustomerKey customerKey;
    private String name;
    private String ssn;
    private Date dob;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String email;
    private String phoneNumber;
    private int lengthRelationship;
    private int numAccounts;
    private Double income;
    private String jobTitle;
    private String employer;

    public int getCustomerID() {
        return customerID;
    }

    public int getCustomerId() {return customerID; }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomerId(int customerId) {this.customerID = customerId; }

    public CustomerKey getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(CustomerKey custKey) {
        this.customerKey = custKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getLengthRelationship() {
        return lengthRelationship;
    }

    public void setLengthRelationship(int lengthRelationship) {
        this.lengthRelationship = lengthRelationship;
    }

    public int getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(int numAccounts) {
        this.numAccounts = numAccounts;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }
}
