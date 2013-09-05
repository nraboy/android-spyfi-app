#!/bin/bash

echo "Starting Script"

if [ -d "../Spyfi/bin" ];
then
    echo "Deleting Bin Directory!"
    rm -Rf "../Spyfi/bin"
fi

if [ -d "../Spyfi/gen" ];
then
    echo "Deleting gen Directory!"
    rm -Rf "../Spyfi/gen"
fi

cd "../Spyfi"

echo "Compiling Project"

ant debug

echo "Script Finished"
