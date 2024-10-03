pipeline {
    agent any
    tools {
        maven 'Maven Apache' // Maven name in Jenkins config
        nodejs 'NodeJs' // NodeJS name in Jenkins config 
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Path to Java home 17
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
                    git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: 'develop', credentialsId: 'github-pat-global'
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
        // stage('Build Frontend') {
        //     steps {
        //         dir('app-frontend') {
        //             // Install dependencies
        //             sh 'npm install'
        
        //             // Build project
        //             sh 'npm run build -- --configuration=production'

        //             //Run unit test
        //            // sh 'npm test -- --watch=false --browsers=ChromeHeadless'
        //         }
        //     }
        // }

        stage('Merge PR') {
            when {
                expression {
                    env.CHANGE_ID != null && env.CHANGE_TARGET == 'develop'
                }
            }
            steps {
                script {
                    def isMergeable = sh(script: "gh pr view $CHANGE_ID --json mergeable --jq .mergeable", returnStdout: true).trim()

                    if (isMergeable == 'true') {
                        echo "PR is mergeable, proceeding to merge."
                        sh "gh pr merge $CHANGE_ID --merge --admin"
                    } else {
                        error("PR is not mergeable. Merge skipped.")
                    }
                }
            }
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
