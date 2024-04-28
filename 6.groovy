template = ''' 
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: terraform
  name: terraform
spec:
  containers:
  - command:
    - sleep
    - "3600"
    image: hashicorp/terraform
    imagePullPolicy: Always
    name: terraform
    '''



tfvars = """
region="${params.region}"
ami_id="${params.ami_id}"
az="${params.az}"
"""
// when you call params inside an env var use "" instead of ''' ''' otherwise it will not see the params 
properties([
    parameters([
        choice(choices: ['apply', 'destroy'], description: 'Pick the action', name: 'action'), // action is the var
        choice(choices: ['us-east-1', 'us-east-2', 'us-west-1', 'us-west-2'], description: 'Pick a region where you want to deploy this application ', name: 'region'), 
        string(description: 'Enter ami id', name: 'ami_id', trim: true), 
        string(description: 'Enter az', name: 'az', trim: true)
        ])
        ])

podTemplate(cloud: 'kubernetes', label: 'terraform', yaml: template ) {
    node ("terraform") {
    container ("terraform") {
    stage ("Checkout SCM") {
         git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git'
    }

    withCredentials([
        usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')
        ]) {
    stage ("Terraform init") {
        sh "terraform init -backend-config='key=${params.region}/${params.az}/terraform.tfstate'" // added to  modify the statefile in github- rename the statefile to us-east-1 and it creates a folder with tfstate file when applied on S3.
    }
    
    writeFile file: 'hello.tfvars', text: tfvars

    if (params.action == "apply") {
        stage ("Apply") {
        sh "terraform apply -var-file hello.tfvars --auto-approve"
    }
    }
    else {
        stage ("Destroy") {
        sh "terraform destroy -var-file hello.tfvars --auto-approve"
    }
    }
}
}
}
}
// make sure default vpc exists when deplopying in diff regions. us-west-1 has no az. 
//withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
// template is the env var and we call in the podtemplate. 
// must have aws credentials to apply tf 
// node name shall be terraform as the pod name otherwise it picks the default one