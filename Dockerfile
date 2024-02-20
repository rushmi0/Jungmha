FROM ubuntu:latest

RUN apt-get update \
    && apt-get install -y wget git bash\
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN wget  https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_linux-x64_bin.tar.gz \
    && tar -xvf graalvm-jdk-17_linux-x64_bin.tar.gz \
    && mv graalvm-jdk-17.0.10+11.1 /opt/graalvm \
    && rm graalvm-jdk-17_linux-x64_bin.tar.gz

ENV JAVA_HOME=/opt/graalvm
ENV PATH=$JAVA_HOME/bin:$PATH

RUN java -version

EXPOSE 8080

WORKDIR /app/source-code
COPY . /app/source-code

#RUN chmod +x gradlew && \
#    ./gradlew nativeOptimizedCompile; \
#    cd build/native/nativeOptimizedCompile; ./jungmha

