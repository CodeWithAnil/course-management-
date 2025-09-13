locals {
  account_name   = "portal_dev_account"
  aws_account_id = "547568624982"
  aws_region     = "us-west-2"
  app_name       = "plasma-lms-course-service"
  eks_cluster_name = "portal_eks_cluster_dev"
  tags = {
    environment = "dev"
    project = "nt-enterprise-apps"
  }
}