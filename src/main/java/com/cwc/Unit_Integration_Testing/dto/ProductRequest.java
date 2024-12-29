package com.cwc.Unit_Integration_Testing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private double price;
}
