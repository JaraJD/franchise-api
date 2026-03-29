module "vpc" {
  source       = "./modules/vpc"
  project_name = var.project_name
  environment  = var.environment
}

module "ecr" {
  source       = "./modules/ecr"
  project_name = var.project_name
  environment  = var.environment
}

module "security_groups" {
  source       = "./modules/security_groups"
  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.vpc.vpc_id
}

module "ec2" {
  source             = "./modules/ec2"
  project_name       = var.project_name
  environment        = var.environment
  aws_region         = var.aws_region
  subnet_id          = module.vpc.public_subnet_ids[0]
  security_group_id  = module.security_groups.ec2_sg_id
  ecr_repository_url = module.ecr.repository_url
  db_username        = var.db_username
  db_password        = var.db_password
  instance_type      = var.instance_type
  ssh_public_key     = var.ssh_public_key
}
