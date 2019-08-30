#!/usr/bin/env bash

#build main dependencies osgi bundle
#clean mockrest-osgi-dependencies bundle and build again.
cd ./mockrest-osgi/mockrest-osgi-dependencies/
#clean install..
mvn clean install

#proceed to main oegi runtime module
cd ../
cd ./mockrest-osgi-runtime
#clean everything from previous build.
mvn clean
#copy specified bundle jar files in classpath
mvn dependency:copy
#package everything in to a runnable
mvn package