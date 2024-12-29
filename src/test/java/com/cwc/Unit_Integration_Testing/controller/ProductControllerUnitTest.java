package com.cwc.Unit_Integration_Testing.controller;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.entity.Product;
import com.cwc.Unit_Integration_Testing.exception.ProductNotFoundException;
import com.cwc.Unit_Integration_Testing.service.ProductService;
import com.cwc.Unit_Integration_Testing.util.AppUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerUnitTest {
    @Autowired
    private MockMvc mockMvc; // MockMmvc to test controller endpoints.
    @MockitoBean
    private ProductService productService;
    private Product product1;
    private Product product2;
    ProductResponse productResponse;
    private ProductRequest productRequest;

    private ObjectMapper objectMapper;

    private List<ProductResponse> productResponses;

    @BeforeEach
    void setUp() {

        productRequest = ProductRequest.builder()
                .name("Laptop")
                .description("this is a dell laptop.")
                .price(255.99)
                .build();
        product1 = Product.builder()
                .id("L-101")
                .name("Laptop")
                .description("This is a dell laptop.")
                .price(255.99)
                .build();
        product2 = Product.builder()
                .id("M-1001")
                .name("Mouse")
                .description("This is wireless mouse.")
                .price(55.99)
                .build();
        productResponse = ProductResponse.builder()
                .id("L-101")
                .name("Laptop")
                .description("This is a dell laptop.")
                .price(255.99)
                .build();
        productResponses = List.of(AppUtil.convertToProductResponse(product1),
                AppUtil.convertToProductResponse(product2));
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllProducts_Found() throws Exception {
        when(productService.findAllProducts()).thenReturn(productResponses);

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void testFindAllProducts_NotFound() throws Exception {
        when(productService.findAllProducts()).thenThrow(ProductNotFoundException.class);

        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testFindProductById_Found() throws Exception {
        when(productService.findProductById("L-101")).thenReturn(productResponse);

        mockMvc.perform(get("/api/v1/product/L-101"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value("L-101"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("This is a dell laptop."))
                .andExpect(jsonPath("$.price").value(255.99));
    }
    @Test
    void testFindProductById_NotFound() throws Exception {
        when(productService.findProductById("L-101")).thenThrow(ProductNotFoundException.class);

        mockMvc.perform(get("/api/v1/product/L-101"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindProductByName_Found() throws Exception {
        when(productService.findProductByName("Laptop")).thenReturn(productResponse);

        mockMvc.perform(get("/api/v1/product/name/Laptop"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value("L-101"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("This is a dell laptop."))
                .andExpect(jsonPath("$.price").value(255.99));
    }
    @Test
    void testFindProductByName_NotFound() throws Exception {
        when(productService.findProductByName("Laptop")).thenThrow(ProductNotFoundException.class);

        mockMvc.perform(get("/api/v1/product/name/Laptop"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveProduct() throws Exception {
        when(productService.saveProduct(any(ProductRequest.class))).thenReturn(productResponse);
        String productRequestJsonString =
                objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestJsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }


    @Test
    void testUpdateProduct_Found() throws Exception {
        ProductRequest updatedProductRequest = ProductRequest.builder()
                .name("Head Phone")
                .description("This is updated product.")
                .price(100)
                .build();
        ProductResponse updatedProductResponse = ProductResponse.builder()
                .id("L-101")
                .name("Head Phone")
                .description("This is updated product.")
                .price(100)
                .build();
        String productRequestJsonString = objectMapper.writeValueAsString(updatedProductRequest);

        // Mock the service behavior
        when(productService.updateProduct("L-101", updatedProductRequest)).thenReturn(updatedProductResponse);

        // Perform the request with corrected Content-Type and body
        mockMvc.perform(put("/api/v1/product/update/L-101")
                        .contentType(MediaType.APPLICATION_JSON) // Correct Content-Type
                        .content(productRequestJsonString)) // Pass JSON as the request body
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.description").value("This is updated product."));
    }

//    @Test
//    void testDeleteProductById() throws Exception {
//        doNothing().when(productService.deleteProductById("1"));
//
//        mockMvc.perform(delete("/api/v1/product/1"))
//                .andExpect(status().isNoContent());
//    }
    @Test
    void testDeleteProductById() throws Exception {
        when(productService.deleteProductById("1")).thenReturn(productResponse);

        mockMvc.perform(delete("/api/v1/product/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteAllProducts() throws Exception {
        when(productService.deleteAllProducts()).thenReturn(productResponses);

        mockMvc.perform(delete("/api/v1/product/all"))
                .andExpect(status().isNoContent());
    }
}