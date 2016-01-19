On EC2
gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib/jvm/java-1.7.0/include" -I"/usr/lib/jvm/java-1.7.0/include/linux" -L"/root/Kira/scratch/spark-ec2/libs" -lsep -I"/root/Kira/scratch/spark-ec2/libs/include" -fPIC

On NERSC Edison
gcc Background.c Extractor.c -std=c99 -o libBackgroundImpl.so -lc -shared -I"/usr/lib64/jvm/java-1.7.0-ibm/include" -I"/usr/lib64/jvm/java-1.7.0-ibm/include/linux" -L"$SCRATCH/Kira/scratch/spark-ec2/libs" -lsep -I"$SCRATCH/Kira/scratch/spark-ec2/libs/include" -fPIC

On Mac
gcc Background.c Extractor.c -o libBackgroundImpl.jnilib -lc -shared -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include" -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include/darwin" -L"/Users/zhaozhang/projects/scratch/Kira/libs" -lsep -I"/Users/zhaozhang/projects/scratch/Kira/libs/include"