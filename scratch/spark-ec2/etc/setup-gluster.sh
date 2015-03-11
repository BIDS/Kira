#!/bin/bash

cd ~
#download the glusterfs tar ball
wget http://download.gluster.org/pub/gluster/glusterfs/LATEST/glusterfs-3.6.2.tar.gz

#untar glusterfs
tar -xf glusterfs-3.6.2

#install required packages on master
yum install automake autoconf libtool flex bison openssl-devel libxml2-devel python-devel libaio-devel libibverbs-devel librdmacm-devel readline-devel lvm2-devel glib2-devel

#install required packages on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h (yum install automake autoconf libtool flex bison openssl-devel libxml2-devel python-devel libaio-devel libibverbs-devel librdmacm-devel readline-devel lvm2-devel glib2-devel)
done

#compile glusterfs on master
cd glusterfs-3.6.2
./configure
make
make install
cd ..

#copy glusterfs-3.6.2 to all nodes
~/spark-ec2/copy-dir glusterfs-3.6.2

#install glustefs on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h (cd glusterfs-3.6.2;make install)
done

#start glusterfs daemon on local node
glusterd

#start glusterfs daemon on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h (glusterd)
done

#mount partition as a glusterfs brick on local node
mkdir -p /srv/sdb1
mount /dev/xvdf /srv/sdb1
mkdir -p /srv/sdb1/brick
echo "/dev/xvdf /srv/sdb1 xfs defaults 0 0" | sudo tee -a /etc/fstab

#mount partition as a glusterfs brick on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h "mkdir -p /srv/sdb1;mount /dev/xvdf /srv/sdb1;mkdir -p /srv/sdb1/brick;echo '/dev/xvdf /srv/sdb1 xfs defaults 0 0' | tee -a /etc/fstab"
done

#probe all nodes
for h in `cat ~/spark/conf/slaves`
do
  gluster peer probe $h
done

#configure glusterfs volume
cmd="gluster volume create testvol replica 2"
for h in `cat ~/spark/conf/slaves`
do
  cmd=$cmd $h:/srv/sdb1/brick
done
echo $cmd
`$cmd`

#start glusterfs volume
gluster volume start testvol

mkdir /mnt/gluster
target=`head -n 1 ~/spark/conf/slaves`
mount -t glusterfs $target:/testvol /mnt/gluster
