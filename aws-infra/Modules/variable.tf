variable "vpc-cidr" {
  default     = "10.0.0.0/16"
  description = "vpc cidr block"
  type        = string
}


variable "availability_zones" {
  default     = ["us-west-2a", "us-west-2b", "us-west-2c"]
  description = "vpc cidr block"
  type        = list
}

variable "vpc-tag" {
  default     = "vpc"
  description = "vpc cidr block"
  type        = string
}


variable "public_subnets_num" {
  default = 3
  description = "number of public subnets"
  type = number
}


variable "private_subnets_num" {
  default = 3
  description = "number of private subnets"
  type = number
}


variable "region" {
  default = "us-west-2"
  type= string
  description = "region"
}

variable "ami_id" {
  type= string
}

variable "aws_access_key" {
  type= string
}
variable "aws_secret_access_key" {
  type= string
}












