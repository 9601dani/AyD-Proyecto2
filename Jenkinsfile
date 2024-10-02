pipeline {
    agent any
    tools {
        maven 'Maven Apache' // Maven name in Jenkins config
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Route of java 17
        PROFILES = 'test'
    }
    stages {
        stage('Checkout') {
            steps {
                // https to clone repo
                git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: 'main', credentialsId: 'github-pat-global'
            }
        }
        stage('Build Backend Microservice Auth') {
            steps {
                dir('app-backend/ms-auth') {
                    // Compile using maven
                    sh '''
                        mvn test &&
                        mvn clean install
                    '''
                }
            }
        }
        /*
        stage('Build Backend Microservice User') {
            steps {
                dir('app-backend/ms-user') {
                    // Compile using maven
                    sh 'mvn clean install'
                }
            }
        }
        */
    }
    post {
        success {
            echo 'Backend build completed successfully!'
        }
        failure {
            echo 'Backend build failed.'
        }
    }
}

// test webhook 2
