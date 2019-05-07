pipeline {
    agent {
        docker {
            image 'maven:3.6-jdk-11'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        registry = 'teambitflow/bitflow4j'
        registryCredential = 'dockerhub'
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn test-compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit "target/surefire-reports/TEST-*.xml"
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
            post {
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
        stage('Build container') {
            steps {
                // sh 'docker build -t  .'
                script {
                    docker.build registry + ":$BUILD_NUMBER"
                }
            }
            //post {
            //   success {
            //        sh ''
            //    }
            //}
        }
    }
}

