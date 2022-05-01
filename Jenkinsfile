pipeline {
    agent any

    environment {
        imagename = "anchor-login-be"
        registryCredential = 'anchor-ecr-credentials'
        dockerImage = 'anchor-login-be'
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
                sh "docker build . -t 438282170065.dkr.ecr.ap-northeast-2.amazonaws.com/anchor-login-be:${currentBuild.number}"
                sh "docker build . -t 438282170065.dkr.ecr.ap-northeast-2.amazonaws.com/anchor-login-be:latest"
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
                        sh "docker push 438282170065.dkr.ecr.ap-northeast-2.amazonaws.com/anchor-login-be:${currentBuild.number}"
                        sh "docker push 438282170065.dkr.ecr.ap-northeast-2.amazonaws.com/anchor-login-be:latest"
                    }
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }   
            }
        }

             // k8s manifest update
        stage('K8S Manifest Update') {
            steps {
                git url: 'https://github.com/GoormAnchor/anchor-k8s-deploy', branch: 'main', credentialsId: 'anchor-repo-credentials'

                sh "sed -i 's/anchor-login-be:.*\$/anchor-login-be:${currentBuild.number}/g' anchor-login-be.yaml"
                sh "git add anchor-login-be.yaml"
                sh "git commit -m 'UPDATE anchor-login-be ${currentBuild.number} image versioning'"
                sshagent(credentials: ['anchor-repo-credentials']) {
                    sh "git remote set-url origin git@github.com:GoormAnchor/anchor-k8s-deploy.git"
                    sh "git push -u origin main"
                }
            }
            post {
                failure {
                  echo 'K8S Manifest Update failure !'
                }
            }
        }
    }
}