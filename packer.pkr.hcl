packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = "~> 1"
    }
  }
}

variable "aws_access_key" {
  type    = string
  default = env("AWS_ACCESS_KEY_ID")
}

variable "aws_secret_key" {
  type    = string
  default = env("AWS_SECRET_ACCESS_KEY")
}

variable "aws_region" {
  type    = string
  default = env("AWS_REGION")
}

variable "source_ami_filter" {
  type    = string
  default = "amzn2-ami-hvm-*-gp2"
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "jenkins_build_number" {
  type    = string
  default = "unknown"
}
source "amazon-ebs" "example" {
  access_key    = var.aws_access_key
  secret_key    = var.aws_secret_key
  region        = var.aws_region
  instance_type = var.instance_type
  ssh_username  = "ec2-user"
  ami_name = "my-ami-${var.jenkins_build_number}"

  source_ami_filter {
    filters = {
      name               = var.source_ami_filter
      "virtualization-type" = "hvm"
    }
    owners = ["137112412989"]
    most_recent = true
  }
}

build {
  sources = [
    "source.amazon-ebs.example"
  ]

  provisioner "shell" {
    inline = [
      "sudo yum install httpd -y",
      "sudo systemctl start httpd",
      "sudo systemctl enable httpd"
    ]
  }
}