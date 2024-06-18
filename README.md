# Crypto portfolio API

This is a simple API that allows you to manage your crypto portfolio. You can add, remove, update and list your assets.

Using Maven, JPA and Postgres.

## Use

Run compose.yaml

Run CryptoApplication.java

Application will be available at `http://localhost:8080`

## AppUser endpoints

#### GET /api/users

- Returns a list of all users

#### GET /api/users/{id}

- Returns a user by id

#### POST /api/users

- Creates a new user using `userDTO`

#### PUT /api/users/{id}

- Updates a user by id

#### DELETE /api/users/{id}

- Deletes a user by id

## PortfolioItem endpoints

#### GET /api/portfolio

- Returns a list of all portfolio items

#### GET /api/portfolio/{id}

- Returns a portfolio item by id

#### POST /api/portfolio

- Creates a new portfolio item using `portfolioItemDTO`

#### PUT /api/portfolio/{id}

- Updates a portfolio item by id

#### DELETE /api/portfolio/{id}

- Deletes a portfolio item by id

## DTOs

#### userDTO

```json
{
  "username": "String",
  "email": "String"
}
```

#### userResponseDTO

```json
{
  "id": "Integer",
  "username": "String",
  "email": "String",
  "portfolioItems": "List<portfolioItem"
}
```

#### portfolioItemDTO

```json
{
  "amount": "BigDecimal",
  "currency": "String",
  "dateOfPurchase": "LocalDateTime",
  "appUserId": "Integer"
}
```

#### portfolioItemResponseDTO

```json
{
  "amount": "BigDecimal",
  "currency": "String",
  "dateOfPurchase": "LocalDateTime",
  "appUserId": "Integer",
  "amountEur": "BigDecimal"
}
```