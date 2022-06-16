package com.company.aws.example.product.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Slf4j
@Configuration
public class AppConfig {

    @Value("${amazon.dynamodb.endpoint:}")
    private String dynamoDbEndpoint;

    @Value("${amazon.dynamodb.region:}")
    private String dynamoDbRegion;

    @Bean
    public DynamoDbClient getDynamoDbClient() {
        log.info("Start getDynamoDbClient()");

        log.info("dynamoDbEndpoint={}", dynamoDbEndpoint);
        log.info("dynamoDbRegion={}", dynamoDbRegion);
        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        if (StringUtils.hasText(dynamoDbEndpoint)) {
            log.info("Using local environment");
            log.info("Using endpointOverride [{}]", dynamoDbEndpoint);
            log.info("Using region [{}]", dynamoDbRegion);
            URI dbUri;
            try {
                dbUri = new URI(dynamoDbEndpoint);
            } catch (URISyntaxException Uex) {
                log.error("DynamoDB endpoint is malformed [{}]", dynamoDbEndpoint);
                throw new BeanCreationException("Could not initialize dynamoDB");
            }
            builder = builder
                    .httpClient(UrlConnectionHttpClient.builder().build())
                    .region(Region.of(dynamoDbRegion))
                    .endpointOverride(dbUri);
        } else {
            log.info("Using AWS environment");
            String amazonAwsRegionString = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
            log.info("Using amazonAwsRegion [{}]", amazonAwsRegionString);
            builder = builder
                    .httpClient(UrlConnectionHttpClient.builder().build())
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(amazonAwsRegionString))
                    .overrideConfiguration(ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build());
        }
        DynamoDbClient client = builder.build();
        log.info("Finish getDynamoDbClient()");
        return client;
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        return objectMapper;
    }

}
