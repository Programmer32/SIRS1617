#!/bin/bash

echo -e "\e[33;1mCleaning Up\e[0m"
rm -rv keys_*

echo -e "\e[33;1mGenerating Keys\e[0m"
./gen-ca-servers-keys.sh UpaBroker UpaTransporter{1..9} TransporterClient


echo -e "\e[33;1mInner Deploy\e[0m"
cp -vf keys_*/ca/ca-certificate.pem.txt ./src/main/resources/
for certificate in keys_*/*/*.cer
do
	cp -vf $certificate ./src/main/resources/
done

cd keys_*
Resources=src/main/resources/


Folder=TransporterClient
echo -e "\e[33;1mDeploy $Folder\e[0m"
(cd $Folder; cp -vf $Folder.jks ../../../transporter-ws-cli/$Resources )
Folder=UpaBroker
echo -e "\e[33;1mDeploy $Folder\e[0m"
(cd $Folder; cp -vf $Folder.jks ../../../broker-ws/$Resources )
Folder=UpaTransporter*
for Folder in UpaTransporter*
do
	echo -e "\e[33;1mDeploy $Folder\e[0m"
	(cd $Folder; cp -vf $Folder*.jks ../../../transporter-ws/$Resources )
done
cd ..

#cp -v src/main/resources/UpaBrokerpriv.key ../broker-ws/src/main/resources/
#cp -v src/main/resources/UpaTransporter*priv.key ../transporter-ws/src/main/resources/
#cp -v src/main/resources/TransporterClient*priv.key ../transporter-ws-cli/src/main/resources/
