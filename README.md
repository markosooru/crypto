# Crypto portfolio API

This is a simple API that allows you to manage your crypto portfolio. You can add, remove, update and list your assets.

Using Maven, JPA and Postgres.

## Use

Run compose.yaml

Run CryptoApplication.java

Application will be available at `http://localhost:8080`

## AppUser endpoints

#### GET /api/appusers

- Returns a list of all users

#### GET /api/appusers/{id}

- Returns a user by id

#### POST /api/appusers

- Creates a new user using `userDTO`

#### PUT /api/appusers/{id}

- Updates a user by id

#### DELETE /api/appusers/{id}

- Deletes a user by id

## PortfolioItem endpoints

#### GET /api/portfolioitems

- Returns a list of all portfolio items

#### GET /api/portfolioitems/{id}

- Returns a portfolio item by id

#### POST /api/portfolioitems

- Creates a new portfolio item using `portfolioItemDTO`

#### PUT /api/portfolioitems/{id}

- Updates a portfolio item by id

#### DELETE /api/portfolioitems/{id}

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