properties([
    parameters([
        string(description: '104.131.172.166', name: 'IP', trim: true)])])
node {
    stage("Install java and git") {
       withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSH_PRIVATE_KEY', usernameVariable: 'SSH_USER')]) {
    sh "ssh -o StrictHostKeyChecking=false -i $SSH_PRIVATE_KEY $SSH_USER@${params.IP} yum install java-11-openjdk -y"
    sh "ssh -o StrictHostKeyChecking=false -i $SSH_PRIVATE_KEY $SSH_USER@${params.IP} yum install git -y"
    sh "ssh -o StrictHostKeyChecking=false -i $SSH_PRIVATE_KEY $SSH_USER@${params.IP} yum install epel-release -y"
    sh "ssh -o StrictHostKeyChecking=false -i $SSH_PRIVATE_KEY $SSH_USER@${params.IP} yum install ansible -y"
    }
    }
}

    
