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


variable "profile" {
  type = string
  description = "profile"
}

variable "region" {
  default = "us-west-2"
  type= string
  description = "region"
}

variable "ami-id" {
  default = "ami-0f1a5f5ada0e7da53"
  type= string
  description = "ami-id"
}








