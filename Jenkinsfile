pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'Java'
        // git 'Git'
    }

    triggers {
        // Jenkins corre en local (no en AWS): GitHub no puede llamar al webhook,
        // por eso Jenkins revisa el repositorio cada minuto y se dispara con cada push.
        pollSCM('* * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Getting code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building...'
                script {
                    if (isUnix()) { sh 'mvn clean compile' } else { bat 'mvn clean compile' }
                }
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Testing (pruebas unitarias y de integracion)...'
                script {
                    if (isUnix()) { sh 'mvn test' } else { bat 'mvn test' }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Creating JAR...'
                script {
                    if (isUnix()) { sh 'mvn package -DskipTests' } else { bat 'mvn package -DskipTests' }
                }
            }
        }
    }
}
