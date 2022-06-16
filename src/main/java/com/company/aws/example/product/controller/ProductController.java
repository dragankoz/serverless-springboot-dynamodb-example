package com.company.aws.example.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.company.aws.example.product.data.MessageResponse;
import com.company.aws.example.product.data.Products;
import com.company.aws.example.product.persistence.entity.Product;
import com.company.aws.example.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/products")
@Tag(name = "product", description = "Operations pertaining to product service")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // https://github.com/swagger-api/swagger-core/issues/4194
    @Operation(summary = "Get all products",extensions = {
                    @Extension(name = "x-amazon-apigateway-integration", properties = {
                            @ExtensionProperty(name = "type", value = "aws_proxy"),
                            @ExtensionProperty(name = "httpMethod", value = "POST"),
                            @ExtensionProperty(name = "uri", value = "EMPTY_STRING"),
                            @ExtensionProperty(name = "Fn::Sub", value = "arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${GetProductsFunction.Arn}/invocations"),
                    })
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProducts() {
        try {
            Products products = productService.findAll();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    // https://github.com/swagger-api/swagger-core/issues/4194
    @Operation(summary = "Get product by id",extensions = {
                    @Extension(name = "x-amazon-apigateway-integration", properties = {
                            @ExtensionProperty(name = "type", value = "aws_proxy"),
                            @ExtensionProperty(name = "httpMethod", value = "POST"),
                            @ExtensionProperty(name = "uri", value = "EMPTY_STRING"),
                            @ExtensionProperty(name = "Fn::Sub", value = "arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${GetProductByIdFunction.Arn}/invocations"),
                    })
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            Product product = productService.findById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    // https://github.com/swagger-api/swagger-core/issues/4194
    @Operation(summary = "Add/update product by id", extensions = {
                    @Extension(name = "x-amazon-apigateway-integration", properties = {
                            @ExtensionProperty(name = "type", value = "aws_proxy"),
                            @ExtensionProperty(name = "httpMethod", value = "POST"),
                            @ExtensionProperty(name = "uri", value = "EMPTY_STRING"),
                            @ExtensionProperty(name = "Fn::Sub", value = "arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${CreateUpdateProductFunction.Arn}/invocations"),
                    })
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "202"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUpdateProduct(@PathVariable String id, @RequestBody Product product) {
        try {
            return productService.createUpdate(id, product) ?
                    new ResponseEntity<>(new MessageResponse(HttpStatus.ACCEPTED.getReasonPhrase(), String.format("Product [%s] updated", id)), HttpStatus.ACCEPTED) :
                    new ResponseEntity<>(new MessageResponse(HttpStatus.CREATED.getReasonPhrase(), String.format("Product [%s] created", id)), HttpStatus.CREATED);
        } catch (Exception ex) {
            return handleException(ex);
        }

    }

    // https://github.com/swagger-api/swagger-core/issues/4194
    @Operation(summary = "Delete product by id", extensions = {
                    @Extension(name = "x-amazon-apigateway-integration", properties = {
                            @ExtensionProperty(name = "type", value = "aws_proxy"),
                            @ExtensionProperty(name = "httpMethod", value = "POST"),
                            @ExtensionProperty(name = "uri", value = "EMPTY_STRING"),
                            @ExtensionProperty(name = "Fn::Sub", value = "arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${DeleteProductFunction.Arn}/invocations"),
                    })
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            productService.delete(id);
            return new ResponseEntity<>(new MessageResponse(HttpStatus.OK.getReasonPhrase(), String.format("Product with id [%s] deleted", id)), HttpStatus.OK);
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

    /**
     * Manage exceptions here since we cant use controlleradvice because it starts up embedded tomcat
     *
     * @param ex
     * @return
     */
    private ResponseEntity<?> handleException(Exception ex) {
        if (ex instanceof HttpClientErrorException) {
            HttpStatus httpStatus = HttpStatus.valueOf(((HttpClientErrorException) ex).getRawStatusCode());
            String detail = ex.getCause() != null && ex.getCause().getMessage() != null ? ex.getCause().getMessage() : ((HttpClientErrorException) ex).getStatusText();
            MessageResponse response = new MessageResponse(httpStatus.getReasonPhrase(), detail);
            return new ResponseEntity<>(response, httpStatus);
        } else {
            MessageResponse response = new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
