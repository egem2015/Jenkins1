pipeline {
    agent any
    tools{
		maven 'maven1'
		'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'docker'
	}

    environment {
        MONGODB_URI = 'mongodb://localhost:27017/productdb' // Uygulamanın bağlanacağı MongoDB URI
        APP_PORT = '9691' // Uygulamanın çalışacağı port
        APP_NAME = 'Jenkins1' // Uygulamanın ismi (JAR dosyasının ismi)
        ARTIFACT_PATH = "${APP_NAME}-0.0.1-SNAPSHOT.jar" // Maven'ın üreteceği JAR dosyasının tam adı
    }

    stages {
        stage('Kodu Çek') {
            steps {
                echo 'Kaynak kod kontrolünden çekiliyor...'
                git branch: 'main', url: 'https://github.com/egem2015/Jenkins1.git'
            }
        }

        stage('Derle ve Test Et') {
            steps {
                echo 'Maven ile uygulama derleniyor ve test ediliyor...'
                sh "mvn clean package -DskipTests=true"
            }
        }

  stage('Build docker') {
                 dockerImage = docker.build("springboot-deploy:${env.BUILD_NUMBER}")
          }

          stage('Deploy docker'){
                  echo "Docker Image Tag Name: springboot-deploy:${env.BUILD_NUMBER}"
                  sh "docker stop springboot-deploy || true && docker rm springboot-deploy || true"
                  sh "docker run --name springboot-deploy -d -p 8081:8081 springboot-deploy:${env.BUILD_NUMBER}"
          }
  

        stage('Dağıtım Sonrası Sağlık Kontrolü') {
            steps {
                echo 'Sağlık kontrolü yapılıyor...'
                script {
                    sleep 10
                    def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/actuator/health", returnStdout: true).trim()
                    if (response == '200') {
                        echo 'Uygulama çalışıyor!'
                    } else {
                        error "Sağlık kontrolü başarısız oldu: ${response}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline tamamlandı.'
        }
        success {
            echo 'Pipeline başarıyla tamamlandı!'
        }
        failure {
            echo 'Pipeline başarısız oldu!'
        }
    }
}