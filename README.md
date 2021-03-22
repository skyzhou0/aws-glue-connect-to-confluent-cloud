# aws-glue-connect-to-confluent-cloud
How to build AWS Glue ETL Job and Connect to Confluent Cloud

## Contents
- How to run a Python Kafka Streaming application in AWS Glue ETL that produce message to Confluent Cloud?
- How to run a Spark Structured Streaming application in AWS Glue ETL that produce message to Confluent Cloud?

Note that the instrucutions assumes that the Confluent Cloud cluster is reachable via public internet.

## Python Kafka Streaming application in AWS Glue ETL
1. Creating VPC and Elastic IP
    - Create an Elastic IP address. The NAT-Gateway will use this IP. 
    - Create a VPC with a private and a public subnet, i.e. vpc-xxx
    - During the VPC creation, in the selection of the Elastic IP allocation ID, set it to be the one associated with the Elastic IP, i.e. eipalloc-xxxx. 
    - Note that we would like the private subnet instances to establish an outbound connection to the internet via a public subset using NAT. It implies that placing AWS Glue ETL in the private subnet will be secure; most importantly, it will able to reach external APIs and Confluent Cloud cluster.

2. Create an IAM role that AWS Glue ETL can assume.

3. Create an S3 bucket to store the Python Kafka Streaming application script for the AWS Glue job.

4. Create AWS Glue Network Connection.
    - Add a connection: providing the Connection name, Description and Connection type. Make sure you select "Network" as the connection type.
    Use the VPC created before - vpc-xxx and select the private subnet within the selected VPC. Make use of the default security group 

5. To test the connection.
    - Make use of the IAM role and S3 bucket created previously. 

6. Create Python Kafka Streaming Application in AWS Glue ETL.
    - Create a python Kafka streaming application. The application will be used in AWS Glue ETL. Please refer "main.py" for details. 
    - In the application, we are using the "confluent-kafka" API created, maintained and supported by Confluent Inc. To do so, we specified the following script to install and load the library during Glue Job ETL run-time
    - ```python
        import os
        import site
        from setuptools.command import easy_install
        install_path = os.environ['GLUE_INSTALLATION']
        easy_install.main( ["--install-dir", install_path, "https://files.pythonhosted.org/packages/ce/db/e55f6cf13251880434ca74cc77da70fc4f9336875f88556ce7de39cf9eab/confluent_kafka-1.6.0-cp36-cp36m-manylinux2010_x86_64.whl"] )
        reload(site)
        ```
    - Provide API Key and Secret so that the application authentication.
    - Create AWS Glue ETL Job: providing the application scripts and using the Network Connection created. 
    Run the job and check in the Confluent Center (Confluent Cloud Dashboard) to ensure that 500 messages have been published to the 'confluent-cloud-kafka-topic'.


