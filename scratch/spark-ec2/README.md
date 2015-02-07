Compilation instructions on MAC

1. Install jfits-0.94.jar

mvn install:install-file -Dfile=libs/jfits-0.94.jar -DgroupId="org.esa" -DartifactId="fits" -Dpackaging="jar" -Dversion="0.94"

2. Build Kira

mvn clean package

3. Run Kira

bin/kira-submit

4. Expected Output:

Spark: 272 objects were detected


Compilation instructions on EC2

1. Install jfits-0.94 jar

mvn install:install-file -Dfile=libs/jfits-0.94.jar -DgroupId="org.esa" -DartifactId="fits" -Dpackaging="jar" -Dversion="0.94"

2. Compile the SEP library, this step can be skipped now

2.1 scons

2.2 cp src/libsep.so* /root/Kira/scratch/spark/libs/

3. Compile the JNI library, this step can be skipped now

gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib/jvm/java-1.7.0/include" -I"/u
sr/lib/jvm/java-1.7.0/include/linux" -L"/root/Kira/scratch/spark-ec2/libs" -lsep -I"/root/Kira/scratch/spark-ec2/libs/include" -fPIC

4. Compile Kira

mvn clean package

5. Update spark-env.sh

5.1 cp /root/Kira/scratch/spark-ec2/etc/spark-env.sh /spark/conf

5.2 /root/spark/sbin/stop-all.sh

5.3 /root/spark/sbin/start-all.sh

6. Run Kira

bin/kira-submit