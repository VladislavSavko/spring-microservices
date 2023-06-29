package com.vlados.codit.productservice.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class Product {

    @Id
    private String id;
    private String name;
    private String desc;
    private BigDecimal price;

}
