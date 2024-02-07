FROM ubuntu:latest

RUN apt-get update \
    && apt-get install -y wget git\
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://download.oracle.com/graalvm/21/latest/graalvm-jdk-21_linux-x64_bin.tar.gz \
    && tar -xvf graalvm-jdk-21_linux-x64_bin.tar.gz \
    && mv graalvm-jdk-21.0.2+13.1 /opt/graalvm \
    && rm graalvm-jdk-21_linux-x64_bin.tar.gz

RUN apt-get update \
    && apt-get install -y bash \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/graalvm
ENV PATH=$JAVA_HOME/bin:$PATH


#WORKDIR /app/source-code

#COPY . /app/source-code

#RUN chmod +x gradlew && \
#    ./gradlew nativeOptimizedCompile; \
#    cd build/native/nativeOptimizedCompile; ./jungmha

#EXPOSE 8080