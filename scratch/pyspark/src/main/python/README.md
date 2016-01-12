Execution Instructions:
============

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
~/projects/spark/spark-1.4.0-fits/bin/spark-submit kira.py input output
```