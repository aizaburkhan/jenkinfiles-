node {
    stage ("Checkout SCM") {
        git branch: 'main', url: 'https://github.com/aizaburkhan/terraform-october-2023.git'
    }
    stage ("Terraform init") {
        sh '''
        cd Class2
        terraform init
        '''
    }
    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
     stage ("Terraform Apply") {
        sh '''
        cd Class2
        ls
        terraform apply --auto-approve
        '''
    }
    stage ('Terraform destroy')
        sh '''
        cd Class2
        ls
        terraform destroy --auto-approve
        '''
    }
}