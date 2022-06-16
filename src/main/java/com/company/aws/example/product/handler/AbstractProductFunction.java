package com.company.aws.example.product.handler;

import java.util.function.Function;

import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.company.aws.example.product.util.AppUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface AbstractProductFunction extends Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    default APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent requestEvent) {
        getLog().info("Start apply()");
        long startTime = System.currentTimeMillis();
        APIGatewayProxyResponseEvent event;
        try {
            checkRestMethod(requestEvent);
            event = new APIGatewayProxyResponseEvent();
            ResponseEntity<?> response = process(requestEvent);
            event.setStatusCode(response.getStatusCodeValue());
            event.setHeaders(response.getHeaders().toSingleValueMap());
            if (response.getBody() != null) {
                event.setBody(getObjectMapper().writeValueAsString(response.getBody()));
            }
        } catch (HttpClientErrorException ex) {
            event = AppUtil.logErrorResponseEvent(ex.getStatusCode(), ex.getCause() != null ? ex.getCause(): ex, getObjectMapper());
        } catch (Exception ex) {
            event = AppUtil.logErrorResponseEvent(HttpStatus.INTERNAL_SERVER_ERROR, ex, getObjectMapper());
        }

        getLog().info("Finish apply() {}ms", System.currentTimeMillis() - startTime);
        return event;
    }

    ResponseEntity<?> process(APIGatewayProxyRequestEvent requestEvent) throws Exception;

    ObjectMapper getObjectMapper();

    HttpMethod getRestMethod();

    Logger getLog();

    private void checkRestMethod(APIGatewayProxyRequestEvent requestEvent) {
        if (!requestEvent.getHttpMethod().equals(getRestMethod().name())) {
            throw new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}
