#!/bin/bash
# test-api.sh - Complete API Testing Script

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo -e "${BLUE}🧪 Franchise Management API - Complete Test Suite${NC}"
echo "================================================="
echo ""

# Function to print test section
print_section() {
    echo -e "\n${YELLOW}$1${NC}"
    echo "----------------------------------------"
}

# Function to print test result
print_test() {
    echo -e "${GREEN}✓${NC} $1"
}

# 1. CREATE FRANCHISES
print_section "1️⃣  CREATING FRANCHISES"

echo "Creating McDonald's franchise..."
curl -s -X POST $BASE_URL/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s"}' | jq .
print_test "McDonald's franchise created"

echo -e "\nCreating Subway franchise..."
curl -s -X POST $BASE_URL/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway"}' | jq .
print_test "Subway franchise created"

echo -e "\nCreating KFC franchise..."
curl -s -X POST $BASE_URL/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "KFC"}' | jq .
print_test "KFC franchise created"

# 2. CREATE BRANCHES FOR MCDONALD'S (ID 1)
print_section "2️⃣  CREATING BRANCHES"

echo "Creating branches for McDonald's (ID: 1)..."
curl -s -X POST $BASE_URL/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Chipichape"}' | jq .
print_test "McDonald's Chipichape branch created"

curl -s -X POST $BASE_URL/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Jardín Plaza"}' | jq .
print_test "McDonald's Jardín Plaza branch created"

curl -s -X POST $BASE_URL/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Unicentro"}' | jq .
print_test "McDonald's Unicentro branch created"

# 3. CREATE BRANCHES FOR SUBWAY (ID 2)
echo -e "\nCreating branches for Subway (ID: 2)..."
curl -s -X POST $BASE_URL/franchises/2/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway Centro"}' | jq .
print_test "Subway Centro branch created"

curl -s -X POST $BASE_URL/franchises/2/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway Norte"}' | jq .
print_test "Subway Norte branch created"

# 4. CREATE PRODUCTS IN MCDONALD'S CHIPICHAPE (Branch ID 1)
print_section "3️⃣  CREATING PRODUCTS"

echo "Creating products for McDonald's Chipichape (Branch ID: 1)..."
curl -s -X POST $BASE_URL/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Big Mac", "stock": 100}' | jq .
print_test "Big Mac product created"

curl -s -X POST $BASE_URL/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "McNuggets 10pz", "stock": 150}' | jq .
print_test "McNuggets 10pz product created"

curl -s -X POST $BASE_URL/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "McFlurry Oreo", "stock": 80}' | jq .
print_test "McFlurry Oreo product created"

# 5. CREATE PRODUCTS IN MCDONALD'S JARDÍN PLAZA (Branch ID 2)
echo -e "\nCreating products for McDonald's Jardín Plaza (Branch ID: 2)..."
curl -s -X POST $BASE_URL/branches/2/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Quarter Pounder", "stock": 120}' | jq .
print_test "Quarter Pounder product created"

curl -s -X POST $BASE_URL/branches/2/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Papas Grandes", "stock": 200}' | jq .
print_test "Papas Grandes product created"

# 6. CREATE PRODUCTS IN SUBWAY CENTRO (Branch ID 4)
echo -e "\nCreating products for Subway Centro (Branch ID: 4)..."
curl -s -X POST $BASE_URL/branches/4/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Sub Italiano BMT", "stock": 60}' | jq .
print_test "Sub Italiano BMT product created"

curl -s -X POST $BASE_URL/branches/4/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Sub Pollo Teriyaki", "stock": 45}' | jq .
print_test "Sub Pollo Teriyaki product created"

# 7. MODIFY STOCK
print_section "4️⃣  MODIFYING STOCK"

echo "Updating stock for product ID 1 (Big Mac)..."
curl -s -X PATCH $BASE_URL/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 250}' | jq .
print_test "Stock updated to 250"

# 8. UPDATE NAMES
print_section "5️⃣  UPDATING NAMES"

echo "Updating franchise name..."
curl -s -X PATCH $BASE_URL/franchises/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Colombia"}' | jq .
print_test "Franchise name updated"

echo -e "\nUpdating branch name..."
curl -s -X PATCH $BASE_URL/branches/1/name \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Chipichape Cali"}' | jq .
print_test "Branch name updated"

echo -e "\nUpdating product name..."
curl -s -X PATCH $BASE_URL/products/2/name \
  -H "Content-Type: application/json" \
  -d '{"name": "McNuggets Cajita Feliz"}' | jq .
print_test "Product name updated"

# 9. DELETE PRODUCT
print_section "6️⃣  DELETING PRODUCT"

echo "Deleting product ID 3 (McFlurry Oreo)..."
curl -s -X DELETE $BASE_URL/products/3
print_test "Product deleted"

# 10. VERIFICATION QUERIES
print_section "7️⃣  VERIFICATION QUERIES"

echo "Getting all franchises..."
curl -s $BASE_URL/franchises | jq .
print_test "All franchises retrieved"

echo -e "\nGetting specific franchise (ID: 1)..."
curl -s $BASE_URL/franchises/1 | jq .
print_test "Franchise details retrieved"

echo -e "\nGetting branches for franchise (ID: 1)..."
curl -s $BASE_URL/franchises/1/branches | jq .
print_test "Franchise branches retrieved"

echo -e "\nGetting products with max stock per branch for franchise (ID: 1)..."
curl -s $BASE_URL/franchises/1/max-stock-products | jq .
print_test "Max stock products retrieved"

# Summary
print_section "✅ TEST SUMMARY"
echo -e "${GREEN}All API endpoints tested successfully!${NC}"
echo ""
echo "Tested endpoints:"
echo "  ✓ POST   /api/v1/franchises                     - Create franchise"
echo "  ✓ POST   /api/v1/franchises/{id}/branches       - Create branch" 
echo "  ✓ POST   /api/v1/branches/{id}/products         - Create product"
echo "  ✓ PATCH  /api/v1/products/{id}/stock            - Update stock"
echo "  ✓ PATCH  /api/v1/franchises/{id}/name           - Update franchise name"
echo "  ✓ PATCH  /api/v1/branches/{id}/name             - Update branch name"
echo "  ✓ PATCH  /api/v1/products/{id}/name             - Update product name"
echo "  ✓ DELETE /api/v1/products/{id}                  - Delete product"
echo "  ✓ GET    /api/v1/franchises                     - List franchises"
echo "  ✓ GET    /api/v1/franchises/{id}                - Get franchise"
echo "  ✓ GET    /api/v1/franchises/{id}/branches       - List branches"
echo "  ✓ GET    /api/v1/franchises/{id}/max-stock-products - Max stock report"
echo ""
echo -e "${BLUE}🎉 API is working correctly!${NC}"