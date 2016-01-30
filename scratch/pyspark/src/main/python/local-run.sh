#!/bin/bash

free && sync && echo 3 > /proc/sys/vm/drop_caches && free

time ~/spark/bin/spark-submit --master local[8] kira.py /mnt/data/input_18_18 output_18_18 > log-1 2>&1
sleep 10
rm -r output_18_18
sleep 10

free && sync && echo 3 > /proc/sys/vm/drop_caches && free

time ~/spark/bin/spark-submit --master local[8] kira.py /mnt/data/input_18_18 output_18_18 > log-2 2>&1
sleep 10
rm -r output_18_18
sleep 10

free && sync && echo 3 > /proc/sys/vm/drop_caches && free

time ~/spark/bin/spark-submit --master local[8] kira.py /mnt/data/input_18_18 output_18_18 > log-3 2>&1
sleep 10
rm -r output_18_18
sleep 10

