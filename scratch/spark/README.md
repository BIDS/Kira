1. Install jftis-0.94.jar
mvn install:install-file -Dfile=libs/jfits-0.94.jar -DgroupId="org.esa" -DartifactId="fits" -Dpackaging="jar" -Dversion="0.94"

2. Build Kira
mvn clean package

3. Run Kira
bin/kira-submit

4. Expected Output:
Spark: 272 objects were detected
