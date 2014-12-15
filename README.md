Kira
============

# Building

We use maven to build. You can build with:

```
mvn clean package
```

We require a local jar to be installed the first time you build. We provide this jar in the repository. Install this jar with:

```
mvn install:install-file -Dfile=lib/jfits-0.94.jar -DgroupId="org.esa" -DartifactId="fits" -Dpackaging="jar" -Dversion="0.94"
```

# Spark Fits2Kira

The spark version has a provided submit script. To run, do:

```
bin/kira-submit
```

You will need to have a copy of the Spark binaries. Set `$SPARK_HOME` to the path to the binaries.