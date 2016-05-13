#!/bin/bash
./gen-ca-servers-keys.sh UpaBroker UpaTransporter{1..9} TransporterClient

cp -v keys_*/ca/ca-certificate.pem.txt ./src/main/resources/
for certificate in keys_*/*/*.cer
do
	cp -v $certificate ./src/main/resources/
done

cd keys_*
Resources=src/main/resources/


Folder=TransporterClient
(cd $Folder; cp -v $Folder.jks ../../../transporter-ws-cli/$Resources )
Folder=UpaBroker
(cd $Folder; cp -v $Folder.jks ../../../broker-ws/$Resources )
Folder=UpaTransporter*
for Folder in UpaTransporter*
do
	(cd $Folder; cp -v $Folder*.jks ../../../transporter-ws/$Resources )
done
cd ..

#cp -v src/main/resources/UpaBrokerpriv.key ../broker-ws/src/main/resources/
#cp -v src/main/resources/UpaTransporter*priv.key ../transporter-ws/src/main/resources/
#cp -v src/main/resources/TransporterClient*priv.key ../transporter-ws-cli/src/main/resources/
