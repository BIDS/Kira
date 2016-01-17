#!/bin/bash

cd /mnt

yum -y install Cython python-pip python-wheel 

pip install astropy

git clone https://github.com/zhaozhang/sep.git
cd sep
git checkout v0.5.3-no-assert
./setup.py install
cd ..

~/spark-ec2/copy-dir /mnt/sep
~/spark-ec2/copy-dir /mnt/Kira

for h in `cat ~/spark/conf/slaves`; do   ssh $h "yum -y install Cython"; done

for h in `cat ~/spark/conf/slaves`; do   ssh $h "cd /mnt/sep; ./setup.py install"; done

for h in `cat ~/spark/conf/slaves`; do   ssh $h "yum -y install python-pip python-wheel"; done

for h in `cat ~/spark/conf/slaves`; do   ssh $h "pip install astropy"; done