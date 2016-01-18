#!/bin/bash

log=$1

grep "Starting task" log-1 | awk '{print $15}' | awk -F, '{printf "%.1f %s\n", $1, $2}' | sort -k 1 > taskid.txt

grep "Finished task" $log | awk '{printf "%.1f %d\n", $7, $14}' | sort -k 1 > tasktime.txt

join taskid.txt tasktime.txt > id-time.txt

awk '{if($3 > 2000) a=a+1} END {print (11150-a)/11150}' id-time.txt 
