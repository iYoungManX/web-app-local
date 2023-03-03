
module "us-west-2-vpc1" {
  region                = "us-west-2"
  profile               = var.profile
  source                = "./Modules"
  vpc-cidr              = "10.0.0.0/16"
  vpc-tag               = "us-west-2-vpc1"
  public_subnets_num    = 3
  private_subnets_num   = 3
  availability_zones    = ["us-west-2a", "us-west-2b", "us-west-2c"]
  ami-id                = "ami-0d05dd54b0245a553"
  db-username           = "csye6225"
  db-password           = "wu390u3j3b3dn"
  db-name               = "csye6225"
  domain                = "prod.iyoungman.me"
  demo-zone-id          = "Z0930698KXRU4BE9XNRX"
  root-zone-id          = "Z091841033OWE1690O59W"
  dev-zone-id           = "Z00200802VX2E58CJXHXO"
}




# module "us-west-2-vpc2" {
#   region              = "us-west-2"
#   profile             = var.profile
#   source              = "./Modules"
#   vpc-cidr            = "10.1.0.0/16"
#   vpc-tag             = "us-west-2-vpc2"
#   public_subnets_num  = 3
#   private_subnets_num = 3
#   availability_zones  = ["us-west-2a", "us-west-2b", "us-west-2c"]
# }


# module "us-west-2-vpc3" {
#   region              = "us-west-2"
#   profile             = var.profile
#   source              = "./Modules"
#   vpc-cidr            = "10.2.0.0/16"
#   vpc-tag             = "us-west-2-vpc3"
#   public_subnets_num  = 3
#   private_subnets_num = 3
#   availability_zones  = ["us-west-2a", "us-west-2b", "us-west-2c"]
# }




# module "us-west-1-vpc1" {
#   region              = "us-west-1"
#   profile             = var.profile
#   source              = "./Modules"
#   vpc-cidr            = "10.0.0.0/16"
#   vpc-tag             = "us-west-1-vpc"
#   public_subnets_num  = 3
#   private_subnets_num = 3
#   availability_zones  = ["us-west-1c", "us-west-1b", "us-west-1c"]
# }


# module "us-east-1-vpc1" {
#   region              = "us-east-1"
#   profile             = var.profile
#   source              = "./Modules"
#   vpc-cidr            = "10.0.0.0/16"
#   vpc-tag             = "us-east-1-vpc"
#   public_subnets_num  = 3
#   private_subnets_num = 3
#   availability_zones  = ["us-east-1a", "us-east-1b", "us-east-1c"]
# }