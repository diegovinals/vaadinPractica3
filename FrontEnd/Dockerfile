FROM maven:3.8.6-jdk-11-slim
RUN mkdir /app
WORKDIR /app
COPY pom.xml .
EXPOSE 8181
ADD ./src/ ./src
CMD ["mvn","jetty:run"]
