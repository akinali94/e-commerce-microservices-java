# E-commerce Microservices Demo


This project is a **cloud-native e-commerce microservices demo application**, designed to showcase scalable, containerized, and event-driven architecture using modern AWS services.

It’s a **web-based shopping platform** where users can browse products, add them to their cart, and complete purchases.  
The goal is to evolve into a **cloud-first application** by integrating **AWS CloudWatch**, **ECS**, **ElastiCache (Redis)**, **DynamoDB**, and **Lambda**.

---

## Architecture Overview

The application is composed of **11 microservices**, built with **Java Spring Boot** for the backend and **React** for the frontend.  
Each service communicates with others via **REST APIs**.


[![Architecture of
microservices](/assets/architecture.png)](/assets/architecture.png)

| Service                                               | Description                                                                                                                       |
| ------------------------------------------------------| --------------------------------------------------------------------------------------------------------------------------------- |
| [frontend](/src/frontend)                             | Serves the web interface. No authentication required; session IDs are generated automatically.                                   |
| [cartservice](/src/cart-service)                      | Manages shopping cart data in **Redis**, supporting retrieval and updates.                                                        |
| [productcatalogservice](/src/product-catalog-service) | Provides a list of products from a JSON source; supports search and product details.                                              |
| [currencyservice](/src/currency-service)              | Converts currencies using live rates from the European Central Bank.                                                             |
| [paymentservice](/src/payment-service)                | Simulates credit card charges (mock) and returns transaction IDs.                                                                 |
| [shippingservice](/src/shipping-service)              | Calculates shipping cost estimates and simulates delivery.                                                                        |
| [emailservice](/src/email-service)                    | Sends order confirmation emails (mock).                                                                                           |
| [checkoutservice](/src/checkout-service)              | Coordinates checkout — retrieves the cart, processes payment and shipping, and triggers email notifications.                     |
| [recommendationservice](/src/recommendation-service)  | Suggests related products based on items in the user’s cart.                                                                     |
| [adservice](/src/ad-service)                          | Serves contextual text ads related to shopping activity.                                                                          |

All services are **Dockerized** and can be launched together using **Docker Compose**.

---

## Cloud Integration Goals

The next step is to migrate this architecture into AWS and make it a fully managed, cloud-native application.

**Planned integrations:**

- **AWS ECS (Elastic Container Service)** – for container orchestration  
- **AWS CloudWatch** – for centralized logging and performance metrics  
- **AWS ElastiCache (Redis)** – for caching shopping cart and session data  
- **AWS DynamoDB** – for product catalog, user data, and order persistence  
- **AWS Lambda** – for event-driven tasks such as notifications or async processing  

---

## Tech Stack

| Layer | Technology |
| ----- | ----------- |
| **Frontend** | React |
| **Backend** | Java Spring Boot |
| **Database** | Redis, DynamoDB (planned) |
| **Containerization** | Docker, Docker Compose |
| **Cloud Platform** | AWS (ECS, CloudWatch, ElastiCache, DynamoDB, Lambda) |
| **Monitoring** | CloudWatch (planned integration) |

---