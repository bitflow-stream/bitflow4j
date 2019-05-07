pipeline {
    agent {
        docker {
            image 'teambitflow/maven-docker:3.6-jdk-11'
            args '-v /root/.m2:/root/.m2 -v /root/.docker:/root/.docker'
        }
    }
    environment {
        docker_image = 'teambitflow/bitflow4j'
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn test-compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test -B -V'
            }
            post {
                always {
                    junit "target/surefire-reports/TEST-*.xml"
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
            post {
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
        stage('Build container') {
            steps {
                script {
                    docker.build docker_image + ':build-$BUILD_NUMBER'
                }
            }
            post {
               success {
                   sh 'docker tag $docker_image:build-$BUILD_NUMBER $docker_image:latest'
                   sh 'docker push $docker_image:build-$BUILD_NUMBER'
                   sh 'docker push $docker_image:latest'
               }
            }
        }
    }
}

