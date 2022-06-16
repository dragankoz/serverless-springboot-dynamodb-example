package com.company.aws.example.product.data;

import java.util.ArrayList;
import java.util.List;

import com.company.aws.example.product.persistence.entity.Product;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Products {
    private List<Product> products = new ArrayList<>();

    public Products() {
    }

    public Products(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

