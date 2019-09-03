#!/usr/bin/env bash

mr_version="1.0-SNAPSHOT";
jvm_version="$(java -version 2>&1 | head -n 1)"
mr_dir="mockrest"

echo "MockRest version: $mr_version"
echo "Using JVM: $jvm_version"
echo ""
echo "_______  _____  _______ _     _  ______ _______ _______ _______"
echo "|  |  | |     | |       |____/  |_____/ |______ |______    |"
echo "|  |  | |_____| |_____  |    \_ |    \_ |______ ______|    |  "

echo ""
confFile="$mr_dir"/conf/conf.properties
executorFileName=$(cat "$confFile" | grep mr_executor | cut -d'=' -f2)
nohup java -jar "$executorFileName" > server.out &
echo "Started MockRest Successfully..."
exit 0