package com.company.aws.example.product;

import java.util.HashSet;

import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.TypeHint;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.xray.entities.TraceHeader;
import com.amazonaws.xray.entities.TraceID;
import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.company.aws.example.product.handler.CreateUpdateProductFunction;
import com.company.aws.example.product.handler.DeleteProductFunction;
import com.company.aws.example.product.handler.GetProductByIdFunction;
import com.company.aws.example.product.handler.GetProductsFunction;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@TypeHint(types = {
        DateTime.class,
        APIGatewayProxyRequestEvent.class,
        TracingInterceptor.class,
        HashSet.class,
        TraceHeader.class,
        TraceID.class
},
        typeNames = {
                "com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent$ProxyRequestContext",
                "com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent$RequestIdentity",
                "com.amazonaws.xray.entities.TraceHeader$SampleDecision"
        })
@Slf4j
public class SpringBootApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

    @Bean
    public GetProductsFunction getProducts() {
        return new GetProductsFunction();
    }

    @Bean
    public GetProductByIdFunction getProductById() {
        return new GetProductByIdFunction();
    }

    @Bean
    public CreateUpdateProductFunction createUpdateProduct() {
        return new CreateUpdateProductFunction();
    }

    @Bean
    public DeleteProductFunction deleteProduct() {
        return new DeleteProductFunction();
    }
}
