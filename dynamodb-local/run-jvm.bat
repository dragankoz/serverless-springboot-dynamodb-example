"C:\Program files\git\bin\bash" -c ./download-dbruntime.sh
java -Djava.library.path=.\temp\DynamoDBLocal_lib -jar temp\DynamoDBLocal.jar -sharedDb -dbPath temp
