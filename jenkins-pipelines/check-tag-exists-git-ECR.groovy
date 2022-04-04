#!groovy
import java.time.*
import groovy.json.JsonSlurper
def gitRepo = 'https://bitbucket.example.com/scm/demo-project/demo-repository.git'
def gitCredentialsId = "b5aeddba-e2d2-4415-aab3-9cb3ec611111"
def gitBranch = "${env.BRANCH_NAME}"
def tag = "${env.TAG}"
def tag1 = "latest"
def tagString =''
def ecrTag ="${env.ECRTag}"
def epoch =''
def region = "${env.REGION}"

pipeline {
    agent { label "Jenkins-Node-1" }

    stages{
        stage("Parameters Check") {
            when { expression { env.Build_Action == 'Deploy_Existing_Tag' || env.TAG != '' ; } }
            steps{
                script{
                    cleanWs()
                    steps.git branch: gitBranch, url:gitRepo, credentialsId: gitCredentialsId
                    stash includes: '**', name: 'workspace'
                    sh """
                    if [ -z \$(git tag -l ${tag}) ]; then
                    echo "${tag} not found in GIT"
                    else
                    if [ -z \$(aws ecr list-images --region $region --repository-name demo-repo --query 'imageIds[?imageTag==`${tag1}`].imageTag' --output text) ]; then
                    echo "tag not found in ECR"
                    ${env.ECRTag}="false"
                    echo "ECR tag flag is-  ${env.ECRTag}"
                    else
                    echo "tag found in ECR"
                    ${env.ECRTag}="true"
                    echo "ECR tag flag is-  ${env.ECRTag}"
                    fi
                    fi
                    """
                }
            }
        }
    }
}
