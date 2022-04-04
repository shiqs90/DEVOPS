def gitRepo = 'https://bitbucket.example.com/scm/demo-project/demo-repository.git'
def gitCredentialsId = "b5aeddba-e2d2-4415-aab3-9cb3ec611111"
def gitBranch = "develop"
def tag ="tag-164567974587"
pipeline {
    agent any
    stages {
        stage('Create GIT Tag Stage') {
            steps{
                script{
                    cleanWs()
                    steps.git branch: gitBranch, url:gitRepo, credentialsId: gitCredentialsId
                    stash includes: '**', name: 'workspace'
                    sh """
                    if [ -z \$(git tag -l ${tag}) ]; then
                    echo "${tag} not found in GIT"                 
                    else                            
                    echo "${tag} already exists in GIT"
                    exit 1
                    fi
                    """
                    def remoteOrigin =gitRepo.replace('https://','')
                    withCredentials([usernamePassword(credentialsId: gitCredentialsId, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        def uri ="""'https://${GIT_USERNAME}:${GIT_PASSWORD}@${remoteOrigin}'"""
                        sh('git config --global user.email "shikhas@example.com"')
                        sh('git config --global user.name "shikhasingh"')
                        sh("git remote -v")
                    }
                    sh """
                    git tag -a ${tag} -m "tag: ${tag} is created'"
                    git push origin ${tag}
                    """
                }
            }
        }
    }
}
