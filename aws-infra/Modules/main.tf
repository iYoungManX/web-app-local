
provider "aws" {
  region  = var.region
  profile = var.profile
  # credentials = "~/.aws/credentials"
}

# Create VPC
# terraform aws create vpc
resource "aws_vpc" "vpc" {
  cidr_block           = var.vpc-cidr
  instance_tenancy     = "default"
  enable_dns_hostnames = true
  tags = {
    Name = var.vpc-tag
  }
}



# Create Internet Gateway and Attach it to VPC
# terraform aws create internet gateway
resource "aws_internet_gateway" "internet-gateway" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    Name = "Integer Gateway"
  }
}

# Create 3 public Subnet
resource "aws_subnet" "public" {
  count = var.public_subnets_num
  cidr_block        = cidrsubnet(var.vpc-cidr,8,count.index+1)
  vpc_id            = aws_vpc.vpc.id
  availability_zone = var.availability_zones[count.index]
  tags = {
    Name = "subnet-public-${count.index + 1}"
  }
}




# Create public route table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet-gateway.id
  }
  tags = {
    Name = "route-table-public"
  }
}

# Create public route_table asscoation
resource "aws_route_table_association" "public" {
  count =3 
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}
# Create  3 private subnet 
resource "aws_subnet" "private" {
  count = 3
  cidr_block        = cidrsubnet(var.vpc-cidr,8,count.index+var.public_subnets_num+1)
  vpc_id            = aws_vpc.vpc.id
  availability_zone = var.availability_zones[count.index]
  tags = {
    Name = "subnet-private-${count.index + 1}"
  }
}

# Create private route table
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    Name = "route-private"
  }
}
# Create private route association
resource "aws_route_table_association" "private" {
  count          =  var.private_subnets_num
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}


resource "aws_security_group" "ec2-security-group" {
  name_prefix = "My SG"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

#  egress {
#    from_port = 3306
#    to_port = 3306
#    protocol = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#    security_groups = [aws_security_group.rds_security-group.id]
#  }
}

resource "aws_instance" "ec2-instance" {
  ami = var.ami-id # Replace with your custom AMI ID
  instance_type = "t2.micro"
  key_name = "yao"
  subnet_id = aws_subnet.public[0].id
  vpc_security_group_ids = [aws_security_group.ec2-security-group.id]
  associate_public_ip_address = true
  root_block_device {
    volume_size = 8
    volume_type = "gp2"
    delete_on_termination = true
  }
  tags = {
    Name = "ec2-instance"
  }

  user_data = <<-EOF
    #!/bin/bash
    # Set environment variables for the application
    export DB_HOST=${aws_db_instance.rds_instance.endpoint}
    exprot DB_NAME=${var.db-name}
    export DB_USERNAME=${var.db-username}
    export DB_PASSWORD=${var.db-password}
    export BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}
    sudo systemctl restart myapp
  EOF
}

resource "aws_security_group" "rds_security-group" {
  name_prefix = "rds_security-group"
  vpc_id      = aws_vpc.vpc.id
  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    security_groups = [aws_security_group.ec2-security-group.id]
  }
}


resource "aws_db_subnet_group" "rds_instance_subnet_group" {
  name = "rds_instance_subnet_group"
  subnet_ids = [aws_subnet.private[0].id,
                aws_subnet.private[1].id]
}

resource "aws_db_parameter_group" "mysql" {
  name_prefix = "mysql"
  family = "mysql8.0"
  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }
  # Add more parameters as desired
}


resource "aws_db_instance" "rds_instance" {
  engine               = "mysql"
  engine_version       = "8.0.23"
  instance_class       = "db.t3.micro"
  allocated_storage    = 5
  identifier           = "csye6225"
  username             = var.db-username
  password             = var.db-password
  multi_az             = false
  publicly_accessible  = false
  db_name              = var.db-name
  db_subnet_group_name = aws_db_subnet_group.rds_instance_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_security-group.id]
  parameter_group_name = aws_db_parameter_group.mysql.name
}

#resource "aws_security_group_rule" "allow_db_access" {
#  type        = "ingress"
#  from_port   = 3306
#  to_port     = 3306
#  protocol    = "tcp"
#  security_group_id = aws_security_group.rds_security-group.id
#  source_security_group_id = aws_security_group.ec2-security-group.id
#}



resource "random_pet" "bucket_name" {
  length = 4
}


resource "aws_s3_bucket" "private_bucket" {
  bucket = "bucket-${random_pet.bucket_name.id}"
}




resource "aws_s3_bucket_server_side_encryption_configuration" "private_bucket_encryption" {
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }

  bucket = aws_s3_bucket.private_bucket.id
}

resource "aws_s3_bucket_lifecycle_configuration" "private_bucket_lifecycle" {
  rule {
    id      = "transition-to-standard-ia"
    status  = "Enabled"
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
  bucket = aws_s3_bucket.private_bucket.id
}

resource "aws_s3_bucket_policy" "private_bucket_policy" {
  bucket = aws_s3_bucket.private_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid = "DenyUnEncryptedObjectUploads"
        Effect = "Deny"
        Principal = "*"
        Action = "s3:PutObject"
        Resource = "${aws_s3_bucket.private_bucket.arn}/*"
        Condition = {
          StringNotEquals = {
            "s3:x-amz-server-side-encryption": "AES256"
          }
        }
      },
      {
        Sid = "DenyPublicAccess"
        Effect = "Deny"
        Principal = "*"
        Action = "s3:*"
        Resource = "${aws_s3_bucket.private_bucket.arn}/*"
        Condition = {
          Bool = {
            "aws:SecureTransport": false
          }
        }
      }
    ]
  })
}



output "public_ip" {
  value = aws_instance.ec2-instance.public_ip
}



