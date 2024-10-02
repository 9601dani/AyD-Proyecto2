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
        stage('Check Branch') {
            steps {
                script {
                    def allowedBranches = ['develop', 'main']
                    if (!allowedBranches.contains(env.BRANCH_NAME)) {
                        echo "Branch '${env.BRANCH_NAME}' is not allowed for this pipeline. Aborting."
                        currentBuild.result = 'SUCCESS'
                        error("Stopping pipeline as branch '${env.BRANCH_NAME}' is not allowed.")
                    } else {
                        echo "Branch '${env.BRANCH_NAME}' is allowed. Continuing with the build."
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                script {
                    echo "Checking out branch: ${env.BRANCH_NAME}"
                    // Clone the repository with the corresponding branch
                    git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: env.BRANCH_NAME, credentialsId: 'github-pat-global'
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
