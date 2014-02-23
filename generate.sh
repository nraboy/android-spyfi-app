#!/bin/bash

echo "Starting Script"

if [ -d "bin" ];
then
    echo "Deleting Bin Directory!"
    rm -Rf "bin"
fi

if [ -d "gen" ];
then
    echo "Deleting gen Directory!"
    rm -Rf "gen"
fi

echo "Compiling Project"

ant debug

echo "Installing Project"

adb install -r bin/Spyfi-debug.apk

echo "Script Finished"
