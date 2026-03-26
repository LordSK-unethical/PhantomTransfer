FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN mkdir -p rooms

COPY server/ /app/server/

RUN javac server/*.java

EXPOSE 5000

CMD ["java", "-cp", ".", "server.ServerMain"]
