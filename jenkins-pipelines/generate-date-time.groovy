#!groovy
import java.text.SimpleDateFormat
pipeline {
    agent any
    stages {
        stage('Generate Date-time') {
            steps {
                script{
                def date = new Date()
                sdf = new SimpleDateFormat("MM/dd/yyyy")
                println(sdf.format(date))
                }
            }
        }
    }
}