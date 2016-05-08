#!/bin/bash

CAHOME=~/.my-own-CA

if [ "$1" = "" ]
then

echo Creating the CA...

# Note, if $CAHOME exists, will over write...
mkdir -p $CAHOME

# Generate root CA key
openssl genrsa -out $CAHOME/rootCA.key 2048

# Create an X.509 cert from the CA key
openssl req -x509 -sha256 -nodes -days 180 -newkey rsa:2048 -key $CAHOME/rootCA.key -out $CAHOME/rootCA.crt

# Create a password protected PFX file, useful for importing, moving around, etc.
openssl pkcs12 -export -out $CAHOME/rootCA.pfx -inkey $CAHOME/rootCA.key -in $CAHOME/rootCA.crt

ls -alF $CAHOME

else

echo Creating a cert from CA...

# Generate user key
openssl genrsa -out "$1.key" 2048

# Create certificate request
openssl req -new -key "$1.key" -out "$1.csr"

# Sign and generate the user certificate from the
openssl x509 -req -in "$1.csr" -CA $CAHOME/rootCA.crt -CAkey $CAHOME/rootCA.key -CAcreateserial -out "$1.crt" -days 500

echo
echo You should probably pick a password rather than leaving it blank...

# Export as password protected PFX file
openssl pkcs12 -export -out "$1.pfx" -inkey "$1.key" -in "$1.crt"

fi

