# aws-infra

### to run the service

```
make init


make dev-plan
make dev-apply
make dev-destroy
or 
make demo-plan
make demo-apply
make demo-destroy
```





### the AWS Cloudformation Service Provider

to start a service

```
aws cloudformation create-stack --stack-name vpc --template-body file://cloudformation.yaml
```


to destroy a service

```
aws cloudformation delete-stack --stack-name vpc
```
