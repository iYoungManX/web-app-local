init:
	terraform init

dev-plan:
	terraform plan -var-file dev.tfvars

demo-plan:
	terraform plan -var-file demo.tfvars

root-plan:
	terraform plan -var-file root.tfvars

dev-apply:
	terraform apply -var-file dev.tfvars

demo-apply:
	terraform apply -var-file demo.tfvars

root-apply:
	terraform apply -var-file root.tfvars

dev-destroy:
	terraform destroy -var-file dev.tfvars

demo-destroy:
	terraform destroy -var-file demo.tfvars


root-destroy:
	terraform destroy -var-file root.tfvars
format:
	terraform fmt



clean:
	rm terraform.tfstate.**
	rm terraform.tfstate

full-clean:
	rm -r .terraform
	rm .terraform.**
	rm terraform.tfstate.**
	rm terraform.tfstate



