stage('Build & Test') {
    steps {
        echo "🛠️ Installing Java 21 if missing, building and testing..."
        withCredentials([sshUserPrivateKey(
            credentialsId: 'vagrant-ssh-key',
            keyFileVariable: 'SSH_KEY'
        )]) {
            sh """
            ssh -i \$SSH_KEY -p ${SSH_PORT} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} << EOF
set -e
set -o pipefail

# Install OpenJDK 21 if not installed
if ! java -version 2>/dev/null | grep "21" >/dev/null; then
    echo "☕ Installing OpenJDK 21..."
    sudo add-apt-repository ppa:openjdk-r/ppa -y
    sudo apt-get update -y
    sudo apt-get install -y openjdk-21-jdk
fi

JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
PATH=\$JAVA_HOME/bin:\$PATH

# Verify
echo "JAVA_HOME=\$JAVA_HOME"
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

# Run Gradle build and test with correct JAVA_HOME
JAVA_HOME=\$JAVA_HOME PATH=\$PATH ./gradlew clean build --no-daemon --parallel
JAVA_HOME=\$JAVA_HOME PATH=\$PATH ./gradlew test --no-daemon
EOF
            """
        }
    }
}
