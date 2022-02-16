# AppFraudDemo

This is a demo that I put together with my own sample database (in MySQL) loading data into my Hazelcast cluster

The data from these underlying tables are being stored in IMaps within Hazelcast, each named for the underlying table they are coming from.
The data is stored as POJOs in my case, but it could just as easily be JSON documents.

All of thte POJOs are part of the net.stickels.appfrauddemo.persistence package.  This package also contains my MapStore implementation for each table, 
which will load the data from the underlying table and push any changes made to the Hazelcast maps to the underlying database.

In my demo that I have, I have a master/detail relationship between tables, specifically the Applicants table contains details to the Applications table
and the Customer table contains details to the Account table.  These master/detail relationships can be defined in your POJOs as well to make sure that 
detail records are stored in the same Hazelcast node as the master record they are associated with.

This is done via the ApplicantKey and CustomerKey classes.  To do this, they need to implement the PartitionAware interface, and to use those as the
keys for those respective records in the Hazelcast IMap.  You can see this in the code that I have available here.
