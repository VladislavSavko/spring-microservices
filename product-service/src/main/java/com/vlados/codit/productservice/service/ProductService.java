package com.vlados.codit.productservice.service;

import com.vlados.codit.productservice.dto.ProductRequest;
import com.vlados.codit.productservice.dto.ProductResponse;
import com.vlados.codit.productservice.model.Product;
import com.vlados.codit.productservice.repository.ProductRepos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ProductRepos productRepository;


    public ProductService(ProductRepos productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest pr) {
        Product product = Product.builder().
                name(pr.getName()).
                desc(pr.getDesc()).
                price(pr.getPrice()).
                build();

        productRepository.save(product);
        log.info(String.format("Product %s was created and added to db successfully!", product));
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().
                stream().
                map(this::mapToProductResponse).
                collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder().
                id(product.getId()).
                desc(product.getDesc()).
                name(product.getName()).
                price(product.getPrice()).
                build();
    }
}
