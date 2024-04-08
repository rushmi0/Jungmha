#!/bin/bash

# ตรวจสอบว่ามี dir ชื่อ build/native/nativeOptimizedCompile หรือไม่
if [ ! -d "build/native/nativeOptimizedCompile" ]; then
    # ถ้าไม่มีให้รันคำสั่ง ./gradlew nativeOptimizedCompile
    ./gradlew nativeOptimizedCompile

    # เมื่อเสร็จสิ้นให้ cd เข้าไปใน build/native/nativeOptimizedCompile
    cd build/native/nativeOptimizedCompile || exit
else
    # ถ้ามีอยู่แล้วให้เข้าไปใน build/native/nativeOptimizedCompile
    cd build/native/nativeOptimizedCompile || exit
fi

# หลังจากเข้าไปใน build/native/nativeOptimizedCompile ให้รัน ./jungmha-0.0.1-alpha
./jungmha-0.0.1-alpha
