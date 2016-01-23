#!/bin/sh

echo Loading configurations...
source ./configure.sh

echo Generating RSA key pair...
./scripts/generate-key.sh
cp ./privatekey.der ./yggdrasil-backend/yggdrasil-backend-impl/src/main/resources/signature_key.der
cp ./publickey.der ./authlib-javaagent/src/main/resources/yggdrasil_session_pubkey.der

echo Writing configurations...
export AGENT_CONF=./authlib-javaagent/config.properties
export BACKEND_CONF=./yggdrasil-backend/yggdrasil-backend-impl/config.properties
export WEB_CONF=./yggdrasil-backend/yggdrasil-web-impl/config.properties
./scripts/write-config.sh

echo Building...
mvn clean install

cp ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar authlibagent.jar
echo ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar is saved authlibagent.jar

cp ./yggdrasil-backend/yggdrasil-backend-impl/target/yggdrasil-backend-impl-*.war yggdrasil-backend.war
echo ./yggdrasil-backend/yggdrasil-backend-impl/target/yggdrasil-backend-impl-*.war is saved yggdrasil-backend.war

cp ./yggdrasil-backend/yggdrasil-web-impl/target/yggdrasil-web-impl-*.war yggdrasil-web.war
echo ./yggdrasil-backend/yggdrasil-web-impl/target/yggdrasil-web-impl-*.war is saved yggdrasil-web.war
