# Franchise Management API - Railway Live Demo

## 🌐 Live Demo URL

The API is deployed and running at: **https://franchise-management-api-railway-production.up.railway.app**

## ✅ API Endpoints - Technical Test Requirements

The API implements all 6 required endpoints as specified in the technical test:

### 1️⃣ Add New Franchise
```bash
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s"}'
```

### 2️⃣ Add New Branch to Franchise
```bash
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Downtown"}'
```

### 3️⃣ Add New Product to Branch
```bash
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Big Mac", "stock": 100}'
```

### 4️⃣ Delete Product from Branch
```bash
curl -X DELETE https://franchise-management-api-railway-production.up.railway.app/api/v1/products/1
```

### 5️⃣ Modify Product Stock
```bash
curl -X PATCH https://franchise-management-api-railway-production.up.railway.app/api/v1/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 250}'
```

### 6️⃣ Get Products with Maximum Stock per Branch for a Franchise
```bash
curl https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/max-stock-products
```

**Response example:**
```json
[
  {
    "productId": 1,
    "productName": "Big Mac",
    "stock": 250,
    "branchId": 1,
    "branchName": "McDonald's Downtown",
    "franchiseId": 1,
    "franchiseName": "McDonald's"
  },
  {
    "productId": 5,
    "productName": "Quarter Pounder",
    "stock": 180,
    "branchId": 2,
    "branchName": "McDonald's Airport",
    "franchiseId": 1,
    "franchiseName": "McDonald's"
  }
]
```

## 🧪 Run Complete Test Suite

### Option 1: Download and Run Railway Test Script

#### For Linux/Mac:
```bash
# Download the Railway-specific test script
wget https://raw.githubusercontent.com/jaimerocha/franchise-management-api-railway/main/test-api-railway.sh

# Make it executable
chmod +x test-api-railway.sh

# Run all tests (URL is already configured for Railway)
./test-api-railway.sh
```

#### For Windows (Git Bash):
```bash
# Download the Railway-specific test script
curl -O https://raw.githubusercontent.com/jaimerocha/franchise-management-api-railway/main/test-api-railway.sh

# Make it executable
chmod +x test-api-railway.sh

# Run all tests
./test-api-railway.sh
```

#### For Windows (PowerShell):
```powershell
# Download the Railway-specific test script
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/jaimerocha/franchise-management-api-railway/main/test-api-railway.sh" -OutFile "test-api-railway.sh"

# Then run in Git Bash
# ./test-api-railway.sh
```

### Option 2: Run Tests Manually

```bash
# 1. Create multiple franchises
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Hut"}'

curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway"}'

# 2. Add branches to franchise ID 1
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Branch North"}'

curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Branch South"}'

# 3. Add products with different stock levels
curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Product A", "stock": 50}'

curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Product B", "stock": 150}'

curl -X POST https://franchise-management-api-railway-production.up.railway.app/api/v1/branches/2/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Product C", "stock": 200}'

# 4. Test max stock endpoint
curl https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/max-stock-products
```

## 📊 Additional Endpoints

### Health Check
```bash
curl https://franchise-management-api-railway-production.up.railway.app/actuator/health
```

### Metrics
```bash
curl https://franchise-management-api-railway-production.up.railway.app/actuator/metrics
```

### List All Franchises
```bash
curl https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises
```

### Get Specific Franchise
```bash
curl https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1
```

### List Branches by Franchise
```bash
curl https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/branches
```

### Update Franchise Name
```bash
curl -X PATCH https://franchise-management-api-railway-production.up.railway.app/api/v1/franchises/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s International"}'
```

### Update Branch Name
```bash
curl -X PATCH https://franchise-management-api-railway-production.up.railway.app/api/v1/branches/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Downtown Premium Branch"}'
```

### Update Product Name
```bash
curl -X PATCH https://franchise-management-api-railway-production.up.railway.app/api/v1/products/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Big Mac Deluxe"}'
```

## 🏗️ Technical Implementation

- **Reactive Programming**: Spring WebFlux with Project Reactor
- **Clean Architecture**: Domain, Application, and Infrastructure layers
- **Database**: MySQL with R2DBC for reactive access
- **Containerization**: Docker with multi-stage build
- **Cloud Deployment**: Railway with automatic scaling
- **Testing**: Comprehensive unit tests with 80%+ coverage

## 📈 Performance Characteristics

- Non-blocking I/O for high concurrency
- Reactive streams for backpressure handling
- Connection pooling for database optimization
- Average response time: < 100ms

## 🔐 Security

- HTTPS/TLS encryption
- Input validation on all endpoints
- SQL injection protection via R2DBC
- CORS configured for API access

## 🚨 Important Notes

### Redis Support
Railway's free tier does NOT include Redis. The application gracefully handles Redis absence:
- Cache operations are skipped
- All functionality remains available
- Performance impact is minimal for demo purposes

### Free Tier Limitations
- 500 hours/month execution time
- $5 usage credit (not charged)
- Apps sleep after ~5 minutes of inactivity
- First request after sleep takes 5-10 seconds

---

**Note**: This is a demonstration deployment. For production use, additional configuration for security, rate limiting, and monitoring would be implemented.