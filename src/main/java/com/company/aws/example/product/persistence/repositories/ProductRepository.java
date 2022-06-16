package com.company.aws.example.product.persistence.repositories;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.aws.example.product.persistence.entity.Product;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

@Repository
public class ProductRepository {
    // Value can come from either external (aws lambda env) or local development (application.properties)
    public static final String TABLE_NAME = System.getenv().getOrDefault("PRODUCT_TABLE_NAME", "ProductsTable");

    // Workaround for https://github.com/oracle/graal/issues/3386
    public static final TableSchema<Product> TABLE_SCHEMA =
            StaticTableSchema.builder(Product.class)
                    .newItemSupplier(Product::new)
                    .addAttribute(String.class, a -> a.name("PK")
                            .getter(Product::getId)
                            .setter(Product::setId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("name")
                            .getter(Product::getName)
                            .setter(Product::setName))
                    .addAttribute(BigDecimal.class, a -> a.name("price")
                            .getter(Product::getPrice)
                            .setter(Product::setPrice))
                    .build();

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public ProductRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    public List<Product> findAll() {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TABLE_SCHEMA) // TableSchema.fromBean(Product.class))
                .scan().items().stream().collect(Collectors.toList());
    }

    public boolean save(Product product) {
        boolean updated = false;
        if (findById(product.getId()) != null) {
            dynamoDbEnhancedClient.table(TABLE_NAME, TABLE_SCHEMA) // TableSchema.fromBean(Product.class))
                    .updateItem(product);
            updated = true;
        } else {
            dynamoDbEnhancedClient.table(TABLE_NAME, TABLE_SCHEMA) // TableSchema.fromBean(Product.class))
                    .putItem(product);
        }
        return updated;
    }

    public void delete(String id) {
        dynamoDbEnhancedClient.table(TABLE_NAME, TABLE_SCHEMA) // TableSchema.fromBean(Product.class))
                .deleteItem(Key.builder().partitionValue(id).build());
    }

    public Product findById(String id) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TABLE_SCHEMA) // TableSchema.fromBean(Product.class))
                .getItem(GetItemEnhancedRequest.builder().key(Key.builder().partitionValue(id).build()).build());
    }
}
