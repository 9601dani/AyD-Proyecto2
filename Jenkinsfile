pipeline {
    agent any
    tools {
        maven 'Maven 3.9.5' // Este es el nombre que se le dio en la  instalación de Maven en Jenkins
    }
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64' // Ruta de Java 17
    }
    stages {
        stage('Checkout') {
            steps {
                // Clonar el repositorio desde GitHub
                git url: 'https://github.com/9601dani/AyD-Proyecto2.git', branch: 'main', credentialsId: 'github-pat-global'
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
