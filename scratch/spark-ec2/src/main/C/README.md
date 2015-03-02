On EC2
gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib/jvm/java-1.7.0/include" -I"/usr/lib/jvm/java-1.7.0/include/linux" -L"/root/Kira/scratch/spark-ec2/libs" -lsep -I"/root/Kira/scratch/spark-ec2/libs/include" -fPIC

On NERSC Edison
gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib64/jvm/java-1.7.0-ibm/include" -I"/usr/lib64/jvm/java-1.7.0-ibm/include/linux" -L"$SCRATCH/Kira/scratch/spark-ec2/libs" -lsep -I"$SCRATCH/Kira/scratch/spark-ec2/libs/include" -fPIC
