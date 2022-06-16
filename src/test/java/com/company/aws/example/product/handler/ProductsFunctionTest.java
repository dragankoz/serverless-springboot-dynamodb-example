package com.company.aws.example.product.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.aws.example.product.SpringBootApp;
import com.company.aws.example.product.persistence.entity.Product;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootApp.class)
public class ProductsFunctionTest extends AbstractProductsFunctionTest {

    @Autowired
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private GetProductsFunction getProductsFunction;

    @Autowired
    private GetProductByIdFunction getProductByIdFunction;

    @Autowired
    private DeleteProductFunction deleteProductFunction;

    @Autowired
    private CreateUpdateProductFunction createUpdateProductFunction;

    public static final String productId = "ea50587a-c205-11ec-9d64-0242ac120002";

    private CustomComparator jsonIgnores = new CustomComparator(JSONCompareMode.STRICT);     // new Customization("**.id", (o1, o2) -> o1 != null && o2 != null));

    @Test
    public void testGetProducts() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(getProductsFunction.getRestMethod().name());
        APIGatewayProxyResponseEvent responseEvent = getProductsFunction.apply(requestEvent);
        assertEquals(HttpStatus.OK.value(), responseEvent.getStatusCode());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    @Test
    public void testCreateProduct() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(createUpdateProductFunction.getRestMethod().name());
        requestEvent.setBody(objectMapper.writeValueAsString(new Product(productId, "Red hat", new BigDecimal("19.23"))));
        requestEvent.setPathParameters(Map.of("id", productId));
        APIGatewayProxyResponseEvent responseEvent = createUpdateProductFunction.apply(requestEvent);
        assertEquals(HttpStatus.CREATED.value(), responseEvent.getStatusCode());
        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    @Test
    public void testUpdateProduct() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        testCreateProduct();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(createUpdateProductFunction.getRestMethod().name());
        requestEvent.setBody(objectMapper.writeValueAsString(new Product(productId, "Blue hat", new BigDecimal("20.23"))));
        requestEvent.setPathParameters(Map.of("id", productId));
        APIGatewayProxyResponseEvent responseEvent = createUpdateProductFunction.apply(requestEvent);
        assertEquals(HttpStatus.ACCEPTED.value(), responseEvent.getStatusCode());
        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    @Test
    public void testDeleteProduct() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        testCreateProduct();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(deleteProductFunction.getRestMethod().name());
        requestEvent.setPathParameters(Map.of("id", productId));
        APIGatewayProxyResponseEvent responseEvent = deleteProductFunction.apply(requestEvent);
        assertEquals(HttpStatus.OK.value(), responseEvent.getStatusCode());

        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    @Test
    public void testGetProductById() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        testCreateProduct();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(getProductByIdFunction.getRestMethod().name());
        requestEvent.setPathParameters(Map.of("id", productId));
        APIGatewayProxyResponseEvent responseEvent = getProductByIdFunction.apply(requestEvent);
        assertEquals(HttpStatus.OK.value(), responseEvent.getStatusCode());
        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    @Test
    public void testCreateProductWithWrongIdProductBody() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(createUpdateProductFunction.getRestMethod().name());
        requestEvent.setBody(objectMapper.writeValueAsString(new Product("xx50587a-c205-11ec-9d64-0242ac120002", "Red hat", new BigDecimal("19.23"))));
        requestEvent.setPathParameters(Map.of("id", productId));
        APIGatewayProxyResponseEvent responseEvent = createUpdateProductFunction.apply(requestEvent);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEvent.getStatusCode());
        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }
    @Test
    public void testCreateProductWithWrongId() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setHttpMethod(createUpdateProductFunction.getRestMethod().name());
        requestEvent.setBody(objectMapper.writeValueAsString(new Product(productId, "Red hat", new BigDecimal("19.23"))));
        requestEvent.setPathParameters(Map.of("id", "xx50587a-c205-11ec-9d64-0242ac120002"));
        APIGatewayProxyResponseEvent responseEvent = createUpdateProductFunction.apply(requestEvent);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEvent.getStatusCode());
        log.info("Body: {}", responseEvent.getBody());
        JSONAssert.assertEquals(getTestContent(getClass(), methodName, "json"), responseEvent.getBody(), jsonIgnores);
    }

    protected DynamoDbEnhancedClient getDbClient() {
        return dynamoDbEnhancedClient;
    }
}