
module "us-west-2-vpc1" {
  region              = "us-west-2"
  profile             = var.profile
  source              = "./Modules"
  vpc-cidr            = "10.0.0.0/16"
  vpc-tag             = "us-west-2-vpc1"
  public_subnets_num  = 3
  private_subnets_num = 3
  availability_zones  = ["us-west-2a", "us-west-2b", "us-west-2c"]
#  ami-id              = "ami-038f39e8d0e91d650"
  ami-id =            "ami-0216d05cafe7d23d9"
  db-username         = "csye6225"
  db-password         = "wu390u3j3b3dn"
  db-name             = "csye6225"
  domain              = "prod.iyoungman.me"
  demo-zone-id        = "Z09073211FPNN9I37EC02"
  root-zone-id        = "Z09142753S1FC885ZIEXA"
  dev-zone-id         = "Z0766292TJ20O61HROJW"
  certificate_arn     =  "arn:aws:acm:us-west-2:050297369388:certificate/d1665867-6367-4926-9388-e8a9b0a87cca"
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