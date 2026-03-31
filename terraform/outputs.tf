output "api_base_url" {
  description = "Public URL of the API"
  value       = "http://${module.ec2.public_ip}:8080"
}

output "swagger_ui_url" {
  description = "Swagger UI URL"
  value       = "http://${module.ec2.public_ip}:8080/swagger-ui.html"
}

output "ec2_public_ip" {
  description = "EC2 instance public IP"
  value       = module.ec2.public_ip
}

output "ec2_instance_id" {
  description = "EC2 instance ID (for SSM access)"
  value       = module.ec2.instance_id
}

output "ecr_repository_url" {
  description = "ECR repository URL for pushing Docker images"
  value       = module.ecr.repository_url
}
