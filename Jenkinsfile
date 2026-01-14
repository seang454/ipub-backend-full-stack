@Library('spring-docuhub-share-library@master') _

pipeline {
    agent any

    environment {
        REPO_NAME  = 'seang454'
        IMAGE_NAME = 'docuhub-spring'
        TAG        = 'latest'
        NETWORK    = 'spring-net'
        DB_NAME    = 'postgres'
        DB_USER    = 'postgres'
        DB_PASS    = 'qwer'   // Update to your password
        SPRING_HOST_PORT = '8083'
        SPRING_CONTAINER_PORT = '8080' // Should match server.port in Spring Boot
    }

    stages {

        // 1️⃣ Clone Spring Boot Code
        stage('Clone Code') {
            steps {
                git 'https://github.com/seang454/spring-share-library.git'
            }
        }

        // 2️⃣ Build & Test with H2 (test profile)
        stage('Build & Test with H2') {
            steps {
                script {
                    if (fileExists('pom.xml')) {
                        withEnv(['SPRING_PROFILES_ACTIVE=test']) {
                            sh 'mvn clean test'
                        }
                    } else if (fileExists('build.gradle')) {
                        withEnv(['SPRING_PROFILES_ACTIVE=test']) {
                            sh 'chmod +x gradlew && ./gradlew clean test'
                        }
                    } else {
                        error "No build file found"
                    }
                }
            }
        }

        // 3️⃣ Prepare Dockerfile from shared library
        stage('Prepare Dockerfile') {
            steps {
                script {
                    def sharedDockerfile = libraryResource 'springboot/dev.Dockerfile'
                    def dockerfilePath = 'Dockerfile'

                    if (fileExists(dockerfilePath)) {
                        def existingDockerfile = readFile(dockerfilePath)
                        if (existingDockerfile.trim() != sharedDockerfile.trim()) {
                            echo 'Dockerfile differs from shared library. Replacing it.'
                            sh "rm -f ${dockerfilePath}"
                            writeFile file: dockerfilePath, text: sharedDockerfile
                        } else {
                            echo 'Dockerfile is already up-to-date.'
                        }
                    } else {
                        echo 'Dockerfile not found. Creating from shared library.'
                        writeFile file: dockerfilePath, text: sharedDockerfile
                    }
                }
            }
        }

        // 4️⃣ Deploy Nginx config from shared library
        stage('Deploy Nginx config') {
            steps {
                script {
                    def sharedNginxConfig = libraryResource 'springboot/nginx.conf'
                    writeFile file: 'nginx.conf', text: sharedNginxConfig

                    sh """
                        sudo mv nginx.conf /etc/nginx/sites-available/docuhub.seang.shop
                        sudo ln -sf /etc/nginx/sites-available/docuhub.seang.shop /etc/nginx/sites-enabled/
                        sudo nginx -t
                        sudo systemctl reload nginx
                    """
                }
            }
        }

        // 5️⃣ Build Docker Image
        stage('Build Image') {
            steps {
                sh 'docker build --no-cache -t ${REPO_NAME}/${IMAGE_NAME}:${TAG} .'
            }
        }

        // 6️⃣ Ensure Docker Hub Repo Exists
        stage('Ensure Docker Hub Repo Exists') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'DOCKERHUB-CREDENTIAL',
                    usernameVariable: 'DH_USERNAME',
                    passwordVariable: 'DH_PASSWORD'
                )]) {
                    sh '''
                    STATUS=$(curl -s -o /dev/null -w "%{http_code}" -u "$DH_USERNAME:$DH_PASSWORD" \
                      https://hub.docker.com/v2/repositories/$REPO_NAME/$IMAGE_NAME/)

                    if [ "$STATUS" -eq 404 ]; then
                      curl -s -u "$DH_USERNAME:$DH_PASSWORD" -X POST \
                        https://hub.docker.com/v2/repositories/ \
                        -H "Content-Type: application/json" \
                        -d "{\"name\":\"$IMAGE_NAME\",\"is_private\":false}"
                    fi
                    '''
                }
            }
        }

        // 7️⃣ Push Docker Image
        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'DOCKERHUB-CREDENTIAL',
                    usernameVariable: 'DH_USERNAME',
                    passwordVariable: 'DH_PASSWORD'
                )]) {
                    sh '''
                    echo "$DH_PASSWORD" | docker login -u "$DH_USERNAME" --password-stdin
                    docker push ${REPO_NAME}/${IMAGE_NAME}:${TAG}
                    docker logout
                    '''
                }
            }
        }

        // 8️⃣ Create Docker Network
        stage('Create Docker Network') {
            steps {
                sh '''
                docker network inspect ${NETWORK} >/dev/null 2>&1 || \
                docker network create ${NETWORK}
                '''
            }
        }

        // 9️⃣ Run PostgreSQL Container
        stage('Run PostgreSQL') {
            steps {
                sh """
                docker rm -f postgres || true

                docker run -d \
                  --name postgres \
                  --network ${NETWORK} \
                  -e POSTGRES_DB=${DB_NAME} \
                  -e POSTGRES_USER=${DB_USER} \
                  -e POSTGRES_PASSWORD=${DB_PASS} \
                  postgres:16

                # Wait for Postgres to be ready
                until docker exec postgres pg_isready -U ${DB_USER}; do
                  echo "Waiting for Postgres..."
                  sleep 2
                done
                """
            }
        }

        // 🔟 Run Spring Boot Container
        stage('Run Spring Boot') {
            steps {
                sh """
                docker rm -f spring-app || true

                docker run -d \
                  --name spring-app \
                  --network ${NETWORK} \
                  -p ${SPRING_HOST_PORT}:${SPRING_CONTAINER_PORT} \
                  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${DB_NAME} \
                  -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                  -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \
                  ${REPO_NAME}/${IMAGE_NAME}:${TAG}
                """
            }
        }

        stage('Generate SSL with Certbot') {
            steps {
                sh '''
                # Install certbot if not installed
                sudo apt update
                sudo apt install -y certbot python3-certbot-nginx openssl

                DOMAIN="docuhub.seang.shop"
                CERT_PATH="/etc/letsencrypt/live/$DOMAIN/fullchain.pem"

                if [ -f "$CERT_PATH" ]; then
                    echo "SSL certificate exists. Checking expiry date..."

                    # Get expiry date in seconds since epoch
                    EXPIRY_DATE=$(openssl x509 -enddate -noout -in "$CERT_PATH" | cut -d= -f2)
                    EXPIRY_SECONDS=$(date -d "$EXPIRY_DATE" +%s)
                    NOW_SECONDS=$(date +%s)

                    # Calculate remaining days
                    REMAINING_DAYS=$(( (EXPIRY_SECONDS - NOW_SECONDS) / 86400 ))

                    echo "Certificate expires in $REMAINING_DAYS days."

                    if [ "$REMAINING_DAYS" -le 30 ]; then
                        echo "Certificate expires in 30 days or less. Renewing..."
                        sudo certbot renew --quiet --deploy-hook "sudo systemctl reload nginx"
                    else
                        echo "Certificate is valid for more than 30 days. Skipping renewal."
                    fi
                else
                    echo "SSL certificate not found. Generating new one..."
                    sudo certbot --nginx -d $DOMAIN \
                        --non-interactive --agree-tos -m your-email@example.com --redirect
                fi
                '''
            }
        }
    }
}
