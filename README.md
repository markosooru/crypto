# Crypto portfolio API

This is a simple API that allows you to manage your crypto portfolio. Using Bitfinex API you can get the current price of your crypto assets.

## Run

Run from the root directory:
```bash
docker-compose up
./mvnw spring-boot:run
```
Application will be available at `http://localhost:8080/api/`

## Info

Seeded with 3 users:
```bash
admin@crypto.com      appPass
user@crypto.com       appPass
user2@crypto.com      appPass
```

From `config/DataInitializer.java`

## Swagger

Swagger is available at `http://localhost:8080/swagger-ui.html`