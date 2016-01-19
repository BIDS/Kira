#Compilation instructions on EC2


## Compile the SEP library, this step can be skipped now

2.1 scons

2.2 cp src/libsep.so* /root/Kira/scratch/spark/libs/

## Compile the JNI library, this step can be skipped now

gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib/jvm/java-1.7.0/include" -I"/u
sr/lib/jvm/java-1.7.0/include/linux" -L"/root/Kira/scratch/spark-ec2/libs" -lsep -I"/root/Kira/scratch/spark-ec2/libs/include" -fPIC

## Compile Kira

mvn clean compile assembly:single

## Update spark-env.sh

5.1 edit spark-env.sh to launch mutliple JVMs each with one worker

    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/root/Kira/scratch/spark-ec2/libs

5.2 edit spark-default.conf to change the memory for each worker

    spark.executor.memory   6144m

    spark.executor.extraClassPath	/root/ephemeral-hdfs/conf:/root/Kira/scratch/spark-ec2/target/appassembler/repo/org/esa/fits/0.94/fits-0.94.jar

    spark.default.parallelism 1
    
    spark.locality.wait 0

5.2 /root/spark/sbin/stop-all.sh

5.3 /root/spark/sbin/start-all.sh

## Run Kira

~/projects/spark/spark-1.6.0/bin/spark-submit --class Kira --master local[2] target/Kira-0.0.1-SNAPSHOT-jar-with-dependencies.jar ~/projects/SDSS/data output