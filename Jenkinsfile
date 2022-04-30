pipeline {
    agent any

    environment {
        imagename = "anchor-login-be"
        registryCredential = 'anchor-ecr-credentials'
        dockerImage = ''
    }

    stages {
        // git에서 repository clone
        stage('Prepare') {
            steps {
                echo 'Clonning Repository'
                git url:'https://github.com/GoormAnchor/AnchorOAuthLogin', branch:'master', credentialsId: 'anchor-repo-credentials';
            }
            post {
                success {
                    echo 'Successfully Cloned Repository'
                }
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        // gradle build
        stage('Bulid Gradle') {
            steps {
                echo 'Bulid Gradle'
                dir ('.') {
                    sh 'chmod +x gradlew' // gradle 실행하기위해 권한추가
                    sh './gradlew clean build --exclude-task test'
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        // docker build
        stage('Bulid Docker') {
            steps {
                echo 'Bulid Docker'
                script {
                    dockerImage = docker.build imagename
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        // docker push
        stage('Push Docker') {
            steps {
                echo 'Push Docker'
                script {
                    docker.withRegistry('https://438282170065.dkr.ecr.ap-northeast-2.amazonaws.com/anchor-login-be', 'ecr:ap-northeast-2:anchor-ecr-credentials') {
                        dockerImage.push("latest")
                    }
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }
    }
}