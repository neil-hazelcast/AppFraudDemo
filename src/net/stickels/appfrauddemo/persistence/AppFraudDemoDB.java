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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppFraudDemoDB
{
    private final static ILogger log = Logger.getLogger(AppFraudDemoDB.class);

    private Connection conn;
    public static final String JDBC_DRIVER_CLASS="com.mysql.cj.jdbc.Driver";

    public static final String JDBC_PROTOCOL="mysql";
    public static final String JDBC_DB_NAME="app_fraud_demo";
    public static final String JDBC_HOST="127.0.0.1";     //when running bare metal (laptop)
    //public static final String JDBC_HOST="";    // AWS db

    public static final String JDBC_PORT="3306";
    // Need username and password here that can be used BEFORE we create the db-specific user
    public static final String JDBC_USER="hazelcast";
    public static final String JDBC_PASS="H@zelcast";

    protected synchronized Connection establishConnection()  {
        try {
            // Register the driver, we don't need to actually assign the class to anything
            Class.forName(JDBC_DRIVER_CLASS);
            String jdbcURL = "jdbc:" + JDBC_PROTOCOL + "://" + JDBC_HOST + ":" + JDBC_PORT + "/" +JDBC_DB_NAME;
            //System.out.println("JDBC URL is " + jdbcURL);
            conn = DriverManager.getConnection(
                    jdbcURL, JDBC_USER, JDBC_PASS);
            log.info("Established connection to MySQL/MariaDB server");
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }


}
