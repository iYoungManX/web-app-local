provider "aws" {
  #  alias = "demo"
  region  = var.region
  profile = var.profile
  # credentials = "~/.aws/credentials"
}

provider "aws" {
  alias   = "dev"
  region  = var.region
  profile = "iYoungManDEV-IAMuser"
}

provider "aws" {
  alias   = "root"
  region  = var.region
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
  count             = var.public_subnets_num
  cidr_block        = cidrsubnet(var.vpc-cidr, 8, count.index + 1)
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
  count          = 3
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}
# Create  3 private subnet
resource "aws_subnet" "private" {
  count             = 3
  cidr_block        = cidrsubnet(var.vpc-cidr, 8, count.index + var.public_subnets_num + 1)
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
  count          = var.private_subnets_num
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}


resource "aws_security_group" "ec2-security-group" {
  name_prefix = "My SG"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    #    security_groups = [aws_security_group.load_balancer_sg.id]
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.load_balancer_sg.id]
  }

  #  ingress {
  #    from_port       = 8080
  #    to_port         = 8080
  #    protocol        = "tcp"
  #    cidr_blocks = ["0.0.0.0/0"]
  #  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}





resource "aws_db_subnet_group" "rds_instance_subnet_group" {
  name       = "rds_instance_subnet_group"
  subnet_ids = [aws_subnet.private[0].id, aws_subnet.private[1].id]
}

resource "aws_db_parameter_group" "mysql" {
  name_prefix = "mysql"
  family      = "mysql8.0"
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
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
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

resource "aws_kms_key" "rds_kms_key" {
  description = "Example customer-managed KMS key"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement =[
      {
        "Sid": "Enable IAM User Permissions",
        "Effect": "Allow",
        "Principal": {
          "AWS": ["arn:aws:iam::050297369388:root",aws_iam_role.ec2_EC2-CSYE6225role.arn]
        },
        "Action": "kms:*",
        "Resource": "*"
      },
      {
        "Sid" : "Enable kms access for auto scaling",
        "Effect" : "Allow",
        "Principal" : {
          "AWS" : "arn:aws:iam::050297369388:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling"
        },
        "Action" : "kms:*",
        "Resource" : "*",
      }

    ]
  })
}


resource "aws_db_instance" "rds_instance" {
  engine                 = "mysql"
  engine_version         = "8.0.23"
  instance_class         = "db.t3.micro"
  allocated_storage      = 5
  identifier             = "csye6225"
  username               = var.db-username
  password               = var.db-password
  multi_az               = false
  publicly_accessible    = false
  db_name                = var.db-name
  db_subnet_group_name   = aws_db_subnet_group.rds_instance_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_security-group.id]
  parameter_group_name   = aws_db_parameter_group.mysql.name
  skip_final_snapshot    = true
  storage_encrypted = true
  kms_key_id = aws_kms_key.rds_kms_key.arn
}





resource "random_uuid" "main" {}


resource "aws_s3_bucket" "private_bucket" {
  bucket        = "bucket-${random_uuid.main.result}"
  force_destroy = true
}

resource "aws_s3_bucket_acl" "private_bucket_acl" {
  bucket = aws_s3_bucket.private_bucket.id
  acl    = "private"
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
    id     = "transition-to-standard-ia"
    status = "Enabled"
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
  bucket = aws_s3_bucket.private_bucket.id
}




resource "aws_iam_policy" "WebAppS3" {
  name = "WebAppS3-policy"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
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




#resource "aws_instance" "ec2-instance" {
#  ami                         = var.ami-id # Replace with your custom AMI ID
#  instance_type               = "t2.micro"
#  key_name                    = "yao"
#  subnet_id                   = aws_subnet.public[0].id
#  vpc_security_group_ids      = [aws_security_group.ec2-security-group.id]
#  associate_public_ip_address = true
#  iam_instance_profile        = aws_iam_instance_profile.ec2_profile.name
#  root_block_device {
#    volume_size           = 8
#    volume_type           = "gp2"
#    delete_on_termination = true
#  }
#  tags = {
#    Name = "ec2-instance"
#  }
#
#  user_data = <<-EOF
#      #!/bin/bash
#      sudo chmod -v 777 /etc/environment
#      # Set environment variables for the application
#      echo "DB_PASSWORD=${var.db-password}">> /etc/environment
#      echo "DB_HOST=${aws_db_instance.rds_instance.endpoint}">> /etc/environment
#      echo "DB_NAME=${var.db-name}">> /etc/environment
#      echo "DB_USERNAME=${var.db-username}">> /etc/environment
#      echo "BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}">> /etc/environment
#      echo "REGION=${var.region}">> /etc/environment
#      sudo systemctl daemon-reload
#      sudo systemctl start myapp.service
#      sudo systemctl enable myapp
#      sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/deployment/cloudwatch-config.json -s
#    EOF
#}



#resource "aws_route53_zone" "domain" {
#  name = var.domain
#}





## load balancer security groups
resource "aws_security_group" "load_balancer_sg" {
  name_prefix = "load-balancer-sg-"
  description = "Security group for the load balancer to access the web application"
  vpc_id      = aws_vpc.vpc.id
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

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}



resource "aws_lb" "load_balancer" {
  name               = "lb"
  internal           = false
  load_balancer_type = "application"

  security_groups = [
    aws_security_group.load_balancer_sg.id,
  ]
  subnets = [
    aws_subnet.public[0].id,aws_subnet.public[1].id,aws_subnet.public[2].id
  ]

  tags = {
    Name = "load_balancer"
  }
}


## DNS validation for demo
data "aws_route53_zone" "demo" {
  name         = "prod.iyoungman.me"
}

resource "aws_acm_certificate" "certificate" {
  domain_name       = "prod.iyoungman.me"
  validation_method = "DNS"
  tags = {
    Environment = "production"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "validation" {
  allow_overwrite = true
  name    = element(aws_acm_certificate.certificate.domain_validation_options[*].resource_record_name, 0)
  type    = element(aws_acm_certificate.certificate.domain_validation_options[*].resource_record_type, 0)
  records = [element(aws_acm_certificate.certificate.domain_validation_options[*].resource_record_value, 0)]
  zone_id = data.aws_route53_zone.demo.zone_id
  ttl     = 60
}



resource "aws_acm_certificate_validation" "valid" {
  certificate_arn         = aws_acm_certificate.certificate.arn
  validation_record_fqdns = aws_route53_record.validation.*.fqdn
}


## DNS validation for dev
data "aws_route53_zone" "dev" {
  provider = aws.dev
  name         = "dev.iyoungman.me"
}

resource "aws_acm_certificate" "certificate-dev" {
  provider = aws.dev
  domain_name       = "dev.iyoungman.me"
  validation_method = "DNS"
  tags = {
    Environment = "production"
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "validation-dev" {
  provider = aws.dev
  allow_overwrite = true
  name    = element(aws_acm_certificate.certificate-dev.domain_validation_options[*].resource_record_name, 0)
  type    = element(aws_acm_certificate.certificate-dev.domain_validation_options[*].resource_record_type, 0)
  records = [element(aws_acm_certificate.certificate-dev.domain_validation_options[*].resource_record_value, 0)]
  zone_id = data.aws_route53_zone.dev.zone_id
  ttl     = 60
}


resource "aws_acm_certificate_validation" "valid-dev" {
  provider = aws.dev
  certificate_arn         = aws_acm_certificate.certificate-dev.arn
  validation_record_fqdns = aws_route53_record.validation-dev.*.fqdn
}




## DNS validation for root
data "aws_route53_zone" "root" {
  provider = aws.root
  name         = "iyoungman.me"
}

resource "aws_acm_certificate" "certificate-root" {
  provider = aws.root
  domain_name       = "iyoungman.me"
  validation_method = "DNS"
  tags = {
    Environment = "production"
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "validation-root" {
  provider = aws.root
  allow_overwrite = true
  name    = element(aws_acm_certificate.certificate-root.domain_validation_options[*].resource_record_name, 0)
  type    = element(aws_acm_certificate.certificate-root.domain_validation_options[*].resource_record_type, 0)
  records = [element(aws_acm_certificate.certificate-root.domain_validation_options[*].resource_record_value, 0)]
  zone_id = data.aws_route53_zone.root.zone_id
  ttl     = 60
}



resource "aws_acm_certificate_validation" "valid-root" {
  provider = aws.root
  certificate_arn         = aws_acm_certificate.certificate-root.arn
  validation_record_fqdns = aws_route53_record.validation-root.*.fqdn
}



#
#resource "aws_lb_listener" "aws_lb_listeners-dev" {
#  provider = aws.dev
#  load_balancer_arn = aws_lb.load_balancer.arn
#  port              = "443"
#  protocol          = "HTTPS"
#  ssl_policy        = "ELBSecurityPolicy-2016-08"
#  certificate_arn   = aws_acm_certificate_validation.valid-dev.certificate_arn
#  default_action {
#    type             = "forward"
#    target_group_arn = aws_lb_target_group.ls_target_group.arn
#  }
#}

resource "aws_lb_listener" "aws_lb_listeners-demo" {
  load_balancer_arn = aws_lb.load_balancer.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  #  certificate_arn   = aws_acm_certificate_validation.valid.certificate_arn
  certificate_arn = var.certificate_arn
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.lb_target_group.arn
  }
}
#
#resource "aws_lb_listener" "aws_lb_listeners-root" {
#  provider = aws.root
#  load_balancer_arn = aws_lb.load_balancer.arn
#  port              = "443"
#  protocol          = "HTTPS"
#  ssl_policy        = "ELBSecurityPolicy-2016-08"
#  certificate_arn   = aws_acm_certificate_validation.valid-root.certificate_arn
#  default_action {
#    type             = "forward"
#    target_group_arn = aws_lb_target_group.ls_target_group.arn
#  }
#}

resource "aws_lb_listener" "aws_lb_listeners-80" {
  load_balancer_arn = aws_lb.load_balancer.arn
  port              = "80"
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.lb_target_group.arn
  }
}


resource "aws_kms_key" "ec2_ebs_key" {
  description = "customer-managed KMS key"
  # Allow the IAM user or role specified in the "principal" block to use the key
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        "Sid": "Enable IAM User Permissions",
        "Effect": "Allow",
        "Principal": {
          "AWS": ["arn:aws:iam::050297369388:root",aws_iam_role.ec2_EC2-CSYE6225role.arn]
        },
        "Action": "kms:*",
        "Resource": "*"
      },
      {
        "Sid" : "Enable kms access for auto scaling",
        "Effect" : "Allow",
        "Principal" : {
          "AWS" : "arn:aws:iam::050297369388:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling"
        },
        "Action" : "kms:*",
        "Resource" : "*",
      }
    ]
  })
}



#resource "aws_launch_configuration" "launch_config" {
#  name_prefix                 = "launch_config"
#  image_id                    = var.ami-id
#  instance_type               = "t2.micro"
#  security_groups             = [aws_security_group.ec2-security-group.id]
#  associate_public_ip_address = true
#  key_name                    = "yao"
#  iam_instance_profile        = aws_iam_instance_profile.ec2_profile.name
#
#    ebs_block_device {
#      device_name = "/dev/sdf"
#      volume_size = 5
#      volume_type = "gp2"
#      encrypted   = true
#      kms_key_id  = aws_kms_key.ec2_ebs_key.key_id
#    }
#
#
#  user_data = <<-EOF
#      #!/bin/bash
#      sudo chmod -v 777 /etc/environment
#      # Set environment variables for the application
#      echo "DB_PASSWORD=${var.db-password}">> /etc/environment
#      echo "DB_HOST=${aws_db_instance.rds_instance.endpoint}">> /etc/environment
#      echo "DB_NAME=${var.db-name}">> /etc/environment
#      echo "DB_USERNAME=${var.db-username}">> /etc/environment
#      echo "BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}">> /etc/environment
#      echo "REGION=${var.region}">> /etc/environment
#      sudo systemctl daemon-reload
#      sudo systemctl start myapp.service
#      sudo systemctl enable myapp
#      sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/deployment/cloudwatch-config.json -s
#    EOF
#
#  lifecycle {
#    create_before_destroy = true
#  }
#}


data "template_file" "user_data" {
  template = <<-EOF
    #!/bin/bash
      sudo chmod -v 777 /etc/environment
      # Set environment variables for the application
      echo "DB_PASSWORD=${var.db-password}">> /etc/environment
      echo "DB_HOST=${aws_db_instance.rds_instance.endpoint}">> /etc/environment
      echo "DB_NAME=${var.db-name}">> /etc/environment
      echo "DB_USERNAME=${var.db-username}">> /etc/environment
      echo "BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}">> /etc/environment
      echo "REGION=${var.region}">> /etc/environment
      sudo systemctl daemon-reload
      sudo systemctl start myapp.service
      sudo systemctl enable myapp
      sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/deployment/cloudwatch-config.json -s
  EOF
}




resource "aws_launch_template" "launch_config" {
  image_id      = var.ami-id
  instance_type = "t2.micro"
  key_name      =  "yao"
  name = "launch_config"

  iam_instance_profile {
    name = aws_iam_instance_profile.ec2_profile.name
  }

  network_interfaces {
    associate_public_ip_address = true
    #    subnet_id =  aws_subnet.public[0].id
    security_groups = [aws_security_group.ec2-security-group.id]
  }
  user_data = base64encode(data.template_file.user_data.rendered)

  block_device_mappings {
    device_name = "/dev/xvda"
    #     device_name = "/dev/sda1"
    ebs {
      delete_on_termination = true
      volume_type = "gp2"
      volume_size = 8
      encrypted   = true
      kms_key_id  = aws_kms_key.ec2_ebs_key.arn
    }
  }
}




resource "aws_lb_target_group" "lb_target_group" {
  name_prefix = "tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.vpc.id
  target_type = "instance"
  load_balancing_algorithm_type = "round_robin"
  health_check {
    #    enabled             = true
    #    port                = 8080
    #    interval            = 30
    #    protocol            = "HTTP"
    path                = "/healthz"
    #    matcher             = "200"
    #    healthy_threshold   = 3
    #    unhealthy_threshold = 3
  }
}

resource "aws_autoscaling_group" "autoscaling" {
  name                      = "asg"
  #  launch_configuration      = aws_launch_configuration.launch_config.id
  min_size                  = 1
  max_size                  = 3
  desired_capacity          = 1
  #  health_check_grace_period = 300
  health_check_type         = "EC2"
  default_cooldown          = 60

  vpc_zone_identifier = [
    aws_subnet.public[0].id,aws_subnet.public[1].id,aws_subnet.public[2].id
  ]
  target_group_arns = [
    aws_lb_target_group.lb_target_group.arn
  ]
  launch_template {
    id = aws_launch_template.launch_config.id
    version = "$Latest"
  }

}


resource "aws_autoscaling_policy" "scale_up_policy" {
  name                   = "scale_up"
  autoscaling_group_name = aws_autoscaling_group.autoscaling.name
  adjustment_type        = "ChangeInCapacity"
  scaling_adjustment     = 1
  cooldown               = 60
}

resource "aws_cloudwatch_metric_alarm" "scale_up_alarm" {
  alarm_description   = "Monitors CPU utilization for Terramino ASG"
  alarm_actions       = [aws_autoscaling_policy.scale_up_policy.arn]
  alarm_name          = "cpu_utilization_scale_up_alarm"
  comparison_operator = "GreaterThanThreshold"
  namespace           = "AWS/EC2"
  metric_name         = "CPUUtilization"
  threshold           = 5
  evaluation_periods  = 1
  period              = "60"
  statistic           = "Average"
  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.autoscaling.name
  }
}

resource "aws_autoscaling_policy" "scale_down_policy" {
  name                   = "scale_down"
  autoscaling_group_name = aws_autoscaling_group.autoscaling.name
  adjustment_type        = "ChangeInCapacity"
  scaling_adjustment     = -1
  cooldown               = 60
}

resource "aws_cloudwatch_metric_alarm" "scale_down_alarm" {
  alarm_description   = "Monitors CPU utilization for Terramino ASG"
  alarm_actions       = [aws_autoscaling_policy.scale_down_policy.arn]
  alarm_name          = "cpu_utilization_scale_down_alarm"
  comparison_operator = "LessThanThreshold"
  namespace           = "AWS/EC2"
  metric_name         = "CPUUtilization"
  threshold           = 3
  evaluation_periods  = 1
  period              = "60"
  statistic           = "Average"
  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.autoscaling.name
  }
}






// demo
resource "aws_route53_record" "record" {
  name    = var.domain
  zone_id = var.demo-zone-id
  type    = "A"
  alias {
    name                   = aws_lb.load_balancer.dns_name
    zone_id                = aws_lb.load_balancer.zone_id
    evaluate_target_health = true
  }
  lifecycle {
    create_before_destroy = true
  }
}
// root
resource "aws_route53_record" "record-root" {
  provider = aws.root
  name     = ""
  zone_id  = var.root-zone-id
  type     = "A"
  alias {
    name                   = aws_lb.load_balancer.dns_name
    zone_id                = aws_lb.load_balancer.zone_id
    evaluate_target_health = true
  }

  lifecycle {
    create_before_destroy = true
  }
}

// dev
resource "aws_route53_record" "record-dev" {
  provider = aws.dev
  name     = ""
  type     = "A"
  zone_id  = var.dev-zone-id
  alias {
    name                   = aws_lb.load_balancer.dns_name
    zone_id                = aws_lb.load_balancer.zone_id
    evaluate_target_health = true
  }
  lifecycle {
    create_before_destroy = true
  }
}
