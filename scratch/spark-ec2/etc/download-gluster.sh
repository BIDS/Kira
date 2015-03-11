#!/bin/bash

cd ~
#download the glusterfs tar ball
wget http://download.gluster.org/pub/gluster/glusterfs/LATEST/glusterfs-3.6.2.tar.gz

#untar glusterfs
tar -zxf glusterfs-3.6.2.tar.gz

#install required packages on master
yum -y install automake autoconf libtool flex bison openssl-devel libxml2-devel python-devel libaio-devel libibverbs-devel librdmacm-devel readline-devel lvm2-devel glib2-devel

#install required packages on all nodes
for h in `cat ~/spark/conf/slaves`
do
  ssh $h "yum -y install automake autoconf libtool flex bison openssl-devel libxml2-devel python-devel libaio-devel libibverbs-devel librdmacm-devel readline-devel lvm2-devel glib2-devel"
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
  ssh $h "cd glusterfs-3.6.2;make install"
done
