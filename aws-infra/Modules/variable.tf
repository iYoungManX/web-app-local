variable "vpc-cidr" {
  default     = "10.0.0.0/16"
  description = "vpc cidr block"
  type        = string
}


variable "availability_zones" {
  default     = ["us-west-2a", "us-west-2b", "us-west-2c"]
  description = "vpc cidr block"
  type        = list(any)
}

variable "vpc-tag" {
  default     = "vpc"
  description = "vpc cidr block"
  type        = string
}


variable "public_subnets_num" {
  default     = 3
  description = "number of public subnets"
  type        = number
}


variable "private_subnets_num" {
  default     = 3
  description = "number of private subnets"
  type        = number
}


variable "profile" {
  type        = string
  description = "profile"
}

variable "region" {
  default     = "us-west-2"
  type        = string
  description = "region"
}

variable "ami-id" {
  default     = "ami-0f1a5f5ada0e7da53"
  type        = string
  description = "ami-id"
}


variable "db-username" {
  default = "root"
  type    = string
}

variable "db-name" {
  default = "csye6225"
  type    = string
}

variable "db-password" {
  default = "youneverknowman123"
  type    = string
}

variable "domain" {
  default = "iyoungman.me"
}

variable "dev-zone-id" {
  default = "iyoungman.me"
}

variable "root-zone-id" {
  default = "iyoungman.me"
}

variable "demo-zone-id" {
  default = "iyoungman.me"
}











