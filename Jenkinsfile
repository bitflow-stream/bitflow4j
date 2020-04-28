pipeline {
    options {
        timeout(time: 1, unit: 'HOURS')
    }
        agent {
        docker {
            image 'bitflowstream/java-build'
            args '-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        registry = 'bitflowstream/bitflow-pipeline-java'
        registryCredential = 'dockerhub'
        dockerImage = ''
        dockerImageARM32 = ''
        dockerImageARM64 = ''
    }
    stages {
        stage('Git') {
            steps {
                script {
                    env.GIT_COMMITTER_EMAIL = sh(script: "git --no-pager show -s --format='%ae'", returnStdout: true).trim()
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
            steps {
                withSonarQubeEnv('CIT SonarQube') {
                    // The find & paste command in the jacoco line lists the relevant files and prints them, separated by comma
                    // The jacoco reports must be given file-wise, while the junit reports are read from the entire directory
                    sh '''
                        mvn sonar:sonar -B -V -Dsonar.projectKey=bitflow4j -Dsonar.branch.name=$BRANCH_NAME \
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
                    dockerImage = docker.build registry + ':$BRANCH_NAME-build-$BUILD_NUMBER', '-f build/alpine.Dockerfile .'
                    sh "./build/test-image.sh $BRANCH_NAME-build-$BUILD_NUMBER"

                    dockerImageARM32 = docker.build registry + ':$BRANCH_NAME-build-$BUILD_NUMBER-arm32v7', '-f build/arm32v7.Dockerfile .'
                    sh "./build/test-image.sh $BRANCH_NAME-build-$BUILD_NUMBER-arm32v7"

                    dockerImageARM64 = docker.build registry + ':$BRANCH_NAME-build-$BUILD_NUMBER-arm64v8', '-f build/arm64v8.Dockerfile .'
                    sh "./build/test-image.sh $BRANCH_NAME-build-$BUILD_NUMBER-arm64v8"
                }
            }
        }
        stage('Docker push') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push("build-$BUILD_NUMBER")
                        dockerImage.push("latest-amd64")
                        // arm32v7 image
                        dockerImageARM32.push("build-$BUILD_NUMBER-arm32v7")
                        dockerImageARM32.push("latest-arm32v7")
                        // arm64v8 image
                        dockerImageARM64.push("build-$BUILD_NUMBER-arm64v8")
                        dockerImageARM64.push("latest-arm64v8")
                    }
                }
                withCredentials([[
                    $class: 'UsernamePasswordMultiBinding',
                    credentialsId: 'dockerhub', usernameVariable: 'DOCKERUSER', passwordVariable: 'DOCKERPASS'
                ]]) {
                    // Dockerhub Login
                    sh 'echo "$DOCKERPASS" | docker login -u "$DOCKERUSER" --password-stdin'
                    sh "docker manifest create ${registry}:latest ${registry}:latest-amd64 ${registry}:latest-arm32v7 ${registry}:latest-arm64v8"
                    sh "docker manifest annotate ${registry}:latest ${registry}:latest-arm32v7 --os=linux --arch=arm --variant=v7"
                    sh "docker manifest annotate ${registry}:latest ${registry}:latest-arm64v8 --os=linux --arch=arm64 --variant=v8"
                    sh "docker manifest push --purge ${registry}:latest"
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
