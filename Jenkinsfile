pipeline {
    agent any
    tools {
        maven 'Maven Apache' // Maven name in Jenkins config
        // nodejs 'NodeJs' // NodeJS name in Jenkins config 
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Path to Java home 17
        PROFILES = 'test'
    }
    stages {
        // stage('Check Branch') {
        //     steps {
        //         script {
        //             def allowedBranches = ['develop', 'main']
        //             if (!allowedBranches.contains(env.BRANCH_NAME)) {
        //                 echo "Branch '${env.BRANCH_NAME}' is not allowed for this pipeline. Aborting."
        //                 currentBuild.result = 'SUCCESS'
        //                 error("Stopping pipeline as branch '${env.BRANCH_NAME}' is not allowed.")
        //             } else {
        //                 echo "Branch '${env.BRANCH_NAME}' is allowed. Continuing with the build."
        //             }
        //         }
        //     }
        // }
        stage('Check Environment Variables') {
            steps {
                script {
                    echo "GCP_BUCKET_NAME=${env.GCP_BUCKET_NAME}"
                    echo "GCP_CREDENTIALS_FILE_PATH=${env.GCP_CREDENTIALS_FILE_PATH}"
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
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }
        stage('Build Backend Microservice Img') {
            steps {
                dir('app-backend/ms-img') {
                    // Build using Maven
                    sh '''
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }
        /*
        stage('Build Backend Microservice User') {
            steps {
                dir('app-backend/ms-user') {
                    // Build using Maven
                    sh '''
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }
        */
        stage('Build Backend Microservice User') {
            steps {
                dir('app-backend/ms-user') {
                    // Build using Maven
                    sh '''
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }

        stage('Build Backend Microservice Email') {
            steps {
                dir('app-backend/ms-email') {
                    // Build using Maven
                    sh '''
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }
        // This should be built after all microservices.
        stage('Build Backend Gateway') {
            steps {
                dir('app-backend/gateway') {
                    // Build using Maven
                    sh '''
                        mvn clean test -D spring.profiles.active=test &&
                        mvn clean install -D spring.profiles.active=test
                    '''
                }
            }
        }
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
        stage('Merge Jacoco Reports') {
            steps {
                dir('app-backend') {
                    echo "Merging Jacoco reports from all microservices..."
                    sh 'mvn clean test verify -D spring.profiles.active=test'
                }
            }
        }

        stage('Verify tests') {
            steps {
                
                dir('app-backend') {
                    echo "cat ms-auth TESTS"
                    sh 'cat ms-auth/target/surefire-reports/*.txt'
                    echo "cat gateway TESTS"
                    sh 'cat gateway/target/surefire-reports/*.txt'
                    echo "cat report TESTS"
                    sh 'cat report/target/surefire-reports/*.txt'
                }
            }
        }

        stage('Verify Jacoco Exec') {
            steps {
                dir('app-backend/report/target') {
                    sh 'ls -l'
                }
            }
        }
        
    }
    post {
        success {
            script {
                jacoco (
                    execPattern: '**/target/*.exec',
                    classPattern: '**/target/classes',
                    sourcePattern: '**/src/main/java',
                    exclusionPattern: '**/target/test-classes',
                    changeBuildStatus: true,
                    minimumLineCoverage: '80'
                )

            }
            echo 'Backend build completed successfully!'
        }
        failure {
            echo 'Backend build failed.'
        }
    }
}
