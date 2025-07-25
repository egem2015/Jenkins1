pipeline {
    agent any

    environment {
        MONGODB_URI = 'mongodb://localhost:27017/productdb' // Uygulamanın bağlanacağı MongoDB URI
        APP_PORT = '8080' // Uygulamanın çalışacağı port
        APP_NAME = 'my-product-app' // Uygulamanın ismi (JAR dosyasının ismi)
        ARTIFACT_PATH = "${APP_NAME}-0.0.1-SNAPSHOT.jar" // Maven'ın üreteceği JAR dosyasının tam adı
    }

    stages {
        stage('Kodu Çek') {
            steps {
                echo 'Kaynak kod kontrolünden çekiliyor...'
                // BURAYI KENDİ GİTHUB KULLANICI ADINLA DEĞİŞTİRDİĞİNDEN EMİN OL!
                git branch: 'main', url: 'https://github.com/senin-kullanici-adın/my-product-app.git'
            }
        }

        stage('Derle ve Test Et') {
            steps {
                echo 'Maven ile uygulama derleniyor ve test ediliyor...'
                sh "mvn clean install"
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker İmajını Oluştur') {
            steps {
                echo 'Docker imajı oluşturuluyor...'
                script {
                    dockerfileContent = """
                        FROM openjdk:17-jdk-slim
                        VOLUME /tmp
                        ARG JAR_FILE=target/${ARTIFACT_PATH}
                        COPY \${JAR_FILE} app.jar
                        ENV SPRING_DATA_MONGODB_URI=${MONGODB_URI}
                        ENV SERVER_PORT=${APP_PORT}
                        EXPOSE ${APP_PORT}
                        ENTRYPOINT ["java","-jar","/app.jar"]
                    """.stripIndent()
                    docker.build("${APP_NAME}:${BUILD_NUMBER}".toLowerCase(), "-f - .").using(stdin: dockerfileContent)
                }
            }
        }

        stage('Uygulamayı Dağıt') {
            steps {
                echo 'Uygulama dağıtılıyor...'
                script {
                    // Önceki çalışan Docker konteynerini durdur ve kaldır (varsa)
                    sh "docker stop ${APP_NAME} || true"
                    sh "docker rm ${APP_NAME} || true"

                    // Yeni Docker imajını çalıştır
                    sh "docker run -d -p ${APP_PORT}:${APP_PORT} --name ${APP_NAME} --network host ${APP_NAME}:${BUILD_NUMBER}".toLowerCase()

                    echo "Uygulama şuraya dağıtıldı: http://localhost:${APP_PORT}/api/products"
                }
            }
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