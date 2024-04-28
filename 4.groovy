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



tfvars = '''
region="us-east-2"
ami_id="ami-019f9b3318b7155c5"
az="us-east-2a"
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
    writeFile file: 'hello.tfvars', text: tfvars
    stage ("Apply") {
        sh "terraform apply -var-file hello.tfvars --auto-approve"
    }
    stage ("Destroy") {
        sh "terraform destroy -var-file hello.tfvars --auto-approve"
    }
}
}
}
}
//withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
// template is the env var and we call in the podtemplate. 
// must have aws credentials to apply tf 
// node name shall be terraform as the pod name otherwise it picks the default one