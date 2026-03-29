#!/bin/bash
set -e
exec > /var/log/user-data.log 2>&1

echo "=== Starting setup at $(date) ==="

# System update and Docker install
dnf update -y
dnf install -y docker
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# Ensure SSM agent is running
systemctl enable amazon-ssm-agent
systemctl start amazon-ssm-agent

# Docker Compose plugin (system-wide)
mkdir -p /usr/local/lib/docker/cli-plugins
curl -sSL \
  "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-linux-x86_64" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

echo "=== Docker installed ==="

# App directory
mkdir -p /opt/franchise-api

# Write docker-compose.yml with values injected by Terraform
cat > /opt/franchise-api/docker-compose.yml << 'COMPOSE'
version: '3.9'
services:
  mongodb:
    image: mongo:7.0
    container_name: franchise-mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${db_username}
      MONGO_INITDB_ROOT_PASSWORD: ${db_password}
      MONGO_INITDB_DATABASE: franchisedb
    volumes:
      - mongo-data:/data/db

  franchise-api:
    image: ${ecr_repository_url}:latest
    container_name: franchise-api
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      MONGODB_URI: "mongodb://${db_username}:${db_password}@mongodb:27017/franchisedb?authSource=admin"
      PORT: "8080"
    depends_on:
      - mongodb

volumes:
  mongo-data:
    driver: local
COMPOSE

# Write deploy script
cat > /opt/franchise-api/deploy.sh << 'DEPLOY'
#!/bin/bash
set -e
REGION="${aws_region}"
ECR_HOST=$(echo "${ecr_repository_url}" | cut -d'/' -f1)
echo "Logging into ECR..."
aws ecr get-login-password --region "$REGION" | \
  docker login --username AWS --password-stdin "$ECR_HOST"
echo "Pulling image..."
docker compose -f /opt/franchise-api/docker-compose.yml pull franchise-api
echo "Starting services..."
docker compose -f /opt/franchise-api/docker-compose.yml up -d
echo "Done at $(date)"
DEPLOY
chmod +x /opt/franchise-api/deploy.sh

# Start MongoDB immediately
echo "=== Starting MongoDB ==="
docker compose -f /opt/franchise-api/docker-compose.yml up -d mongodb

# Background loop: try to deploy app every 30s until ECR image is available
echo "=== Starting auto-deploy loop ==="
(
  while true; do
    if /opt/franchise-api/deploy.sh >> /var/log/franchise-deploy.log 2>&1; then
      echo "=== App deployed at $(date) ===" >> /var/log/franchise-deploy.log
      break
    fi
    echo "Image not ready, retrying in 30s..." >> /var/log/franchise-deploy.log
    sleep 30
  done
) &

echo "=== Setup complete at $(date) ==="
