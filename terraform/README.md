# Terraform - Franchise Management API

## 📋 Description

This Terraform configuration deploys the complete infrastructure on AWS for the Franchise Management API using managed services and following best practices.

## 🏗️ Architecture Deployed

```
┌─────────────────────────────────────────────────────────────┐
│                          Internet                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    ┌──────┴──────┐
                    │     ALB     │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │            Public Subnets           │
        │                                     │
        │  ┌─────────────┐ ┌─────────────┐  │
        │  │ NAT Gateway │ │ NAT Gateway │  │
        │  └──────┬──────┘ └──────┬──────┘  │
        └─────────┼───────────────┼──────────┘
                  │               │
        ┌─────────┼───────────────┼──────────┐
        │         │  Private Subnets │        │
        │    ┌────┴────┐      ┌────┴────┐   │
        │    │   ECS   │      │   ECS   │   │
        │    │ Fargate │      │ Fargate │   │
        │    └────┬────┘      └────┬────┘   │
        │         │                │         │
        │    ┌────┴────────────────┴────┐   │
        │    │                          │   │
        │    ▼                          ▼   │
        │ ┌──────┐                ┌───────┐ │
        │ │ RDS  │                │ Redis │ │
        │ │MySQL │                │ Cache │ │
        │ └──────┘                └───────┘ │
        └────────────────────────────────────┘
```

## 🚀 AWS Services Used

- **VPC**: Isolated network with public and private subnets
- **ECS Fargate**: Serverless containers
- **RDS MySQL**: Managed database
- **ElastiCache** (cache.t3.micro): ~$13/month after free tier
- **ECS Fargate**: ~$10/month (1 task, 0.25 vCPU, 0.5GB RAM)
- **ALB**: ~$25/month
- **NAT Gateways**: ~$90/month (2 x $45)
- **Total estimated**: ~$153/month

### 🎯 Cost Optimization

To reduce costs in development:

1. **Use single NAT Gateway** (saves $45/month):
   ```hcl
   # In modules/network/main.tf, change count from 2 to 1
   resource "aws_nat_gateway" "main" {
     count = 1  # Reduce from 2 to 1
     ...
   }
   ```

2. **Stop resources when not in use**:
   ```bash
   # Stop RDS
   aws rds stop-db-instance --db-instance-identifier franchise-api-production-mysql
   
   # Scale ECS to 0 tasks
   terraform apply -var="ecs_desired_count=0"
   ```

## 🆓 Free Testing Alternatives

### LocalStack (AWS Simulator)
```bash
# Install LocalStack
pip install localstack

# Start LocalStack
localstack start

# Configure Terraform for LocalStack
export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
```

### Terraform Plan Only
```bash
# Validate and plan without applying
terraform init
terraform validate
terraform plan -out=plan.tfplan

# Generate infrastructure graph
terraform graph | dot -Tpng > infrastructure.png
```

## 🔍 Configuration Validation

Validation script:

```bash
#!/bin/bash
# validate.sh

echo "🔍 Validating Terraform configuration..."

# Initialize
terraform init -backend=false

# Validate syntax
terraform validate

# Format code
terraform fmt -recursive

# Generate plan without applying
terraform plan -input=false -out=plan.out

# Show summary
terraform show -json plan.out | jq '.resource_changes | length' | \
  xargs -I {} echo "✅ {} resources would be created"

echo "✨ Validation complete!"
```

## 🛠️ Useful Commands

```bash
# View current state
terraform state list

# View resource details
terraform state show module.ecs.aws_ecs_service.main

# Import existing resources
terraform import module.database.aws_db_instance.main franchise-db

# Destroy infrastructure
terraform destroy

# Destroy specific module
terraform destroy -target=module.cache
```

## 📊 Monitoring

Once deployed, monitor via:

- **CloudWatch Logs**: `/ecs/franchise-api-production`
- **ECS Console**: View tasks and services
- **RDS Metrics**: Performance Insights enabled
- **ALB Metrics**: Requests, latency, errors

## 🔐 Security

- RDS and Redis only accessible from ECS
- ALB is the only public entry point
- Secrets handled as sensitive variables
- Encryption enabled on RDS
- Isolated VPC with private subnets

## 🚨 Troubleshooting

### Error: Insufficient privileges
```bash
# Verify required IAM permissions
aws iam simulate-principal-policy \
  --policy-source-arn $(aws sts get-caller-identity --query Arn --output text) \
  --action-names ec2:CreateVpc rds:CreateDBInstance
```

### Error: Free tier limits
- Verify using t3.micro instances
- Only 750 hours/month in free tier
- RDS free tier: 20GB storage, 20GB backups

### Corrupted state
```bash
# Backup state
cp terraform.tfstate terraform.tfstate.backup

# Refresh state
terraform refresh
```

## 📚 Next Steps

1. Configure custom domain with Route 53
2. Add SSL certificate with ACM
3. Implement CI/CD with GitHub Actions
4. Add WAF for additional security
5. Configure automated backups

---

**Note**: This configuration is optimized for demonstration. For production:
- Enable Multi-AZ on RDS
- Use Redis Cluster mode
- Add more ECS replicas
- Implement VPN or Direct Connect
- Configure disaster recovery Redis**: In-memory cache
- **ALB**: Application Load Balancer
- **CloudWatch**: Logs and monitoring

## 📦 Module Structure

```
terraform/
├── main.tf                 # Main configuration
├── variables.tf            # Global variables
├── terraform.tfvars.example # Example values
├── modules/
│   ├── network/           # VPC, Subnets, IGW, NAT
│   ├── database/          # RDS MySQL
│   ├── cache/             # ElastiCache Redis
│   └── ecs/               # ECS Cluster, Service, ALB
```

## 🔧 Configuration

### 1. Prerequisites

- Terraform >= 1.0
- AWS CLI configured
- AWS credentials with sufficient permissions

### 2. Configure Variables

```bash
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your values
```

### 3. Initialize Terraform

```bash
terraform init
```

### 4. Plan Deployment

```bash
terraform plan
```

### 5. Apply Changes

```bash
terraform apply
```

## 💰 Estimated Costs (Free Tier)

With minimal configuration and Free Tier eligible:

- **RDS** (db.t3.micro): ~$15/month after free tier
- **ElastiCache