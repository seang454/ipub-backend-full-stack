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

        stage('Build & Test') {
            steps {
                echo "🛠️ Building project with Gradle..."
                withCredentials([sshUserPrivateKey(credentialsId: 'vagrant-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e
set -o pipefail

# Ensure OpenJDK 21 is installed
if ! java -version 2>/dev/null | grep "21" >/dev/null; then
    sudo add-apt-repository ppa:openjdk-r/ppa -y
    sudo apt-get update -y
    sudo apt-get install -y openjdk-21-jdk
fi

# Set JAVA_HOME and PATH
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$JAVA_HOME/bin:$PATH

echo "✅ JAVA_HOME = $JAVA_HOME"
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

# Make Gradle wrapper executable
chmod +x gradlew

# Stop previous Gradle daemons and clean build
./gradlew --stop
./gradlew clean build -x test --no-daemon --parallel

EOF
                    """
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo "🐳 Building Docker image..."
                withCredentials([sshUserPrivateKey(credentialsId: 'vagrant-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                    ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << 'EOF'
set -e

# Install Docker if missing
if ! command -v docker >/dev/null 2>&1; then
    sudo apt-get update -y
    sudo apt-get install -y docker.io
    sudo systemctl enable --now docker
fi

# Remove old image
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

# Stop & remove old container
sudo docker stop ${CONTAINER_NAME} || true
sudo docker rm ${CONTAINER_NAME} || true

# Run new container
sudo docker run -d -p 8083:8083 \
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
