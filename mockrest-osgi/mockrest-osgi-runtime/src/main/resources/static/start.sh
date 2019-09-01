#!/usr/bin/env bash

mr_version="1.0-SNAPSHOT";
jvm_version="$(java -version 2>&1 | head -n 1)"

echo "MockRest version: $mr_version"
echo "Using JVM: $jvm_version"

echo "_______  _____  _______ _     _  ______ _______ _______ _______"
echo "|  |  | |     | |       |____/  |_____/ |______ |______    |"
echo "|  |  | |_____| |_____  |    \_ |    \_ |______ ______|    |  "
