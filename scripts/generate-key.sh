#!/bin/bash

# Environment variables:
# KEY_BITS - the bits of the key
#
# Output files:
# key.pem - the generated rsa key pair
# publickey.der - the SubjectPublicKeyInfo
# privatekey.der - the PrivateKeyInfo

if [ -e key.pem ]
then
	echo key.pem already exists, using existing key.
else
	openssl genrsa -out key.pem $KEY_BITS -batch
fi

openssl req -new -x509 -key key.pem -out cert.pem -batch
ASN_CERT=`openssl asn1parse -in cert.pem`
SUB_OFFSET=`echo $ASN_CERT|grep -P -o '\d+(?=:d=\d+\s*hl=\d+\s*l=\s*\d+\s*cons:\s*SEQUENCE\s*\d+:d=\d+\s*hl=\d+\s*l=\s*\d+\s*cons:\s*SEQUENCE\s*\d+:d=\d+\s*hl=\d+\s*l=\s*\d+\s*prim:\s*OBJECT\s*:rsaEncryption)'`
SUB_LENGTH=`openssl asn1parse -in cert.pem -offset $SUB_OFFSET|grep -P -o '\d+(?=:d=\d+\s*hl=\d+\s*l=\s*\d+\s*cons:\s*cont\s*\[\s*\d+\s*\])'`
openssl asn1parse -in cert.pem -out publickey.der -offset $SUB_OFFSET -length $SUB_LENGTH -noout
openssl pkcs8 -in key.pem -out privatekey.der -outform der -topk8 -nocrypt

# cleanup
rm cert.pem
