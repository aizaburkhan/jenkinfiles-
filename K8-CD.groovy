template = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: kubernetes
  name: kubernetes
spec:
  serviceAccount: kubernetes
  containers:
  - image: aizaburkhan/bin:1.0
    name: kubernetes
    '''
properties([
    parameters([
        string(description: 'Enter tag', name: 'tag', trim: true)
        ])
        ])
podTemplate(cloud: 'kubernetes', label: 'kubernetes', yaml: template) {
    node ("kubernetes") {
    container ("kubernetes") {
       stage ("Checkout SCM") {
            git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git' 
        }
       stage ("deploy") {
        sh """
         helm install apache apache/ --set repository.tag=${tag}
         """ // from GitHub
       }
    }
    }
}

// homework- make the tag version pull dynamic with properties. properties([pipelineTriggers([upstream('')])])