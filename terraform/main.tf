# main.tf - Main infrastructure configuration

terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS Provider configuration
provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "franchise-management-api"
      Environment = var.environment
      ManagedBy   = "Terraform"
      Owner       = "jaimerocha"
    }
  }
}

# Network module (VPC)
module "network" {
  source = "./modules/network"
  
  project_name = var.project_name
  environment  = var.environment
  vpc_cidr     = var.vpc_cidr
}

# Database module
module "database" {
  source = "./modules/database"
  
  project_name           = var.project_name
  environment            = var.environment
  db_instance_class      = var.db_instance_class
  db_allocated_storage   = var.db_allocated_storage
  db_name                = var.db_name
  db_username            = var.db_username
  db_password            = var.db_password
  subnet_ids             = module.network.private_subnet_ids
  vpc_id                 = module.network.vpc_id
  allowed_security_group = module.ecs.ecs_security_group_id
}

# Redis cache module
module "cache" {
  source = "./modules/cache"
  
  project_name           = var.project_name
  environment            = var.environment
  node_type              = var.redis_node_type
  subnet_ids             = module.network.private_subnet_ids
  vpc_id                 = module.network.vpc_id
  allowed_security_group = module.ecs.ecs_security_group_id
}

# ECS module for the application
module "ecs" {
  source = "./modules/ecs"
  
  project_name         = var.project_name
  environment          = var.environment
  vpc_id               = module.network.vpc_id
  public_subnet_ids    = module.network.public_subnet_ids
  private_subnet_ids   = module.network.private_subnet_ids
  container_image      = var.container_image
  container_port       = var.container_port
  cpu                  = var.ecs_task_cpu
  memory               = var.ecs_task_memory
  desired_count        = var.ecs_desired_count
  
  # Environment variables for the application
  environment_variables = [
    {
      name  = "SPRING_PROFILES_ACTIVE"
      value = "production"
    },
    {
      name  = "DB_HOST"
      value = module.database.endpoint
    },
    {
      name  = "DB_USERNAME"
      value = var.db_username
    },
    {
      name  = "DB_PASSWORD"
      value = var.db_password
    },
    {
      name  = "REDIS_HOST"
      value = module.cache.endpoint
    }
  ]
}

# Main outputs
output "application_url" {
  description = "Application URL"
  value       = "http://${module.ecs.alb_dns_name}"
}

output "database_endpoint" {
  description = "RDS database endpoint"
  value       = module.database.endpoint
  sensitive   = true
}

output "redis_endpoint" {
  description = "Redis endpoint"
  value       = module.cache.endpoint
  sensitive   = true
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "ECS service name"
  value       = module.ecs.service_name
}