pipeline {
    agent { label "Jenkins-Node-1" }

    stages {
        stage('Hello') {
            steps {
                script{
                    def outputString = sh '/home/ec2-user/jenkins/find-ecr-image.sh demo-repo latest'
                    println "Output string is ${outputString}"
                }
            }
        }
    }
}
