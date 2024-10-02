pipeline {
    agent any
    tools {
        maven 'Maven Apache' // Maven name in Jenkins config
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Ruta de Java 17
        PROFILES = 'test'
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    // Use the 'main' branch by default if nothing else is defined
                    def branchName = env.BRANCH_NAME ?: 'main'
                    echo "Checking out branch: ${branchName}"
                    
                    // Clone the repository to the corresponding branch
                    git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: branchName, credentialsId: 'github-pat-global'
                }
            }
        }
        stage('Build Backend Microservice Auth') {
            steps {
                dir('app-backend/ms-auth') {
                    // Build using Maven
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
                    // Build using Maven
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
