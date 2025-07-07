# variables.tf - Variable definitions

variable "aws_region" {
  description = "AWS region where infrastructure will be deployed"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment (dev, staging, production)"
  type        = string
  default     = "production"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "franchise-api"
}

# Network variables
variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# Database variables
variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"  # Free tier eligible
}

variable "db_allocated_storage" {
  description = "Allocated storage in GB"
  type        = number
  default     = 20  # Free tier: up to 20GB
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "franchise_db"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "franchise_user"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

# Redis variables
variable "redis_node_type" {
  description = "Node type for ElastiCache Redis"
  type        = string
  default     = "cache.t3.micro"  # Free tier eligible
}

# ECS variables
variable "container_image" {
  description = "Docker image for container"
  type        = string
  default     = "franchise-management-api:1.0.0"
}

variable "container_port" {
  description = "Container port"
  type        = number
  default     = 8080
}

variable "ecs_task_cpu" {
  description = "CPU for ECS task (in CPU units)"
  type        = string
  default     = "256"  # 0.25 vCPU
}

variable "ecs_task_memory" {
  description = "Memory for ECS task (in MB)"
  type        = string
  default     = "512"
}

variable "ecs_desired_count" {
  description = "Desired number of ECS tasks"
  type        = number
  default     = 1
}