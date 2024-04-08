#!/bin/bash

# ตรวจสอบว่ามี dir ชื่อ build/docker/optimized/layers หรือไม่
if [ ! -d "build/docker/optimized/layers" ]; then
    # ถ้าไม่มีให้รันคำสั่ง ./gradlew assemble
    ./gradlew assemble
fi

# ให้รันคำสั่ง java -jar build/docker/optimized/layers/application.jar
java -jar build/docker/optimized/layers/application.jar
