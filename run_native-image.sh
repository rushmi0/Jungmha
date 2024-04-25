#!/bin/bash

if [ ! -f "build/native/nativeOptimizedCompile/jungmha-0.0.1-alpha" ]; then
    ./gradlew nativeOptimizedCompile

    cd build/native/nativeOptimizedCompile || exit
else
    cd build/native/nativeOptimizedCompile || exit
fi

./jungmha-0.0.1-alpha
