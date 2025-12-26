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

        // Correct JAVA_HOME path
        JAVA_HOME_PATH = "/usr/lib/jvm/java-21-openjdk-amd64"
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
                echo "🛠️ Installing Java 21 if missing, building project, and running tests..."
                withCredentials([sshUserPrivateKey(credentialsId: 'vagrant-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e
set -o pipefail

# Install OpenJDK 21 if not installed
if ! java -version 2>/dev/null | grep "21" >/dev/null; then
    echo "☕ Installing OpenJDK 21..."
    sudo add-apt-repository ppa:openjdk-r/ppa -y
    sudo apt-get update -y
    sudo apt-get install -y openjdk-21-jdk
fi

# Set correct JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=\$JAVA_HOME/bin:\$PATH

echo "✅ JAVA_HOME set to \$JAVA_HOME"
java -version
javac -version

# Clone repo if not exists, else update
if [ -d "${APP_DIR}" ]; then
    cd ${APP_DIR}
    git fetch origin
    git reset --hard origin/main
else
    git clone https://github.com/seang454/ipub-backend-full-stack.git ${APP_DIR}
    cd ${APP_DIR}
fi

chmod +x gradlew

# Build and test using correct JAVA_HOME
JAVA_HOME=\$JAVA_HOME PATH=\$PATH ./gradlew clean build --no-daemon --parallel
JAVA_HOME=\$JAVA_HOME PATH=\$PATH ./gradlew test --no-daemon
EOF
                    """
                }
            }
        }

        stage('Delivery') {
            steps {
                echo "📦 Building Docker image..."
                withCredentials([sshUserPrivateKey(credentialsId: 'vagrant-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e

# Install Docker if not installed
if ! command -v docker >/dev/null 2>&1; then
    echo "🐳 Docker not found, installing..."
    sudo apt-get update -y
    sudo apt-get install -y docker.io
    sudo systemctl enable --now docker
fi

# Remove old image if exists
sudo docker rmi ${APP_NAME} || true

# Build new Docker image
sudo docker build -t ${APP_NAME} ${APP_DIR}
EOF
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploying container..."
                withCredentials([sshUserPrivateKey(credentialsId: 'vagrant-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e

# Stop and remove old container
sudo docker stop ${CONTAINER_NAME} || true
sudo docker rm ${CONTAINER_NAME} || true

# Run new container
sudo docker run -d -p 8080:8080 \
  --name ${CONTAINER_NAME} \
  --restart unless-stopped \
  ${APP_NAME}

# Show running container
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
