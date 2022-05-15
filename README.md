# Java akka Sample

## Configuration

### application.conf

- Run on inmemory
```shell
include classpath("persistence/inmemory.conf")
# include classpath("persistence/local.conf")
```

- Run on LevelDB
```shell
# include classpath("persistence/inmemory.conf")
include classpath("persistence/local.conf")
```

## API

### Create order

```shell
curl --location --request POST 'localhost:8081/api/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountId" : "test-account-id",
    "detail" : {
        "items" : []
    }
}'
```

### Make order shipped
```shell
curl --location --request POST 'localhost:8081/api/orders/{order-id}/make-shipped'
```

### Cancel order
```shell
curl --location --request POST 'localhost:8081/api/orders/{order-id}/cancel'
```

### Cancel due to discontinuation
```shell
curl --location --request POST 'localhost:8081/api/orders/{order-id}/cancel-discontinuation'
```
