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
                sh 'mvn clean test-compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test -B -V'
            }
            post {
                always {
                    junit "target/surefire-reports/TEST-*.xml"
                    jacoco classPattern: 'target/classes,target/test-classes', execPattern: 'target/coverage-reports/*.exec', inclusionPattern: '**/*.class', sourcePattern: 'src/main/java,src/test/java'
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
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('CIT SonarQube') {
                    sh 'mvn sonar:sonar'
                }  
            }
        }
        stage('Build container') {
            when {
                branch 'master'
            }
            steps {
                script {
                    sh 'docker build -t $docker_image:build-$BUILD_NUMER -t $docker_image:latest .'
                }
            }
            post {
                success {
                    sh '''
                        docker push $docker_image:build-$BUILD_NUMBER
                        docker push $docker_image:latest'
                    '''
               }
            }
        }
    }
}

