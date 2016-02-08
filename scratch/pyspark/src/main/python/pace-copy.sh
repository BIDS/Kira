#!/bin/bash
#~/ephemeral-hdfs/bin/hdfs dfs -ls /user/root/input_1TB | awk '{print $NF}' | tail -n +2 > filelist.txt

i=0
stamp1=`date +%s.%N`
files=""
while read -r line || [[ -n "$line" ]]; do
   if [ "$i" -lt 1000 ]; then
       files=$files" "$line
       i=$(($i+1))
   else
       ~/ephemeral-hdfs/bin/hdfs dfs -cp $files /user/root/input_streaming
       stamp2=`date +%s.%N`
       diff=`echo $stamp2 $stamp1 | awk '{print $1-$2}'`
       echo "copying 50 files take $diff seconds"
       sleep 30

       i=0
       files=""
       stamp1=`date +%s.%N`
   fi
done < filelist.txt
