# MangmentsOrders
This repository is a Spring Boot application for efficient order management, precise inventory monitoring, and seamless product coordination. This repository is a collection of microservices,it contains Java Spring Boot, REST API, Docker, Kafka, Netflix Eureka, Spring Cloud Gateway, MySQL, MongoDB.<br>


## Project Structure
- `product-service`: First service that contains the products and stores it in MongoDB.
- `order-service`: Second service that mangaes the orders and stores the orderes in MySQL. 
- `notifacation-service`: Third service that notifies if an order has been done. it notifies using Kafka.
- `inventory-service`: Fourth service that manges the inventory of the products and the orders.
- `discovery-server`: Fifth service that runs the Spring Cloud Netflix - Eureka Server in order to make key tenets of a microservice-based architecture.
- `api-gateway`: An api gateway to provide centralized handling of API communication between the services.
- `docker-compose.yml`: To run kafka through docker.

