# Wallet Tracking API

This project is a Spring Boot application that provides an API for tracking assets value in a wallet.\
The application allows users to add, retrieve, and manage their assets.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Endpoints](#endpoints)
3. [Project Setup](#project-setup)
4. [Example API Requests](#example-api-requests)

---

## Project Overview

This API provides functionality to:
- Add assets to a wallet.
- Retrieve all assets in a wallet.
- Clear all assets in a wallet.
- Calculate the performance of assets for the current day or a given past date.

---

## Endpoints

| **Method** | **Endpoint**              | **Description**                              | **Request Parameters/Body**               | **Response Body**        |
|------------|---------------------------|----------------------------------------------|-------------------------------------------|--------------------------|
| `POST`     | `/api/assets`             | Add one or more assets to the wallet         | Array of `AssetRequestDTO`                | `List<AssetResponseDTO>` |
| `GET`      | `/api/assets`             | Retrieve all assets in the wallet            | N/A                                       | `List<AssetResponseDTO>` |
| `DELETE`   | `/api/assets`             | Clear all assets in th wallet                | N/A                                       | N/A                      |
| `GET`      | `/api/assets/performance` | Retrieve performance of all assets over time | Query param: `pastDateParam` (yyyy-MM-dd) | `AssetsPerformanceDTO`   |

### **Request/Response DTOs**

- **AssetRequestDTO**
    ```json
    [
        {
            "symbol": "BTC",
            "quantity": 2.5,
            "originalPrice": 45000.00
        },
        {
            "symbol": "ETH",
            "quantity": 10,
            "originalPrice": 3000.00
        },
        {
            "symbol": "LTC",
            "quantity": 10,
            "originalPrice": 300.00
        }
    ]
    ```

- **AssetResponseDTO**
    ```json
    [
        {
            "symbol": "BTC",
            "quantity": 2.5,
            "originalPrice": 45000.00
        },
        {
            "symbol": "ETH",
            "quantity": 10,
            "originalPrice": 3000.00
        },
        {
            "symbol": "LTC",
            "quantity": 10,
            "originalPrice": 300.00
        }
    ]
    ```

- **AssetsPerformanceDTO**
    ```json
    {
        "total": 200145.17,
        "best_asset": "BTC",
        "best_performance": 54.92,
        "worst_asset": "LTC",
        "worst_performance": -76.82
    }
    ```

---

## Project Setup

To set up and run the project locally:

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- Any IDE (e.g., IntelliJ, Eclipse, VS Code)

### Steps

1. Clone the repository:
    ```bash
    git clone https://github.com/fnps/crypto-performance.git
    cd crypto-performance
    ```

2. Build the project:
    ```bash
    mvn clean install
    ```

3. Run the application:
    ```bash
    mvn spring-boot:run
    ```

4. Access the API at:
    - **Base URL:** `http://localhost:8080/api/assets`

---

## Example API Requests

### Add Assets to Wallet

**Request:**
```http
POST /api/assets
Content-Type: application/json

[
    {
        "symbol": "BTC",
        "quantity": 2.5,
        "originalPrice": 45000.00
    },
    {
        "symbol": "ETH",
        "quantity": 10,
        "originalPrice": 3000.00
    },
    {
        "symbol": "LTC",
        "quantity": 10,
        "originalPrice": 300.00
    }
]
```

### Get performance from the wallet with optional past date param

**Request:**
```http
GET /api/assets/performance?pastDateParam=2023-01-01
Content-Type: application/json
```
**Response**
```
{
    "total": 200145.17,
    "best_asset": "BTC",
    "best_performance": 54.92,
    "worst_asset": "LTC",
    "worst_performance": -76.82
}
```