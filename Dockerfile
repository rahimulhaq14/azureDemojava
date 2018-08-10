FROM maven:3.5.4-jdk-10-slim
RUN apt-get update && \
    apt-get install -y git && \
    apt-get install -y npm && \
    npm install -g n && \
    n 8.9.0 
RUN git clone -b for-demo https://github.com/rahimulhaq14/azureDemojava.git /var/azure/
WORKDIR  /var/azure
RUN mvn install
RUN mvn dependency:resolve-plugins
RUN mvn dependency:go-offline

WORKDIR  /