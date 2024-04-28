// pipeline using tfvars credentials secret file 

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


podTemplate(cloud: 'kubernetes', label: 'terraform', yaml: template ) {
    node ("terraform") {
    container ("terraform") {
    stage ("Checkout SCM") {
         git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git'
    }
    stage ("Terraform init") {
        sh "terraform init"
    }
    withCredentials([
        usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID'),
        file(credentialsId: 'tfvars', variable: 'tfvars')
        ]) {
    stage ("Apply") {
        sh "terraform apply -var-file ${tfvars} --auto-approve"
    }
}
}
}
}


