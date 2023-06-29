package com.vlados.codit.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlados.codit.productservice.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    private static final String DOCKER_CONTAINER_VERSION = "mongo";
    private static final String URI_PROPERTY = "spring.data.mongodb.uri";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DOCKER_CONTAINER_VERSION);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(URI_PROPERTY, mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void checkResponseHttpStatusWhenCreatingAProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products").
        contentType(MediaType.APPLICATION_JSON).
        content(mapper.writeValueAsString(getProductRequest()))).
                andExpect(status().isCreated());

    }


    private ProductRequest getProductRequest() {
        return ProductRequest.builder().
                name("Test").desc("Test")
                .price(new BigDecimal(12345))
                .build();
    }

}
