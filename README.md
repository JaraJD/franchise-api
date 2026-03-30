
# Franchise Management API

Reactive REST API to manage a franchise network — built with **Spring WebFlux**, **MongoDB**, and deployed on **AWS EC2** using **Terraform**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 (Spring Framework 7) |
| Reactive | Project Reactor (Mono / Flux) |
| Database | MongoDB 7.0 (ReactiveMongoRepository) |
| Mapping | MapStruct 1.5.5 (compile-time) |
| Validation | Jakarta Validation |
| Documentation | SpringDoc OpenAPI 3 + Swagger UI 5.21 (WebJar) |
| Build | Gradle 9.4 (multi-module) |
| Container | Docker (eclipse-temurin:21-jre-alpine) |

---

## Architecture

This project follows the **Clean Architecture** proposed by Bancolombia's Scaffold:

```
Franchise-api/
│
├── domain/
│   ├── model/                          # Pure Java — zero framework dependencies
│   │   ├── model/franchise/            # Franchise, TopStockProduct
│   │   ├── model/branch/               # Branch
│   │   ├── model/product/              # Product
│   │   ├── model/gateway/              # FranchiseRepository (port interface)
│   │   └── exception/                  # Domain exceptions hierarchy
│   └── usecase/
│       └── franchise/                  # FranchiseUseCase (all business logic)
│
├── infrastructure/
│   ├── driven-adapters/
│   │   └── mongo-repository/
│   │       ├── entity/                 # FranchiseEntity (MongoDB document)
│   │       ├── repository/             # FranchiseMongoRepository (Spring Data)
│   │       ├── mapper/                 # FranchiseEntityMapper (MapStruct)
│   │       ├── adapter/                # FranchiseRepositoryAdapter (implements port)
│   │       └── config/                 # MongoConfig, MongoDBSecret
│   └── entry-points/
│       └── reactive-web/
│           ├── Handler.java            # All request handling
│           ├── RouterRest.java         # RouterFunctions + OpenAPI + Swagger UI route
│           ├── dto/                    # Request/response records
│           ├── mapper/                 # FranchiseResponseMapper (domain → DTO)
│           ├── exception/              # GlobalErrorHandler, RequestValidator
│           └── config/                 # CorsConfig, SecurityHeadersConfig
│
└── applications/app-service/           # Spring Boot entry point
```

### Key design decisions

| Decision | Rationale |
|---|---|
| `record` for domain models | Immutability by default; withers via Lombok `@With` + `toBuilder` |
| RouterFunctions + Handlers | Decouples routing from logic; no `@RestController` |
| MapStruct | Zero-reflection mappers; compile-time safe |
| Domain exceptions hierarchy | Each error has a `code` — translated cleanly in `GlobalErrorHandler` |
| `switchIfEmpty(Mono.error(...))` | Idiomatic reactive 404 handling — no blocking checks |
| Embedded validation | `RequestValidator` wraps `jakarta.validation.Validator` reactively |
| Aggregate root (Franchise) | Franchise owns branches and products; single repository port |

---

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/franchises` | Create a franchise |
| `GET` | `/api/v1/franchises` | List all franchises |
| `PATCH` | `/api/v1/franchises/{franchiseId}` | Update franchise name |
| `POST` | `/api/v1/franchises/{franchiseId}/branches` | Add a branch |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}` | Update branch name |
| `POST` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products` | Add a product |
| `DELETE` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Remove a product |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Update product stock |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Update product name |
| `GET` | `/api/v1/franchises/{franchiseId}/top-stock` | Top-stock product per branch |

**Swagger UI:** `http://localhost:8080/swagger-ui.html`
**API Docs (JSON):** `http://localhost:8080/v3/api-docs`

---

## Running locally

### Option 1 — Docker Compose (recommended)

```bash
# Start MongoDB + API
docker-compose up --build

# API available at http://localhost:8080
# Swagger at http://localhost:8080/swagger-ui.html
```

### Option 2 — Gradle + local MongoDB

```bash
# 1. Start MongoDB with authentication (Docker)
docker-compose up mongodb -d

# 2. Run the application
./gradlew bootRun

# 3. Or build and run the jar
./gradlew bootJar
java -jar applications/app-service/build/libs/Franchise.jar
```

### Environment variables

| Variable | Default | Description |
|---|---|---|
| `MONGODB_URI` | `mongodb://admin:admin123@localhost:27017/franchisedb?authSource=admin` | MongoDB connection string |
| `PORT` | `8080` | Server port |

> **Note:** MongoDB requires authentication. The default credentials match those configured in `docker-compose.yml` (`admin` / `admin123`).

---

## Running tests

```bash
# Unit tests only
./gradlew test

# With coverage report (HTML at build/reports/jacoco/test/html/index.html)
./gradlew jacocoTestReport
```

---

## Deploying to AWS

### AWS Free Tier

This infrastructure is designed to run **within the AWS Free Tier** (~$0/month):

| Service | Usage | Free Tier |
|---|---|---|
| EC2 t2.micro | App + MongoDB in Docker | 750 hrs/month — 12 months |
| ECR | Docker image storage | 500 MB/month |
| VPC / SG / IGW | Networking | Always free |

> Destroy the infrastructure after each session (`terraform destroy`) to preserve free tier hours.

### Infrastructure modules

```
terraform/
├── main.tf                  # Root module — wires all submodules
├── variables.tf             # Input variables
├── outputs.tf               # Outputs (API URL, EC2 IP, ECR URL)
├── providers.tf             # AWS provider + Terraform version >= 1.6
├── terraform.tfvars.example # Example variable values
└── modules/
    ├── vpc/                 # VPC + 2 public subnets + IGW + route tables
    ├── ecr/                 # ECR repository + lifecycle policy (keeps last 5 images)
    ├── security_groups/     # EC2 SG: allow :8080 (API) and :22 (SSH)
    └── ec2/                 # t2.micro + IAM (ECR + SSM) + Docker + auto-deploy loop
```

### CI/CD Pipeline (GitHub Actions)

The deployment is fully automated via two GitHub Actions workflows:

| Workflow | Trigger | Steps |
|---|---|---|
| **CI** (`.github/workflows/ci.yml`) | Pull Request to `main` or `develop` | Build → Test |
| **CD** (`.github/workflows/cd.yml`) | Push to `main` | Build → Test → Push image to ECR → SSH deploy to EC2 |

### GitHub Secrets required

| Secret | Description |
|---|---|
| `AWS_ACCESS_KEY_ID` | IAM user access key |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret key |
| `AWS_REGION` | e.g. `us-east-1` |
| `ECR_REPOSITORY_URL` | From `terraform output ecr_repository_url` |
| `EC2_HOST` | From `terraform output ec2_public_ip` |
| `EC2_SSH_PRIVATE_KEY` | Private key content (generated with `ssh-keygen`) |
| `DB_USERNAME` | MongoDB username (matches `terraform.tfvars`) |
| `DB_PASSWORD` | MongoDB password (matches `terraform.tfvars`) |

### Deployment steps

```bash
# 1. Configure variables
cp terraform/terraform.tfvars.example terraform/terraform.tfvars
# Edit terraform.tfvars with your values

# 2. Provision infrastructure (~2 min)
cd terraform
terraform init
terraform apply

# 3. Copy outputs to GitHub Secrets
terraform output ec2_public_ip      # → EC2_HOST secret
terraform output ecr_repository_url # → ECR_REPOSITORY_URL secret

# 4. Push to main — CI/CD runs automatically
git push origin main
```

### Redeploy after code changes

Simply push to `main` — the CD pipeline handles everything automatically:
1. Builds and tests the application
2. Builds the Docker image and pushes to ECR
3. SSHs into the EC2 and restarts the containers with the new image

### Destroy infrastructure

```bash
cd terraform
terraform destroy
```

### AWS Infrastructure

```
Internet
   │  :8080
   ▼
EC2 t2.micro  (public subnet — free tier)
   ├── franchise-api  (Docker container — Spring WebFlux)
   └── mongodb        (Docker container — MongoDB 7.0)
```

---

## Error handling

All errors return a consistent envelope:

```json
{
  "code": "FRANCHISE_NOT_FOUND",
  "message": "Franchise not found with id: abc123",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

| HTTP Status | Code | Trigger |
|---|---|---|
| `400` | `VALIDATION_ERROR` | Bean validation failure |
| `404` | `FRANCHISE_NOT_FOUND` | Franchise ID not found |
| `404` | `BRANCH_NOT_FOUND` | Branch ID not found |
| `404` | `PRODUCT_NOT_FOUND` | Product ID not found |
| `409` | `DUPLICATE_NAME` | Duplicate franchise name |
| `500` | `INTERNAL_ERROR` | Unexpected server error |

---

## Interview prep topics (from brief)

- **Hexagonal Architecture** — Domain ports (`FranchiseRepository`) implemented by adapters (`FranchiseRepositoryAdapter`). The domain never imports Spring or MongoDB.
- **Reactive programming** — `Mono<T>` for 0-1 items; `Flux<T>` for 0-N streams. Operators: `flatMap`, `switchIfEmpty`, `map`, `flatMapMany`.
- **Mono vs Flux** — `Mono` = 0 or 1 element (like `Optional` but async). `Flux` = 0 to N elements (like `Stream` but async and backpressure-aware).
- **SOLID** — Single Responsibility (use case per domain), Open/Closed (new adapters without changing domain), Dependency Inversion (domain defines the port; infra implements it).
- **Functional programming** — Immutable records, pure functions, no side effects in domain model.
- **Terraform** — Modules for reusability; `variables.tf` for parameterization; `outputs.tf` for cross-module references; `backend "s3"` for remote state.
- **AWS** — ECR (image registry), EC2 (virtual machine — t2.micro free tier), IAM roles (least-privilege access), VPC + Security Groups (network isolation), SSH key pairs for secure access.
