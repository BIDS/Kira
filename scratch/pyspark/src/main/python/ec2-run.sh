#!/bin/bash

for h in `cat ~/spark/conf/slaves`
do
  ssh $h "free && sync && echo 3 > /proc/sys/vm/drop_caches && free"
done

time ~/spark/bin/spark-submit kira.py input_stream_65GB/1 output_stream_65GB > log-1 2>&1
sleep 10
~/ephemeral-hdfs/bin/hdfs dfs -rm -r /user/root/output_stream_65GB
sleep 10

for h in `cat ~/spark/conf/slaves`
do
  ssh $h "free && sync && echo 3 > /proc/sys/vm/drop_caches && free"
done

time ~/spark/bin/spark-submit kira.py input_stream_65GB/1 output_stream_65GB > log-2 2>&1
sleep 10
~/ephemeral-hdfs/bin/hdfs dfs -rm -r /user/root/output_stream_65GB
sleep 10

for h in `cat ~/spark/conf/slaves`
do
  ssh $h "free && sync && echo 3 > /proc/sys/vm/drop_caches && free"
done

time ~/spark/bin/spark-submit kira.py input_stream_65GB/1 output_stream_65GB > log-3 2>&1
sleep 10
~/ephemeral-hdfs/bin/hdfs dfs -rm -r /user/root/output_stream_65GB
sleep 10
