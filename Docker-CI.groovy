//this is the CI pipeline docker 
template = ''' 
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: docker
  name: docker
spec:
  containers:
  - command:
    - sleep
    - "3600"
    image: docker
    imagePullPolicy: Always
    name: docker
    volumeMounts: 
    - mountPath: /var/run/docker.sock
      name: docker
  volumes: 
  - name: docker
    hostPath: 
      path: /var/run/docker.sock
    '''
def version = env.BUILD_NUMBER

podTemplate(cloud: 'kubernetes', label: 'docker', yaml: template ) {
    node ("docker") {
    container ("docker") {

      stage ("Docker check") {
        sh "docker version"
      }
      stage ("Checkout SCM") {
        git branch: 'main', url: 'https://github.com/aizaburkhan/jenkins-oct-terraform.git' 
      }
      withCredentials([usernamePassword(credentialsId: 'docker-creds', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')
      ]) {
        stage ("Docker build") {
          sh "docker build -t ${DOCKER_USER}/hello-world:${version}.0 ./docker" 
        }
        stage ("Docker push") {
          sh """
            docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
            docker push ${DOCKER_USER}/hello-world:${version}.0
          """
          build job: 'kubernetes', parameters: [string(name: 'tag', value: "${version}.0")]
        }
        }
    }
  }
}

// build job: part triggers the K8 pipeline and the besrion tag is taken from the Docker pipeline build number 
// "./docker" leads to GitHub repo - it finds the Dockerfile in the current dir    
// if error shows helm release problem,  uninstall previous apache helm chart in the cluster namespace and or install ina  diff ns. 
// "helm uninstall apache" command 