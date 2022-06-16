package com.company.aws.example.product.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.company.aws.example.product.controller.ProductController;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeleteProductFunction implements AbstractProductFunction {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductController productController;

    public ResponseEntity<?> process(APIGatewayProxyRequestEvent requestEvent) throws Exception {
        String id = requestEvent.getPathParameters().get("id");
        return productController.deleteProduct(id);
    }

    public HttpMethod getRestMethod() {
        return HttpMethod.DELETE;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Logger getLog() {
        return log;
    }

}
