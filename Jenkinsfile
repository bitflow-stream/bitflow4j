pipeline {
    agent {
        docker {
            image 'teambitflow/maven-docker:3.6-jdk-11'
            args '-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        registry = 'teambitflow/bitflow4j'
        registryCredential = 'dockerhub'
        dockerImage = ''
    }
    stages {
        stage('Git') {
            steps {
                script {
                    env.GIT_COMMITTER_EMAIL = sh(
                        script: "git --no-pager show -s --format='%ae'",
                        returnStdout: true
                        ).trim()
                }
            }
        }
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
                    archiveArtifacts 'target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/coverage-reports/*.exec'
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
            when {
                branch 'master'
            }
            steps {
                withSonarQubeEnv('CIT SonarQube') {
                    // The find & paste command in the jacoco line lists the relevant files and prints them, separted by comma
                    // The jacoco reports must be given file-wise, while the junit reports are read from the entire directory
                    sh '''
                        mvn sonar:sonar -B -V -Dsonar.projectKey=bitflow4j \
                            -Dsonar.sources=src/main/java -Dsonar.tests=src/test/java \
                            -Dsonar.inclusions="**/*.java" -Dsonar.test.inclusions="src/test/java/**/*.java" \
                            -Dsonar.exclusions="src/main/java/bitflow4j/script/generated/*.java" \
                            -Dsonar.junit.reportPaths=target/surefire-reports \
                            -Dsonar.jacoco.reportPaths=$(find target/coverage-reports -name '*.exec' | paste -s -d , -)
                    '''
                }  
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Maven Install') {
            when {
                branch 'master'
            }
            steps {
                // Only install the jar, that has been built in the previous stages. Do not re-compile or re-test.
                sh 'mvn jar:jar install:install -B -V'
            }
        }
        stage('Docker build') {
            steps {
                script {
                    dockerImage = docker.build registry + ':$BRANCH_NAME-build-$BUILD_NUMBER'
                }
            }
        }
        stage('Docker push') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('', registryCredential ) {
                        env.dockerImage.push(':build-$BUILD_NUMBER')
                        dockerImage.push('latest')
                    }
                }
            }
        }
    }
    post {
        success {
            withSonarQubeEnv('CIT SonarQube') {
                slackSend channel: '#jenkins-builds-all', color: 'good',
                    message: "Build ${env.JOB_NAME} ${env.BUILD_NUMBER} was successful (<${env.BUILD_URL}|Open Jenkins>) (<${env.SONAR_HOST_URL}|Open SonarQube>)"
            }
        }
        failure {
            slackSend channel: '#jenkins-builds-all', color: 'danger',
                message: "Build ${env.JOB_NAME} ${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open Jenkins>)"
        }
        fixed {
            withSonarQubeEnv('CIT SonarQube') {
                slackSend channel: '#jenkins-builds', color: 'good',
                    message: "Thanks to ${env.GIT_COMMITTER_EMAIL}, build ${env.JOB_NAME} ${env.BUILD_NUMBER} was successful (<${env.BUILD_URL}|Open Jenkins>) (<${env.SONAR_HOST_URL}|Open SonarQube>)"
            }
        }
        regression {
            slackSend channel: '#jenkins-builds', color: 'danger',
                message: "What have you done ${env.GIT_COMMITTER_EMAIL}? Build ${env.JOB_NAME} ${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open Jenkins>)"
        }
    }
}
