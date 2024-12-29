package com.cwc.Unit_Integration_Testing.controller;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.exception.ProductNotFoundException;
import com.cwc.Unit_Integration_Testing.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAllProducts() throws ProductNotFoundException {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@ PathVariable String id) throws ProductNotFoundException {
        ProductResponse product = productService.findProductById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(product);
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<ProductResponse> findProductByName(@PathVariable String name) throws ProductNotFoundException {
        ProductResponse productByName = productService.findProductByName(name);
        return ResponseEntity.status(HttpStatus.FOUND).body(productByName);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productRequest)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(productRequest));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id,
                                                         @RequestBody ProductRequest productRequest)
            throws ProductNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.updateProduct(id,productRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProductById(@PathVariable String id) throws ProductNotFoundException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(productService.deleteProductById(id));
    }
    @DeleteMapping("/all")
    public ResponseEntity<List<ProductResponse>> deleteAllProducts() throws ProductNotFoundException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(productService.deleteAllProducts());
    }
}