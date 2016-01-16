Execution Instructions:
============
# Install pip
```
yum -y install python-pip python-wheel
```

# Install SEP
```
pip install sep
```

# Install astropy
```
pip install astropy
```

# Running on a single machine
```
~/projects/spark/spark-1.4.0-fits/bin/spark-submit --master local[4] kira.py input output
```

# Running on a cluster
```
Download sep, modify sep.pyx, do not assert sum_ellipse, compile it.
for h in `cat ~/spark/conf/slaves`; do   ssh $h "yum -y install Cython"; done
for h in `cat ~/spark/conf/slaves`; do   ssh $h "cd /mnt/sep; ./setup.py install"; done
```

```
for h in `cat ~/spark/conf/slaves`; do   ssh $h "yum -y install python-pip python-wheel"; done
```

```
for h in `cat ~/spark/conf/slaves`; do   ssh $h "pip install astropy"; done
for h in `cat ~/spark/conf/slaves`; do   ssh $h "pip install --upgrade numpy"; done
```

```
~/ephemeral-hdfs/bin/hadoop distcp s3n://$AWS_ACCESS_KEY_ID:$AWS_SECRET_ACCESS_KEY@sdss-dataset/input_18_18 /user/root/
```

```
~/projects/spark/spark-1.4.0-fits/bin/spark-submit kira.py input output
```

```
for h in `cat ~/spark/conf/slaves`; do   ssh $h "free && sync && echo 3 > /proc/sys/vm/drop_caches && free"; done
```
