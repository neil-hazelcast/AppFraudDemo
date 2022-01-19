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

import com.hazelcast.partition.PartitionAware;

import java.io.Serializable;

public class CustomerKey implements PartitionAware, Serializable
{
    private final int customerId;
    private final int accountNum;

    public CustomerKey(int customerId, int accountNum)
    {
        this.customerId = customerId;
        this.accountNum = accountNum;
    }

    @Override
    public Object getPartitionKey()
    {
        return accountNum;
    }

    public int getCustomerId()
    {
        return customerId;
    }

    public int getAccountNum()
    {
        return accountNum;
    }
}
