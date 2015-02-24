#!/bin/bash

proc=$1

i=0
pids=""
for f in `ls /mnt/input_test`
do
  sex /mnt/input_test/$f -CATALOG_NAME catalog_18_18/$f.cat &
  pids="$pids $!"
  i=$((i+1))
  if [ "${i}" -eq "${proc}" ];
  then
      wait $pids
      pids=""
      i=0
  fi
done