#!/usr/bin/env bash

pid_file="mockrest/PID"
if test -f "$pid_file";then
    pid=$(cat mockrest/PID)
    kill -15 $pid
    kill -9 $pid

    rm -f "$pid_file"
else
    echo ""
    echo "Mockrest not running"
    echo ""
fi