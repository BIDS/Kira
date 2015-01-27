This directory is for experimental code for JNI.

1. Compile Background.java
   javac -cp libs/jfits-0.94.jar Fits.java Background.java

2. Compile JNI interface
   gcc Background.c -o libBackgroundImpl.jnilib -lc -shared -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include" -I"/Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/include/darwin" -L"/Users/zhaozhang/projects/scratch/java/test/libs" -lsep -I"/Users/zhaozhang/projects/scratch/java/test/libs/include"

3. Run the test
   java -cp .:libs/jfits-0.94.jar Background

