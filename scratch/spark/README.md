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
Install jfits-0.94 jar
mvn install:install-file -Dfile=libs/jfits-0.94.jar -DgroupId="org.esa" -DartifactId="fits" -Dpackaging="jar" -Dversion="0.94"

Compile the SEP library
1. scons
2. cp src/libsep.so* /root/Kira/scratch/spark/libs/

Compile the JNI library
gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib/jvm/java-1.7.0/include" -I"/usr/lib/jvm/java-1.7.0/include/linux" -L"/root/Kira/scratch/spark/libs" -lsep -I"/root/Kira/scratch/spark/libs/include" -fPIC

Compile Kira
mvn clean package

Run Kira
bin/kira-submit