#!/bin/bash
# quick-start.sh - Quick start script for Franchise Management API

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🚀 Franchise Management API - Quick Start${NC}"
echo "========================================="
echo ""

# Function to check command exists
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${RED}❌ $1 is not installed${NC}"
        echo "   Please install $1 first"
        exit 1
    else
        echo -e "${GREEN}✅ $1 is installed${NC}"
    fi
}

# 1. Check prerequisites
echo -e "${YELLOW}1️⃣ Checking prerequisites...${NC}"
check_command docker
check_command git
check_command curl
check_command jq

# 2. Build Docker image
echo -e "\n${YELLOW}2️⃣ Building Docker image...${NC}"
if docker build -f docker/Dockerfile -t franchise-management-api:1.0.0 .; then
    echo -e "${GREEN}✅ Docker image built successfully${NC}"
else
    echo -e "${RED}❌ Failed to build Docker image${NC}"
    exit 1
fi

# 3. Start services
echo -e "\n${YELLOW}3️⃣ Starting services with Docker Compose...${NC}"

# First, stop any existing containers
echo "Stopping any existing containers..."
cd docker
docker compose down
docker compose -f docker-compose-local.yml down
cd ..

# Now start the complete stack
cd docker
if docker compose up -d; then
    echo -e "${GREEN}✅ Services started${NC}"
else
    echo -e "${RED}❌ Failed to start services${NC}"
    exit 1
fi
cd ..

# 4. Wait for services to be ready
echo -e "\n${YELLOW}4️⃣ Waiting for services to be ready...${NC}"
echo -n "Waiting for API to be healthy"
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "\n${GREEN}✅ API is ready!${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

# 5. Show service status
echo -e "\n${YELLOW}5️⃣ Service status:${NC}"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "NAME|franchise"

# 6. Test API
echo -e "\n${YELLOW}6️⃣ Testing API health...${NC}"
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
if [ "$HEALTH" == "UP" ]; then
    echo -e "${GREEN}✅ API is healthy!${NC}"
else
    echo -e "${RED}❌ API health check failed${NC}"
fi

# 7. Show quick test commands
echo -e "\n${YELLOW}7️⃣ Quick test commands:${NC}"
echo "Create a franchise:"
echo -e "${BLUE}curl -X POST http://localhost:8080/api/v1/franchises \\
  -H \"Content-Type: application/json\" \\
  -d '{\"name\": \"Test Franchise\"}'${NC}"

echo -e "\nList franchises:"
echo -e "${BLUE}curl http://localhost:8080/api/v1/franchises | jq .${NC}"

echo -e "\nRun complete test suite:"
echo -e "${BLUE}./test-api.sh${NC}"

# 8. Show logs command
echo -e "\n${YELLOW}8️⃣ To view logs:${NC}"
echo -e "${BLUE}docker logs franchise-api -f${NC}"

# 9. Show stop command
echo -e "\n${YELLOW}9️⃣ To stop services:${NC}"
echo -e "${BLUE}cd docker && docker compose down -v${NC}"

echo -e "\n${GREEN}✨ Quick start complete!${NC}"
echo "API is running at: http://localhost:8080"
echo "Documentation: See README.md"
echo ""
echo -e "${BLUE}Happy coding! 🎉${NC}"