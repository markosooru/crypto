# Crypto Portfolio API

This API allows you to manage your crypto portfolio and fetch the current prices of your crypto assets using the Bitfinex API.

## Getting Started

### Prerequisites

- Docker
- Java 21 or later

### Running the Application

1. Start the DB using Docker:
   ```bash
   docker-compose up
   ```
2. Build the application:
    ```bash
    ./mvnw clean install
    ```
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080/api/`.

## API Documentation

You can access the API documentation using Swagger UI at the following URL:

[Swagger UI](http://localhost:8080/swagger-ui/index.html)

## Seed Data

The application comes with seeded data for initial testing. Here are the pre-configured users:

| Email              | Password |
|--------------------|----------|
| admin@crypto.com   | appPass  |
| user@crypto.com    | appPass  |
| user2@crypto.com   | appPass  |

Seed data is defined in `DataInitializer` class.

## Features

- **Manage Crypto Portfolio**: Add, update, and delete your crypto assets.
- **Fetch Current Prices**: Retrieve the latest prices of your crypto assets using the Bitfinex API.

## Security Configuration

The application uses JWT for securing API endpoints. Refer to the `SecurityConfig` class for more details on the security setup.