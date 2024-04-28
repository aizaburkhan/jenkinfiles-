// this is the CD pipeline called kubernetes
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
podTemplate(cloud: 'kubernetes', label: 'kubernetes', yaml: template) {
    node ("kubernetes") {
    container ("kubernetes") {
       stage ("Checkout SCM") {
            git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git' 
        }
       stage ("version") {
        sh """
        terraform version
        helm version
        kubectl version
        kubectl get nodes
        """
       }
       stage ("Apply file pod.yaml") {
        sh "kubectl apply -f pod.yaml"
       }
    }
    }
}
