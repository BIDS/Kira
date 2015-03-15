#!/bin/bash

indir=$1
outdir=$2
workdir="tmp"
mkdir -p $workdir
#ccm_file=`sort -u $PBS_NODEFILE`
#num_nodes=`sort -u $PBS_NODEFILE|wc -l`

date=`date +%s.%2N`
echo "start time $date" >> log

ccm_file=`cat ~/spark/conf/slaves`
num_nodes=`sort -u ~/spark/conf/slaves | wc -l`
#num_nodes=4
ls -l $indir | tail -n +2 | awk '{print $9}' > $workdir/file.list
num_files=`wc -l $workdir/file.list| awk '{print $1}'`
#echo $num_files
num_lines=$(($num_files/$num_nodes+1))

cd $workdir
split file.list -l $num_lines -d -a 4
cd ..

ls $workdir/x* > $workdir/part.list
rm $workdir/nodes
for h in $ccm_file 
do
  echo $h >> $workdir/nodes
done
#cp nodes $workdir/nodes

date=`date +%s.%2N`
echo "serial part finishes at $date" >> log
 
paste $workdir/nodes $workdir/part.list | while read node file
do
  #echo "ssh $node $SCRATCH/sep/data/run-local.sh $file $indir $outdir"
  (ssh -n $node /mnt/gluster/sep/data/run-local.sh /mnt/gluster/sep/data/$file $indir $outdir &)
done
#wait

#sleep 1800
#rm -rf $outdir/*
#date=`date +%s.%2N`
#echo "start time $date" >> log
#paste $workdir/nodes $workdir/part.list | while read node file
#do
#  #echo "ssh $node $SCRATCH/sep/data/run-local.sh $file $indir $outdir"
#  (ssh -n $node $SCRATCH/sep/data/run-local.sh $SCRATCH/sep/data/$file $indir $outdir &)
#done
#sleep 1800
