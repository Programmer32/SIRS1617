rm -rv keys_*
cd ..

for folder in */
do
	cd $folder
	for extension in .pem.txt .cer .jks .key
	do
		rm -rv ./src/main/resources/*$extension
	done
	cd ..
done
