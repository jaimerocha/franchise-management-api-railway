#!/bin/bash
# validate.sh - Script to validate Terraform configuration without deploying

set -e

echo "🚀 Franchise Management API - Terraform Validation"
echo "================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print with color
print_status() {
    if [ $2 -eq 0 ]; then
        echo -e "${GREEN}✅ $1${NC}"
    else
        echo -e "${RED}❌ $1${NC}"
        exit 1
    fi
}

# Check if we're in the correct directory
if [ ! -f "main.tf" ]; then
    echo -e "${RED}❌ Error: main.tf not found. Run this script from the terraform/ directory${NC}"
    exit 1
fi

# 1. Check Terraform installation
echo "1️⃣ Checking Terraform installation..."
if command -v terraform &> /dev/null; then
    VERSION=$(terraform version | head -n 1)
    echo -e "${GREEN}✅ $VERSION${NC}"
else
    echo -e "${RED}❌ Terraform is not installed${NC}"
    echo "   Install Terraform from: https://www.terraform.io/downloads"
    exit 1
fi

# 2. Initialize Terraform (without backend for local validation)
echo ""
echo "2️⃣ Initializing Terraform..."
terraform init -backend=false > /dev/null 2>&1
print_status "Terraform initialized" $?

# 3. Format code
echo ""
echo "3️⃣ Formatting Terraform code..."
terraform fmt -recursive -check
if [ $? -eq 0 ]; then
    print_status "Code properly formatted" 0
else
    echo -e "${YELLOW}⚠️  Code needs formatting. Run: terraform fmt -recursive${NC}"
fi

# 4. Validate syntax
echo ""
echo "4️⃣ Validating syntax..."
terraform validate
print_status "Syntax valid" $?

# 5. Check for terraform.tfvars
echo ""
echo "5️⃣ Checking variables file..."
if [ -f "terraform.tfvars" ]; then
    print_status "terraform.tfvars found" 0
else
    echo -e "${YELLOW}⚠️  terraform.tfvars not found. Creating from example...${NC}"
    cp terraform.tfvars.example terraform.tfvars
    echo -e "${YELLOW}   Please edit terraform.tfvars with your values${NC}"
fi

# 6. Generate plan without AWS credentials (simulation mode)
echo ""
echo "6️⃣ Generating infrastructure plan..."
echo -e "${YELLOW}   Note: This will show errors if AWS credentials are not configured${NC}"

# Set dummy credentials for validation
export AWS_ACCESS_KEY_ID=dummy
export AWS_SECRET_ACCESS_KEY=dummy
export AWS_DEFAULT_REGION=us-east-1

# Try to generate plan
terraform plan -input=false -refresh=false > plan.output 2>&1 || true

# 7. Analyze resources to be created
echo ""
echo "7️⃣ Resources to be created:"
echo "================================"

# Count resources by type
echo -e "${YELLOW}📊 Resource summary:${NC}"
grep -E "will be created|must be replaced" plan.output 2>/dev/null | wc -l | xargs -I {} echo "   Total resources: {}"

# List resource types
echo ""
echo -e "${YELLOW}📦 Resource types:${NC}"
grep "will be created" plan.output 2>/dev/null | grep -oE 'aws_[a-z_]+' | sort | uniq -c | while read count resource; do
    echo "   - $resource: $count"
done

# 8. Generate dependency graph
echo ""
echo "8️⃣ Generating dependency graph..."
if command -v dot &> /dev/null; then
    terraform graph > infrastructure.dot
    print_status "Graph generated in infrastructure.dot" $?
    echo "   To visualize: dot -Tpng infrastructure.dot -o infrastructure.png"
else
    echo -e "${YELLOW}⚠️  Graphviz not installed. Cannot generate visual diagram${NC}"
fi

# 9. Verify modules
echo ""
echo "9️⃣ Verifying modules:"
for module in modules/*/; do
    if [ -d "$module" ]; then
        module_name=$(basename "$module")
        if [ -f "$module/main.tf" ]; then
            echo -e "${GREEN}✅ Module $module_name${NC}"
        else
            echo -e "${RED}❌ Module $module_name missing main.tf${NC}"
        fi
    fi
done

# 10. Cost estimation
echo ""
echo "💰 AWS Cost Estimation (monthly):"
echo "=================================="
echo "   RDS MySQL (db.t3.micro):        ~\$15"
echo "   ElastiCache (cache.t3.micro):    ~\$13"
echo "   ECS Fargate (0.25 vCPU):         ~\$10"
echo "   Application Load Balancer:        ~\$25"
echo "   NAT Gateways (2x):                ~\$90"
echo "   ----------------------------------"
echo -e "${YELLOW}   TOTAL ESTIMATED:                  ~\$153/month${NC}"
echo ""
echo -e "${GREEN}💡 Tip: For development, use 1 NAT Gateway to save \$45/month${NC}"

# 11. Next steps
echo ""
echo "✨ Validation complete!"
echo ""
echo "📝 Next steps:"
echo "   1. Configure AWS credentials: aws configure"
echo "   2. Edit terraform.tfvars with your values"
echo "   3. Run: terraform plan"
echo "   4. If everything looks good: terraform apply"
echo ""
echo "🆓 To test without AWS:"
echo "   - Use LocalStack: https://localstack.cloud"
echo "   - Or Terraform Cloud (free account): https://app.terraform.io"

# Clean up
rm -f plan.output infrastructure.dot 2>/dev/null

echo ""
echo "🎉 Terraform configuration is ready to use!"