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
                sh 'mvn clean test-compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V'
            }
        }
        stage('Test') { 
            steps {
                sh 'mvn test -B -V'
            }
            post {
                always {
                    junit 'target/surefire-reports/TEST-*.xml'
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
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('CIT SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.junit.reportPaths="target/surefire-reports/TEST-*.xml" \
                        -Dsonar.jacoco.reportPaths="target/coverage-reports/*.exec" \
                    '''
                }  
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Slack message') {
            steps { sh 'true' }
            post {
                success {
                    withSonarQubeEnv('CIT SonarQube') {
                        slackSend color: 'good', message: "Build ${env.JOB_NAME} ${env.BUILD_NUMBER} was successful (<${env.BUILD_URL}|Open Jenkins>) (<${env.SONAR_HOST_URL}|Open SonarQube>)"
                    }
               }
               failure {
                    slackSend color: 'danger', message: "Build ${env.JOB_NAME} ${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open Jenkins>)"
               }
            }
        }
    }
}

