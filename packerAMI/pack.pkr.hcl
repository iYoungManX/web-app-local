variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "source_ami" {
  type    = string
  default = "ami-0f1a5f5ada0e7da53" # Amazon Linux 2 LTS
}


variable "aws_access_key" {
  type    = string
  default = env("AWS_ACCESS_KEY_ID")
}

variable "aws_secret_key" {
  type    = string
  default = env("AWS_SECRET_ACCESS_KEY")
}



source "amazon-ebs" "ami" {
  ami_name        = "webapp-{{timestamp}}"
  ami_description = "AMI for CSYE 6225"
  instance_type   = "t2.micro"
  region          = var.aws_region
  source_ami      = var.source_ami
  access_key      = var.aws_access_key
  secret_key      = var.aws_secret_key
  ssh_username    = var.ssh_username
  ami_users = ["050297369388","435646449189"]

  launch_block_device_mappings {
    device_name = "/dev/xvda"
    volume_size = 8
    volume_type = "gp2"
    delete_on_termination = true
  }
}

build {
  sources = ["source.amazon-ebs.ami"]


  provisioner "shell" {
    script = "setup.sh"
  }

  provisioner "file" {
    source      = "../target/CSYE6225-0.0.1-SNAPSHOT.jar"
    destination = "/opt/deployment/app.jar"
  }

  provisioner "shell" {
    script = "systemd.sh"
  }
}
