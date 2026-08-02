# BuziaHub API

Backend REST API for managing patients and appointments in a healthcare-style application.

## Overview

BuziaHub API is a Java and Spring Boot backend application that exposes REST endpoints for managing patient data and appointment-related workflows. The project is structured using typical Spring Boot layers such as controllers, services, repositories, DTOs, and JPA entities.

The application demonstrates:
- CRUD-style patient management
- paginated and filterable patient search
- DTO-based API responses
- service-layer validation
- persistence with Spring Data JPA / Hibernate
- unit and web-layer testing with JUnit, Mockito, and MockMvc

## Features

### Patient management
- create patients
- fetch patient details by ID
- update patient name
- update patient contact details
- archive patients
- retrieve active patients
- retrieve patient summary views
- retrieve recent patients
- delete all patients
- delete seeded patients

### Search
- search patients using optional criteria
- filter by:
    - first name
    - last name
    - gender
    - active status
- support for text match modes such as:
    - `PREFIX`
    - `CONTAINS`
- paginated responses with sorting support

### Testing
- unit tests for service-layer logic
- controller tests for request binding and HTTP behaviour
- Mockito-based mocking of repositories
- validation and exception-path testing

## Tech stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- JUnit 5
- Mockito
- MockMvc
- PostgreSQL

## Project structure

```text
src/
├── main/
│   └── java/com/buziahub/buziahub_api/
│       ├── patient/
│       ├── appointment/
│       ├── exceptions/
│       └── ...
└── test/
    └── java/com/buziahub/buziahub_api/
        ├── patient/
        ├── appointment/
        └── ...
```

## Architecture

The application follows a layered Spring Boot structure:

- **Controller** – handles HTTP requests and responses
- **DTOs** – represent request and response payloads
- **Service** – contains business logic and validation
- **Repository** – handles database access
- **Entity** – models persisted domain objects
- **Specification** – builds dynamic search queries

Example flow:

`HTTP request -> controller -> DTO binding -> service -> repository -> JPA/Hibernate -> database -> response DTO`

## Example use cases

### Create a patient
A client sends patient details to the API, the service creates a `Patient` entity, persists it through the repository, and returns a response DTO.

### Search patients
A client can search using optional filters such as first name, last name, gender, and active status. The backend builds a dynamic JPA specification and returns paginated summary results.

## Running the application

### Prerequisites
Make sure you have installed:

- Java
- Maven
- PostgreSQL

### Run locally
```bash
git clone https://github.com/PrzemyslawGromiec/buziahub-api.git
cd buziahub-api
./mvnw spring-boot:run
```

If you are on Windows, use:

```bash
mvnw.cmd spring-boot:run
```

> If your project requires a database configuration, application profile, or environment variables, document them here.

## Running tests

Run all tests with:

```bash
./mvnw test
```

or on Windows:

```bash
mvnw.cmd test
```

## API behaviour highlights

### Validation
The service layer includes validation logic for search criteria. For example, `CONTAINS` text search requires a minimum search length before querying the repository.

### Pagination and sorting
Search and recent-patient endpoints support Spring `Pageable`, allowing:
- page number
- page size
- sorting

### Error handling
The API uses custom exceptions such as:
- `PatientNotFoundException`
- `InvalidPatientSearchCriteriaException`

These help keep controller and service responsibilities clear.

## Development goals

This project is also a learning-focused backend codebase for practising:
- clean layered architecture
- Spring Boot REST API design
- DTO mapping
- validation strategy
- JPA specifications
- automated testing
- maintainable service logic

## Possible future improvements

- API documentation with Swagger / OpenAPI
- integration tests with a real database
- authentication and authorisation
- audit/history tracking
- Docker support
- CI pipeline
- improved observability and logging

## Contributing

Contributions, suggestions, and feedback are welcome.  
If you spot a bug or have an improvement idea, feel free to open an issue or a pull request.

## Author

Created by [PrzemyslawGromiec](https://github.com/PrzemyslawGromiec)