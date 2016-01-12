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
for h in `cat ~/spark/conf/slaves`; do   ssh $h "yum -y install python-pip python-wheel"; done
```

```
for h in `cat ~/spark/conf/slaves`; do   ssh $h "pip install sep astropy"; done
```

```
~/projects/spark/spark-1.4.0-fits/bin/spark-submit kira.py input output
```