#!/bin/bash

proc=$1

i=0
pids=""
for f in `ls /mnt/data/input_8_8`
do
  ~/sep/ctest/test_image /mnt/data/input_8_8/$f /mnt/output/catalog_8_8/$f.cat &
  pids="$pids $!"
  i=$((i+1))
  if [ "${i}" -eq "${proc}" ];
  then
      wait $pids
      pids=""
      i=0
  fi
done
wait