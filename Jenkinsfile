pipeline {
    agent any
    tools {
        maven 'Maven Apache' // Maven name in Jenkins config
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Route of java 17
    }
    stages {
        stage('Checkout') {
            steps {
                // https for clone repo
                git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: 'main', credentialsId: 'github-pat-global'
            }
        }
        stage('Build Backend') {
            steps {
                dir('app-backend/ms-auth') {
                    // Compile using maven
                    sh 'mvn clean install'
                }
            }
            /*
            steps {
                dir('app-backend/ms-user') {
                    // Compile using maven
                    sh 'mvn clean install'
                }
            }
            */
        }
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
