#!/bin/bash

cd ~

#start glusterfs daemon on local node
glusterd

#start glusterfs daemon on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h glusterd
done

sleep 5
#mount partition as a glusterfs brick on local node
mkdir -p /srv/sdb1/brick
mount /dev/xvdb /srv/sdb1
mkdir -p /srv/sdb1/brick
echo "/dev/xvdb /srv/sdb1 xfs defaults 0 0" | sudo tee -a /etc/fstab

#mount partition as a glusterfs brick on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h "mkdir -p /srv/sdb1;mount /dev/xvdb /srv/sdb1;mkdir -p /srv/sdb1/brick;echo '/dev/xvdb /srv/sdb1 xfs defaults 0 0' | tee -a /etc/fstab"
done

sleep 5
#probe all nodes
for h in `cat ~/spark/conf/slaves`
do
  gluster peer probe $h
done

#configure glusterfs volume
#cmd="gluster volume create testvol replica 2 "
cmd="gluster volume create testvol "
for h in `cat ~/spark/conf/slaves`
do
  cmd="$cmd $h:/srv/sdb1/brick"
done
echo $cmd

$cmd

sleep 5
#start glusterfs volume
gluster volume start testvol

mkdir /mnt/gluster
target=`grep SPARK_MASTER_IP ~/spark/conf/spark-env.sh | cut -d '=' -f 2`
mount -t glusterfs $target:/testvol /mnt/gluster

for h in `cat ~/spark/conf/slaves`
do
  ssh $h "mkdir /mnt/gluster; target=`grep SPARK_MASTER_IP ~/spark/conf/spark-env.sh | cut -d '=' -f 2`; mount -t glusterfs $target:/testvol /mnt/gluster"
done
