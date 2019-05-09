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
                    // The find & paste commands in the jacoco/junit lines list the relevant files and prints them, separted by comma
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.sources=./src/main/java -Dsonar.tests=./src/test/java \
                        -Dsonar.inclusions="**/*.java" -Dsonar.test.inclusions="**/*.java" \
                        -Dsonar.junit.reportPaths=$(find target/surefire-reports -name 'TEST-*.xml' | paste -s -d , -) \
                        -Dsonar.jacoco.reportPaths=$(find target/coverage-reports -name '*.exec' | paste -s -d , -)
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

