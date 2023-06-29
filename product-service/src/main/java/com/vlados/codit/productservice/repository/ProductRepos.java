package com.vlados.codit.productservice.repository;

import com.vlados.codit.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepos extends MongoRepository<Product, String> {
}
