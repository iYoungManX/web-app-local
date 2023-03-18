#!/bin/bash
# Update the system
sudo yum update -y

sudo yum install expect -y

# Install Java 17 JDK
sudo amazon-linux-extras enable corretto8
sudo yum install -y java-17-amazon-corretto-devel

# Install MySQL
sudo yum install -y mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb

## install cloudwatch agent
sudo yum install amazon-cloudwatch-agent -y

##
sudo mkdir /opt/deployment
sudo chown -R $USER:$USER /opt/deployment


