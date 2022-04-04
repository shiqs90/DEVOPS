def gitRepo = 'https://bitbucket.example.com/scm/demo-project/demo-repository.git'
def gitCredentialsId = "b5aeddba-e2d2-4415-aab3-9cb3ec611111"
pipeline {
    agent { label "Jenkins-Node-1" }
    stages {
        stage("testCheckout") {
            steps{
                checkout([$class: 'GitSCM',
                          branches: [[name: 'refs/heads/'+env.BRANCH_NAME]],
                          userRemoteConfigs: [[
                                                      credentialsId: 'gitCredentialsId',
                                                      refspec: '+refs/tags/Pulse-temp-bookmark',
                                                      url: 'https://bitbucket.example.com/scm/demo-project/demo-repository.git']]
                ])
            }
        }
    }
}