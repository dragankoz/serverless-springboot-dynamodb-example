aws dynamodb create-table --cli-input-json file://create-table.json --endpoint-url http://localhost:8000
aws dynamodb list-tables --endpoint-url http://localhost:8000
REM aws dynamodb describe-table --table-name ProductsTable --endpoint-url http://localhost:8000