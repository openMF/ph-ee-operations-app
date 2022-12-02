pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('docker') {
            steps {
                sh 'docker build . -t paymenthubee.azurecr.io/phee/operations-app'
                sh 'docker push paymenthubee.azurecr.io/phee/operations-app'
            }
        }
    }
}
