
provider "aws" {
#  alias = "demo"
  region  = var.region
  profile = var.profile
  # credentials = "~/.aws/credentials"
}

provider "aws" {
  alias = "dev"
  region = var.region
  profile = "iYoungManDEV-IAMuser"
}

provider "aws" {
  alias = "root"
  region = var.region
  profile = "root"
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

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}





resource "aws_db_subnet_group" "rds_instance_subnet_group" {
  name = "rds_instance_subnet_group"
  subnet_ids = [aws_subnet.private[0].id,aws_subnet.private[1].id]
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


resource "aws_security_group" "rds_security-group" {
  name_prefix = "rds_security-group"
  vpc_id      = aws_vpc.vpc.id
  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    #    cidr_blocks = [aws_subnet.public[0].cidr_block]
    security_groups = [aws_security_group.ec2-security-group.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
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
  skip_final_snapshot  = true
}





resource "random_uuid" "main" {}


resource "aws_s3_bucket" "private_bucket" {
  bucket = "bucket-${random_uuid.main.result}"
  force_destroy = true
}

resource "aws_s3_bucket_acl" "private_bucket_acl" {
  bucket = aws_s3_bucket.private_bucket.id
  acl = "private"
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




resource "aws_iam_policy" "WebAppS3" {
  name        = "WebAppS3-policy"
  policy      = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = [
          "s3:Get*",
          "s3:List*",
          "s3:PutObject",
          "s3:DeleteObject",
        ]
        Resource = [
          "arn:aws:s3:::${aws_s3_bucket.private_bucket.bucket}",
          "arn:aws:s3:::${aws_s3_bucket.private_bucket.bucket}/*",
        ]
      },
    ]
  })
}



resource "aws_iam_role" "ec2_EC2-CSYE6225role" {
  name = "EC2-CSYE6225"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "webapp_s3_attachment" {
  policy_arn = aws_iam_policy.WebAppS3.arn
  role       = aws_iam_role.ec2_EC2-CSYE6225role.name
}



resource "aws_iam_role_policy_attachment" "ec2_cloudwatch_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
  role       = aws_iam_role.ec2_EC2-CSYE6225role.name
}



resource "aws_iam_instance_profile" "ec2_profile" {
  name = "EC2-CSYE6225-profile"
  role = aws_iam_role.ec2_EC2-CSYE6225role.name
}




resource "aws_instance" "ec2-instance" {
  ami = var.ami-id # Replace with your custom AMI ID
  instance_type = "t2.micro"
  key_name = "yao"
  subnet_id = aws_subnet.public[1].id
  vpc_security_group_ids = [aws_security_group.ec2-security-group.id]
  associate_public_ip_address = true
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
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
      sudo chmod -v 777 /etc/environment
      # Set environment variables for the application
      echo "export DB_PASSWORD=${var.db-password}">> /etc/environment
      echo "export DB_HOST=${aws_db_instance.rds_instance.endpoint}">> /etc/environment
      echo "export DB_NAME=${var.db-name}">> /etc/environment
      echo "export DB_USERNAME=${var.db-username}">> /etc/environment
      echo "export BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}">> /etc/environment
      echo "export REGION=${var.region}">> /etc/environment
      sudo systemctl daemon-reload
      sudo systemctl start myapp.service
      sudo systemctl enable myapp
      sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/deployment/cloudwatch-config.json -s
    EOF
}

output "public_ip" {
  value = aws_instance.ec2-instance.public_ip
}

#resource "aws_route53_zone" "domain" {
#  name = var.domain
#}

// demo
resource "aws_route53_record" "record" {
  name = var.domain
  zone_id = var.demo-zone-id
  type = "A"
  ttl = 300
  records = [aws_instance.ec2-instance.public_ip]

  lifecycle {
    create_before_destroy = true
  }
}
// root
resource "aws_route53_record" "record-root" {
  provider = aws.root
  name = ""
  zone_id = var.root-zone-id
  type = "A"
  ttl = 300
  records = [aws_instance.ec2-instance.public_ip]

  lifecycle {
    create_before_destroy = true
  }
}

// dev
resource "aws_route53_record" "record-dev" {
  provider = aws.dev
  name = ""
  zone_id = var.dev-zone-id
  type = "A"
  ttl = 300
  records = [aws_instance.ec2-instance.public_ip]

  lifecycle {
    create_before_destroy = true
  }
}




