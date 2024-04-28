//CI with Packer for Amazon Ami with params for each environment
template = ''' 
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: packer
  name: packer
spec:
  containers:
  - command:
    - sleep
    - "3600"
    image: hashicorp/packer
    imagePullPolicy: Always
    name: packer
    '''

def ami_version =  env.BUILD_NUMBER
properties([
  parameters([
    choice(choices: ['stage ', 'dev ', 'qa', 'prod'], description: 'Pick environment', name: 'env')
    ])
    ])

if (params.env == "stage") {
    region = "us-east-1"
}

else if (params.env == "dev") {
    region = "us-east-2"
}

else if (params.env == "qa") {
    region = "us-west-1"
}

else {
    region = "us-west-2"
}


podTemplate(cloud: 'kubernetes', label: 'packer', yaml: template ) {
    node ("packer") {
    container ("packer") {
    stage ("Checkout SCM") {
         git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git'
    }
    withCredentials([
        usernamePassword(
          credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')
          ]) {
    withEnv(["AWS_REGION=${region}"]) {

    stage ("Packer Build") {
      sh """
      packer version
      packer init ./packer
      packer build -var 'jenkins_build_number=${ami_version}' packer/packer.pkr.hcl
      """
    } 
    }
    }
}
}
}
//temporary change the env var to ami 


