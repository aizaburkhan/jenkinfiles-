node { //Ansible
    stage ("checkout SCM") {
        git branch: 'main', url: 'https://github.com/aizaburkhan/ansible-oct2023.git'
    }
    stage ("Ansible") {
        sh '''
        export ANSIBLE_HOST_KEY_CHECKING=False
        ansible --version
        cd Class4
        ansible -i "167.71.107.253," all -m ping
        '''
    }
}

//issue