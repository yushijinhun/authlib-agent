#!/bin/bash

echo Loading configurations...
source ./configure.sh

echo Generating RSA key pair...
./scripts/generate-key.sh
cp ./privatekey.der ./yggdrasil-backend/src/main/resources/signature_key.der
cp ./publickey.der ./authlib-javaagent/src/main/resources/new_yggdrasil_session_pubkey.der

echo Writing configurations...
export AGENT_CONF=./authlib-javaagent/config.properties
export BACKEND_CONF=./yggdrasil-backend/config.properties
./scripts/backup-config.sh
./scripts/write-config.sh

echo Building...
mvn clean install

echo

./scripts/restore-config.sh

cp ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar authlibagent.jar
echo ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar is saved to authlibagent.jar

cp ./yggdrasil-backend/target/yggdrasil-backend-*.war yggdrasil-backend.war
echo ./yggdrasil-backend/target/yggdrasil-backend-*.war is saved to yggdrasil-backend.war

