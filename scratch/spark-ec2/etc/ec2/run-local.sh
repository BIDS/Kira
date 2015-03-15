#!/bin/bash

cd /mnt/gluster/sep/data
inlist=$1
indir=$2
outdir=$3
#inlist=tmp/x0000
#outdir=$SCRATCH/output/catalog_8_8

proc=8

i=0

for f in `cat $inlist`
do
  base=`basename $f`
  #strace -T -o /dev/shm/${base}.strace /scratch1/scratchdirs/zhaozhan/sextractor-2.19.5/bin/sex $indir/$f -CATALOG_NAME $outdir/${base}.cat &
  /mnt/gluster/sextractor-2.19.5/bin/sex $indir/$f -CATALOG_NAME $outdir/${base}.cat -CHECKIMAGE_TYPE NONE &
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
date=`date +%s.%2N`
echo "finish time $date" >> log
#sleep 1500

