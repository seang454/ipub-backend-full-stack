pipeline {
    agent any

    environment {
        SSH_PORT = "2222"
        SSH_USER = "vagrant"
        SSH_HOST = "127.0.0.1"

        APP_NAME = "docuhub-app"
        CONTAINER_NAME = "docuhub-container"
        APP_DIR = "/home/vagrant/docuhub-api"
        GRADLE_USER_HOME = "/home/vagrant/.gradle"
    }

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                echo "📥 Checking out code from GitHub..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "🛠️ Building project (Gradle)..."
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'vagrant-ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
                    set -e
                    set -o pipefail

                    if [ -d "${APP_DIR}" ]; then
                      cd ${APP_DIR}
                      git fetch origin
                      git reset --hard origin/main
                    else
                      git clone https://github.com/seang454/ipub-backend-full-stack.git ${APP_DIR}
                      cd ${APP_DIR}
                    fi

                    chmod +x gradlew
                    ./gradlew clean build --no-daemon --parallel
EOF
                    """
                }
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Running tests..."
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'vagrant-ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
                    set -e
                    cd ${APP_DIR}
                    ./gradlew test --no-daemon
EOF
                    """
                }
            }
        }

        stage('Delivery') {
            steps {
                echo "📦 Building Docker image..."
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'vagrant-ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
                    set -e
                    cd ${APP_DIR}

                    if ! docker --version >/dev/null 2>&1; then
                        sudo apt-get update -y
                        sudo apt-get install -y docker.io
                        sudo systemctl enable --now docker
                        sudo usermod -aG docker ${SSH_USER}
                    fi

                    docker rmi ${APP_NAME} || true
                    docker build -t ${APP_NAME} .
EOF
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploying container..."
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'vagrant-ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
                    set -e

                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true

                    docker run -d -p 8080:8080 \
                      --name ${CONTAINER_NAME} \
                      --restart unless-stopped \
                      ${APP_NAME}

                    docker ps | grep ${CONTAINER_NAME}
EOF
                    """
                }
            }
        }
    }

    post {
        success {
            echo "🎉 CI/CD pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed!"
        }
    }
}
