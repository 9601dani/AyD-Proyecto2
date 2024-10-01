pipeline {
    agent any
    tools {
        Maven Apache
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Ruta de Java 17
        MAVEN_HOME = '/opt/maven' // Ruta de Maven
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${env.PATH}"
    }
    stages {
        stage('Checkout') {
            steps {
                // Clonar el repositorio desde GitHub
                git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: 'main',credentialsId: 'github-pat-global'
            }
        }
        stage('Build Backend') {
            steps {
                dir('app-backend') {
                    // Compilar el backend usando Maven
                    sh 'mvn clean install'
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
