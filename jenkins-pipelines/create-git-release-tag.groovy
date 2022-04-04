#!groovy
import java.text.SimpleDateFormat
def gitRepo = "${env.REPO_NAME}"
def gitBranch = "${env.BRANCH_NAME}"
def gitCredentialsId = "b5aeddba-e2d2-4415-aab3-9cb3ec611111"
def snapshotVersion = ''
pipeline {
    agent { label "Jenkins-Node-1" }
    stages {
        stage('Pull code from GIT') {
            steps {
                script {
                    cleanWs()
                    steps.git branch: gitBranch, url:gitRepo, credentialsId: gitCredentialsId
                    stash includes: '**', name: 'workspace'
                }
            }
        }
        stage('Initialize GIT') {
            steps {
                script {
                    def remoteOrigin =gitRepo.replace('https://','')
                    snapshotVersion = sh(script:"""xmllint --xpath "/*[local-name() = 'project']/*[local-name() = 'version']/text()" pom.xml""", returnStdout:true).trim()
                    withCredentials([usernamePassword(credentialsId: gitCredentialsId, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        def uri ="""'https://${GIT_USERNAME}:${GIT_PASSWORD}@${remoteOrigin}'"""
                        sh('git config --global user.email "shikhas@example.com"')
                        sh('git config --global user.name "shikhasingh"')
                        sh("git remote -v")
                    }
                }
            }
        }
        stage('Create release tag') {
            steps {
                script {
                    def date = new Date()
                    sdf = new SimpleDateFormat("dd-MM-yyyy")
                    //snapshotVersion = sh(script:"""xmllint --xpath "/*[local-name() = 'project']/*[local-name() = 'version']/text()" pom.xml""", returnStdout:true).trim()
                    println("Date is: "+sdf.format(date))
                    def TAG="tag-${sdf.format(date)}"
                    echo "TAG is : ${TAG}"
                    sh """
                     echo "TAG is : ${TAG}"
                     git tag -a ${TAG} -m "tag: ${TAG} is created"
                     echo "*** Created tag ${TAG} in ${gitBranch}"
                     git push origin ${TAG}

                """
                }
            }
        }
    }
}