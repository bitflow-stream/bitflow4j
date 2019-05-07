pipeline {
    agent {
        docker {
            image 'maven:3.6-jdk-11'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn test-compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test jacoco:report coveralls:report'
            }
        }
        /*
        stage('Build Container') { 
            steps {
                // 
            }
        }
        stage('Push Container') {
            steps {
                // 
            }
        }
        */
    }
}

