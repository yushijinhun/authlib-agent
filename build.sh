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

echo Downloading LaunchWrapper...
url="https://libraries.minecraft.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar"

if [ ! -e "./authlib-javaagent/launchwrapper-1.12.jar" ]
then
    wget $url -O ./authlib-javaagent/launchwrapper-1.12.jar
fi

echo Building...
mvn clean package

echo

./scripts/restore-config.sh

cp ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar authlibagent.jar
echo ./authlib-javaagent/target/authlib-javaagent-*-jar-with-dependencies.jar is saved to authlibagent.jar

cp ./yggdrasil-backend/target/yggdrasil-backend-*.war yggdrasil-backend.war
echo ./yggdrasil-backend/target/yggdrasil-backend-*.war is saved to yggdrasil-backend.war

