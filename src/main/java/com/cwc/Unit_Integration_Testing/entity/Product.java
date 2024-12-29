package com.cwc.Unit_Integration_Testing.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private double price;
}
