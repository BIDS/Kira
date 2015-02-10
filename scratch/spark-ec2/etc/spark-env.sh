#!/usr/bin/env bash

export SPARK_LOCAL_DIRS="/mnt/spark"

# Standalone cluster options
export SPARK_MASTER_OPTS=""
export SPARK_WORKER_INSTANCES=4
export SPARK_WORKER_CORES=1

export HADOOP_HOME="/root/ephemeral-hdfs"
export SPARK_MASTER_IP=ec2-54-153-51-7.us-west-1.compute.amazonaws.com
export MASTER=`cat /root/spark-ec2/cluster-url`

export SPARK_SUBMIT_LIBRARY_PATH="$SPARK_SUBMIT_LIBRARY_PATH:/root/ephemeral-hdfs/lib/native/"
export SPARK_SUBMIT_CLASSPATH="$SPARK_CLASSPATH:$SPARK_SUBMIT_CLASSPATH:/root/ephemeral-hdfs/conf"

# Bind Spark's web UIs to this machine's public EC2 hostname:
export SPARK_PUBLIC_DNS=`wget -q -O - http://169.254.169.254/latest/meta-data/public-hostname`

# Set a high ulimit for large shuffles
ulimit -n 1000000

#Set LD_LIBRARY_PATH
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/root/Kira/scratch/spark-ec2/libs
