FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://github.com/ekzhang/bore/releases/download/v0.5.0/bore-v0.5.0-x86_64-unknown-linux-musl.tar.gz -o bore.tar.gz && \
    tar -xzf bore.tar.gz && \
    mv bore /usr/local/bin/ && \
    rm bore.tar.gz && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p rooms

COPY server/ /app/server/

RUN javac server/*.java

EXPOSE 5000

CMD java -cp . server.ServerMain & \
    echo "Starting bore tunnel..." && \
    bore local 5000 --to bore.pub
