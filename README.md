# Franchise Management API

## 🏢 Description

RESTful reactive API built with Spring Boot WebFlux for managing franchises, branches, and products. Implements Clean Architecture, reactive programming, and development best practices.

## 🚀 Key Features

- **Clean Architecture**: Clear separation between domain, application, and infrastructure layers
- **Reactive Programming**: Implemented with Spring WebFlux and Project Reactor
- **Dual Persistence**: MySQL (R2DBC) for transactional data and Redis for caching
- **Containerization**: Docker and Docker Compose for simplified deployment
- **Infrastructure as Code**: Terraform for AWS provisioning
- **Testing**: Unit tests with comprehensive coverage
- **RESTful API**: Well-defined endpoints following REST standards

## 📋 Prerequisites

- Java 17+
- Maven 3.8+
- Docker 20.10+
- Docker Compose 2.0+
- MySQL 8.0 (optional if using Docker)
- Redis 7+ (optional if using Docker)

## 🏗️ Architecture

The project follows Clean Architecture principles:

```
src/
├── main/
│   ├── java/com/retailchain/franchise/
│   │   ├── domain/                    # Business core
│   │   │   ├── model/                 # Domain entities
│   │   │   ├── port/                  # Interfaces (ports)
│   │   │   │   ├── input/             # Use cases
│   │   │   │   └── output/            # Repositories
│   │   │   └── exception/             # Domain exceptions
│   │   ├── application/               # Application logic
│   │   │   ├── service/               # Use case implementations
│   │   │   └── dto/                   # DTOs
│   │   └── infrastructure/            # Implementation details
│   │       ├── adapter/
│   │       │   ├── input/rest/        # REST controllers
│   │       │   └── output/persistence/ # Repository implementations
│   │       └── config/                # Configurations
│   └── resources/
│       ├── application.yml            # Application configuration
│       └── schema.sql                 # Database schema
└── test/                              # Unit and integration tests
```

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**
```bash
git clone https://github.com/jaimerocha/franchise-management-api.git
cd franchise-management-api
```

2. **Build the Docker image**
```bash
docker build -f docker/Dockerfile -t franchise-management-api:1.0.0 .
```

3. **Start all services**
```bash
cd docker
docker compose up -d
```

4. **Verify the status**
```bash
docker ps
docker logs franchise-api -f
```

5. **Test the API**
```bash
curl http://localhost:8080/actuator/health
```

6. **Stop services and clean up**
```bash
# Stop all services and remove volumes
docker compose down -v
```

### Option 2: Local Execution with Eclipse/IntelliJ

1. **Start database services**
```bash
cd docker
docker compose -f docker-compose-local.yml up -d
```

2. **Run the application**
```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

Or from your IDE by running the `FranchiseManagementApiApplication` class

3. **Stop database services and clean up**
```bash
cd docker
docker compose -f docker-compose-local.yml down -v
```

## 📡 API Endpoints

### Franchises

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/franchises` | Create new franchise |
| GET | `/api/v1/franchises` | List all franchises |
| GET | `/api/v1/franchises/{id}` | Get franchise by ID |
| PATCH | `/api/v1/franchises/{id}/name` | Update franchise name |

### Branches

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/franchises/{franchiseId}/branches` | Add branch to franchise |
| GET | `/api/v1/franchises/{franchiseId}/branches` | List franchise branches |
| PATCH | `/api/v1/branches/{id}/name` | Update branch name |

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/branches/{branchId}/products` | Add product to branch |
| DELETE | `/api/v1/products/{id}` | Delete product |
| PATCH | `/api/v1/products/{id}/stock` | Update product stock |
| PATCH | `/api/v1/products/{id}/name` | Update product name |
| GET | `/api/v1/franchises/{id}/max-stock-products` | Get products with highest stock per branch |

## 🧪 Testing the API

### Complete Test Script

Create a file `test-api.sh`:

```bash
#!/bin/bash

# API Testing Script
echo "🧪 Testing Franchise Management API"
echo "==================================="

# Base URL
BASE_URL="http://localhost:8080/api/v1"

# 1. Create franchises
echo -e "\n1️⃣ Creating franchises..."
curl -X POST $BASE_URL/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Palace"}'

curl -X POST $BASE_URL/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Burger World"}'

# 2. Add branches (assuming franchise ID 1)
echo -e "\n2️⃣ Adding branches..."
curl -X POST $BASE_URL/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Palace Downtown"}'

curl -X POST $BASE_URL/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Palace Mall"}'

# 3. Add products (assuming branch ID 1)
echo -e "\n3️⃣ Adding products..."
curl -X POST $BASE_URL/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Pepperoni Pizza", "stock": 50}'

curl -X POST $BASE_URL/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Margherita Pizza", "stock": 30}'

# 4. Update stock
echo -e "\n4️⃣ Updating stock..."
curl -X PATCH $BASE_URL/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 100}'

# 5. Update names
echo -e "\n5️⃣ Updating names..."
curl -X PATCH $BASE_URL/franchises/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Palace International"}'

curl -X PATCH $BASE_URL/branches/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Palace Downtown Central"}'

curl -X PATCH $BASE_URL/products/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Super Pepperoni Pizza"}'

# 6. Get max stock products
echo -e "\n6️⃣ Getting products with max stock..."
curl $BASE_URL/franchises/1/max-stock-products

# 7. Delete a product
echo -e "\n7️⃣ Deleting product..."
curl -X DELETE $BASE_URL/products/2

# 8. List all franchises
echo -e "\n8️⃣ Listing all franchises..."
curl $BASE_URL/franchises

echo -e "\n✅ Testing complete!"
```

Run with:
```bash
chmod +x test-api.sh
./test-api.sh
```

## 🧪 Unit Testing

**Prerequisites**: MySQL and Redis must be running for tests.

Start test dependencies:
```bash
cd docker
docker compose -f docker-compose-local.yml up -d
cd ..
```

Run all tests:
```bash
./mvnw test
```

## 🐳 Docker

### Build image
```bash
docker build -f docker/Dockerfile -t franchise-management-api:1.0.0 .
```

### Run with Docker Compose
```bash
cd docker
docker compose up -d
```

### Stop and clean up
```bash
cd docker
docker compose down -v    # Stop services and remove volumes
```

### Push to Docker Hub (Optional)
If you want to push to your own Docker Hub account:
```bash
# Tag with your username
docker tag franchise-management-api:1.0.0 yourusername/franchise-management-api:1.0.0

# Login and push
docker login
docker push yourusername/franchise-management-api:1.0.0

# Update docker-compose.yml to use your image:
# Change: image: franchise-management-api:1.0.0
# To: image: yourusername/franchise-management-api:1.0.0
```

## ☁️ AWS Deployment

The project includes Terraform configuration for AWS deployment:

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

**Note**: AWS credentials are required to execute `terraform plan/apply`. The infrastructure code is ready for deployment when AWS access is configured.

### Resources created:
- VPC with public and private subnets
- ECS Cluster with Fargate
- RDS MySQL
- ElastiCache Redis
- Application Load Balancer
- CloudWatch for monitoring

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `local` |
| `DB_USERNAME` | MySQL username | `franchise_user` |
| `DB_PASSWORD` | MySQL password | `franchise_pass` |
| `REDIS_PASSWORD` | Redis password | (empty) |

### Available Profiles

- **local**: For local development
- **docker**: For container execution  
- **production**: For AWS deployment

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

## 🛠️ Technologies Used

- **Spring Boot 3.3.13**: Main framework
- **Spring WebFlux**: Reactive programming
- **Project Reactor**: Reactive library
- **R2DBC MySQL**: Reactive MySQL driver
- **Spring Data Redis Reactive**: Reactive Redis integration
- **Docker**: Containerization
- **Terraform**: Infrastructure as Code
- **Maven**: Dependency management
- **Lombok**: Boilerplate code reduction

## 📁 Database Schema

```sql
-- franchises
├── id (PK)
├── name
├── created_at
└── updated_at

-- branches
├── id (PK)
├── name
├── franchise_id (FK)
├── created_at
└── updated_at

-- products
├── id (PK)
├── name
├── stock
├── branch_id (FK)
├── created_at
└── updated_at
```

## 🤝 Development Workflow

### Git Workflow
```bash
# Clone repository
git clone https://github.com/jaimerocha/franchise-management-api.git

# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "feat: add new feature"

# Push to GitHub
git push origin feature/new-feature
```

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Write self-documenting code
- Add JavaDoc for public methods

## 📝 License

This project is proprietary and confidential.

## 👤 Author

**Jaime Alberto Rocha Gomez**
- GitHub: [@jaimerocha](https://github.com/jaimerocha)
- Email: jaimerocha2006@gmail.com
- Location: Cali, Colombia

---

Built with ❤️ using Spring Boot and Clean Architecture