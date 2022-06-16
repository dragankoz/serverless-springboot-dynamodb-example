package com.company.aws.example.product.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.company.aws.example.product.data.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppUtil {

    /**
     * ALlow serialization not to fail as a last resort provide the message from message response
     *
     * @param messageResponse
     * @return
     */
    public static String writeAsJson(ObjectMapper objectMapper, MessageResponse messageResponse) {
        String ret;
        try {
            ret = objectMapper.writeValueAsString(messageResponse);
        } catch (Exception ex) {
            log.error("Failed to serialized messageResponse", ex);
            ret = messageResponse.getMessageDetail();
        }
        return ret;
    }

    public static APIGatewayProxyResponseEvent logErrorResponseEvent(HttpStatus httpStatus, Throwable ex, ObjectMapper objectMapper) {
        MessageResponse response = new MessageResponse(httpStatus.getReasonPhrase(), ex.getMessage());
        log.error(response.getReason(), ex);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(httpStatus.value())
                .withBody(AppUtil.writeAsJson(objectMapper, response));
    }


    public static ResponseEntity<?> logErrorResponseEntity(HttpStatus httpStatus, Exception ex) {
        MessageResponse response = new MessageResponse(httpStatus.getReasonPhrase(), ex.getMessage());
        log.error(response.getReason(), ex);
        return new ResponseEntity<>(new MessageResponse(httpStatus.getReasonPhrase(), ex.getMessage()),httpStatus);
    }
}
