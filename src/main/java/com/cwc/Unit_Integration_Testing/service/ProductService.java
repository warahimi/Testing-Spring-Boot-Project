package com.cwc.Unit_Integration_Testing.service;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.entity.Product;
import com.cwc.Unit_Integration_Testing.exception.ProductNotFoundException;
import com.cwc.Unit_Integration_Testing.repository.ProductRepository;
import com.cwc.Unit_Integration_Testing.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponse> findAllProducts() throws ProductNotFoundException {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty())
        {
            throw new ProductNotFoundException("There is no product in the database");
        }
        return products.stream().map(AppUtil::convertToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findProductById(String id) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty())
        {
            throw new ProductNotFoundException("Product with id " + id +" not found");
        }
        return AppUtil.convertToProductResponse(product.get());
    }
    public ProductResponse findProductByName(String name) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findByName(name);
        if(product.isEmpty())
        {
            throw new ProductNotFoundException("Product with name " + name +" not found");
        }
        return AppUtil.convertToProductResponse(product.get());
    }

    public ProductResponse saveProduct(ProductRequest productRequest)
    {
        Product product = AppUtil.convertToProduct(productRequest);
        Product savedProduct = productRepository.save(product);
        return AppUtil.convertToProductResponse(savedProduct);
    }
    public ProductResponse updateProduct(String id, ProductRequest productRequest)
            throws ProductNotFoundException {
        if(!productRepository.existsById(id))
        {
            throw new ProductNotFoundException("Product with id "+id+" not found");
        }
        Product product = AppUtil.convertToProduct(productRequest);
        product.setId(id);
        Product savedProduct = productRepository.save(product);

        return AppUtil.convertToProductResponse(savedProduct);
    }
    public ProductResponse deleteProductById(String id) throws ProductNotFoundException {

        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty())
        {
            throw new ProductNotFoundException("Product with id "+id+" not found");
        }
        productRepository.deleteById(id);
        return AppUtil.convertToProductResponse(product.get());
    }

    public List<ProductResponse> deleteAllProducts() throws ProductNotFoundException {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty())
        {
            throw new ProductNotFoundException("There is no product in the database");
        }
        productRepository.deleteAll();
        return products.stream().map(AppUtil::convertToProductResponse)
                .collect(Collectors.toList());
    }
//    public List<ProductResponse> deleteAllProducts() {
//        List<Product> products = productRepository.findAll();
//        if (products.isEmpty()) {
//            log.warn("No products found in the database to delete.");
//            return List.of(); // Return an empty list instead of throwing an exception
//        }
//        productRepository.deleteAll();
//        return products.stream()
//                .map(AppUtil::convertToProductResponse)
//                .collect(Collectors.toList());
//    }



}
