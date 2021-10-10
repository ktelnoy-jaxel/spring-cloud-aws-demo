### DynamoDB
Insert item via CLI
```shell
aws dynamodb put-item \
--table-name TestTable \
--item '{"PK": {"S": "1"}, "SK": {"S": "2"}, "Color": {"SS": ["Red"]}}' \
--return-values ALL_NEW
```
Update item with new array element via CLI
```shell
aws dynamodb update-item \
--table-name TestTable \
--key '{"PK": {"S": "1"}, "SK": {"S": "2"}}' \
--update-expression "ADD Color :c" \
--expression-attribute-values '{":c": {"SS":["Red"]}}' \
--return-values ALL_NEW
```
Update a set of items with new array element via CLI
```shell
aws dynamodb update-item \
--table-name TestTable \
--key '{"PK": {"S": "1"}, "SK": {"S": "2"}}' \
--update-expression "ADD Color :c" \
--expression-attribute-values '{":c": {"SS":["Red"]}}' \
--return-values ALL_NEW
```