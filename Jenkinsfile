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
                        mvn test -D spring.profiles.active=test &&
                        mvn jacoco:report -D spring.profiles.active=test &&
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
                    sh 'mvn clean install'
                }
            }
        }
        */
        // This should be built after all microservices.
        stage('Build Backend Gateway') {
            steps {
                dir('app-backend/gateway') {
                    // Build using Maven
                    sh '''
                        mvn test -D spring.profiles.active=test &&
                        mvn jacoco:report -D spring.profiles.active=test &&
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
                script {
                    echo "Merging Jacoco reports from all microservices..."
                    sh '''
                        mkdir -p app-backend/coverage
                        mvn jacoco:merge \
                            -Djacoco.destFile=app-backend/coverage/merged.exec \
                            -Djacoco.dataFiles="app-backend/ms-auth/target/jacoco.exec,app-backend/gateway/target/jacoco.exec"
                    '''
                }
            }
        }
        stage('Generate Combined Coverage Report') {
            steps {
                script {
                    echo "Generating the combined Jacoco report..."
                    sh '''
                        mvn jacoco:report -Djacoco.dataFile=app-backend/coverage/merged.exec \
                            -Djacoco.outputDirectory=app-backend/coverage/
                    '''
                }
            }
        }
        stage('Verify Combined Coverage') {
            steps {
                script {
                    def coverageReport = readFile 'app-backend/coverage/jacoco.xml'
                    
                    def coverage = sh(script: "xmllint --xpath 'string(//report/counter[@type=\"INSTRUCTION\"]/@covered)' app-backend/coverage/jacoco.xml", returnStdout: true).trim().toDouble()

                    if (coverage < 80.0) {
                        error("Combined coverage is below 80%! Current coverage: ${coverage}%")
                    } else {
                        echo "Combined coverage is sufficient: ${coverage}%"
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
