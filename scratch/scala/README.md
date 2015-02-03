Compile instructions:
1. Compile the library:
gcc Background.c Extractor.c -o libBackgroundImpl.jnilib -lc -shared -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include" -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include/darwin" -L"/Users/zhaozhang/projects/scratch/java/test/libs" -lsep -I"/Users/zhaozhang/projects/scratch/java/test/libs/include"

2. Compile the Source Extractor
scalac *.scala

3. To execute
scala test
