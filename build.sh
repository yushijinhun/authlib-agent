#!/bin/bash

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
./scripts/backup-config.sh
./scripts/write-config.sh

echo Building...
mvn clean install

echo

./scripts/restore-config.sh

cp ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar authlibagent.jar
echo ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar is saved to authlibagent.jar

cp ./yggdrasil-backend/yggdrasil-backend-impl/target/yggdrasil-backend-impl-*.war yggdrasil-backend.war
echo ./yggdrasil-backend/yggdrasil-backend-impl/target/yggdrasil-backend-impl-*.war is saved to yggdrasil-backend.war

cp ./yggdrasil-backend/yggdrasil-web-impl/target/yggdrasil-web-impl-*.war yggdrasil-web.war
echo ./yggdrasil-backend/yggdrasil-web-impl/target/yggdrasil-web-impl-*.war is saved to yggdrasil-web.war

cp ./yggdrasil-backend/yggdrasil-web-api/target/yggdrasil-web-api-*.jar yggdrasil-web-api.jar
echo ./yggdrasil-backend/yggdrasil-web-api/target/yggdrasil-web-api-*.jar is saved to yggdrasil-web-api.jar
