package com.cwc.Unit_Integration_Testing.util;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.entity.Product;

public class AppUtil {
    public static Product convertToProduct(ProductRequest productRequest)
    {
        return Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
    }
    public static ProductResponse convertToProductResponse(Product product)
    {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
