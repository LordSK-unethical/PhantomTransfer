FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    gnupg \
    && curl -fsSL https://tailscale.com/install.sh | sh \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app/rooms

COPY server/ /app/server/

RUN javac server/*.java

EXPOSE 5000

ENTRYPOINT ["java", "-cp", ".", "server.ServerMain"]
