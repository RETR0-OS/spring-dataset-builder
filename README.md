# DataCanvas - Interactive Dataset Builder

## Project Overview 
DataCanvas is an interactive web application for creating multi-dimensional datasets through an intuitive paint-like interface. The application consists of a Spring Boot backend and HTML/CSS/JavaScript frontend.

## Installation

### Prerequisites
- Java 21 
- MySQL 8.0+
- Maven
- Node.js (for serving frontend)

### Database Setup
```sql
CREATE DATABASE datasetbuilder;
```

### Configure Environment Variables
```bash
export DB_URL=jdbc:mysql://localhost:3306/datasetbuilder
export DB_USER=your_username 
export DB_PASS=your_password
```

### Build & Run Backend
```bash
cd datasetBuilder/datasetBuilder
mvn clean install
mvn spring-boot:run
```

### Run Frontend
```bash
cd frontend
# Use any HTTP server, e.g:
npx http-server 
```

## Project Structure

```
datasetBuilder/    - Spring Boot backend
├── src/
│   └── main/
│       ├── java/
│       │   └── com/aaditya/honors/datasetBuilder/
│       │       ├── Controllers/    - REST endpoints
│       │       ├── Models/         - Data models
│       │       ├── Repositories/   - Database access
│       │       └── Services/       - Business logic
│       └── resources/
│           └── application.properties
frontend/          - Web interface
├── css/          - Stylesheets
├── datasets/     - Dataset views
|      |
|      |
|      ├── build/ - Build New Dataset Pages
|      |
|      └── dashboard/ - Dataset Management Dashboard Pages  
├── images/       - Static assets
└── js/           - JavaScript files
```

## Features

### Interactive Dataset Creation
- Up to 5 dimensions:
  - 2-4 numerical variables
  - 1 optional categorical variable
  
### Drawing Interface
- Paint-like brush tool
- Adjustable brush size and density
- Point size and opacity controls
- Shape-based categorical data

### Dataset Management 
- Save datasets to MySQL database
- View saved datasets
- Download as CSV
- Delete datasets

## API Endpoints

```
GET    /api/v1/datasets/list/all/     - List all datasets
GET    /api/v1/datasets/get/{id}/     - Get dataset by ID
POST   /api/v1/datasets/add/new/      - Create new dataset
DELETE /api/v1/datasets/delete/{id}   - Delete dataset
GET    /api/v1/datasets/get/{id}/download/ - Download dataset as CSV
```

## Tech Stack

### Backend
- Spring Boot 3.4
- Spring Data JPA  
- MySQL

### Frontend
- HTML5/CSS3
- JavaScript
- Bootstrap 5
- Chart.js

## Contributing
This repository does not accept contributions yet as it is a part of the author's school ciriculum. This situation may change in the future after obtaining the neccssary approvals.

## License
This project is licensed under the MIT License. Check ```LICENSE``` file for more information.
