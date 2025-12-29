pipeline {
    agent any

    environment {
        SSH_USER = "vagrant"
        SSH_HOST = "192.168.56.12"
        SSH_PORT = "22"

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

        stage('Build & Test') {
            steps {
                echo "🛠️ Building project on remote VM..."
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'vagrant-ssh-key',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e
set -o pipefail

# Ensure Java 21
if ! java -version 2>&1 | grep -q "21"; then
    sudo apt-get update -y
    sudo apt-get install -y openjdk-21-jdk
fi

export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=\$JAVA_HOME/bin:\$PATH

java -version
javac -version

# Clone or update repo
if [ -d "${APP_DIR}" ]; then
    cd ${APP_DIR}
    git fetch origin
    git reset --hard origin/main
else
    git clone https://github.com/seang454/ipub-backend-full-stack.git ${APP_DIR}
    cd ${APP_DIR}
fi

chmod +x gradlew
./gradlew --stop
./gradlew clean build -x test --no-daemon

EOF
                    """
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo "🐳 Building Docker image..."
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'vagrant-ssh-key',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e

if ! command -v docker >/dev/null; then
    sudo apt-get update -y
    sudo apt-get install -y docker.io
    sudo systemctl enable --now docker
fi

sudo docker rmi ${APP_NAME} || true
sudo docker build -t ${APP_NAME} ${APP_DIR}

EOF
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploying container..."
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'vagrant-ssh-key',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e

sudo docker stop ${CONTAINER_NAME} || true
sudo docker rm ${CONTAINER_NAME} || true

sudo docker run -d \\
  -p 8083:8083 \\
  --name ${CONTAINER_NAME} \\
  --restart unless-stopped \\
  ${APP_NAME}

sudo docker ps | grep ${CONTAINER_NAME}

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
