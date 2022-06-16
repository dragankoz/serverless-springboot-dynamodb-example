package com.company.aws.example.product.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.company.aws.example.product.data.Products;
import com.company.aws.example.product.persistence.entity.Product;
import com.company.aws.example.product.persistence.repositories.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(String id) {
        log.info("Start findById() [id={}]", id);
        long startTime = System.currentTimeMillis();
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("Product with id [%s] not found", id));
        }
        log.info("Finish findById() {}ms", System.currentTimeMillis() - startTime);
        return product;
    }

    public boolean createUpdate(String id, Product product) {
        log.info("Start createUpdate() [{}]", product.toString());
        long startTime = System.currentTimeMillis();
        if (product.getId() == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Product id is missing");
        }
        if (!product.getId().equals(id)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Product id path parameter does not match product id value");
        }
        boolean ret = productRepository.save(product);
        log.info("Finish createUpdate() {}ms", System.currentTimeMillis() - startTime);
        return ret;
    }

    public void delete(String id) {
        log.info("Start delete() [id={}]", id);
        long startTime = System.currentTimeMillis();
        productRepository.findById(id);
        productRepository.delete(id);
        log.info("Finish delete() {}ms", System.currentTimeMillis() - startTime);
    }

    public Products findAll() {
        log.info("Start getAll()");
        long startTime = System.currentTimeMillis();
        Products products = new Products(productRepository.findAll());
        log.info("Finish getAll() {}ms", System.currentTimeMillis() - startTime);
        return products;
    }
}
