#!/bin/bash
if [ ! -d "temp" ]
then
  mkdir -p ../target
  curl --insecure  https://s3.ap-southeast-1.amazonaws.com/dynamodb-local-singapore/dynamodb_local_2022-01-10.zip --output ../target/dynamodb_local_2022-01-10.zip
  unzip ../target/dynamodb_local_2022-01-10.zip -d temp
fi

