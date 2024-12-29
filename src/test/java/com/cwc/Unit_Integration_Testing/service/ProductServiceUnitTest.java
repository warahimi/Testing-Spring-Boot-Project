package com.cwc.Unit_Integration_Testing.service;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.entity.Product;
import com.cwc.Unit_Integration_Testing.exception.ProductNotFoundException;
import com.cwc.Unit_Integration_Testing.repository.ProductRepository;
import com.cwc.Unit_Integration_Testing.util.AppUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceUnitTest {
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllProducts() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(
                Arrays.asList(product1, product2));

        List<ProductResponse> products = productService.findAllProducts();
        Assertions.assertNotNull(products);
        Assertions.assertNotEquals(0, products.size());
        Assertions.assertEquals(2, products.size(),"List size is not 2");

        verify(productRepository, times(1)).findAll();
    }
    @Test
    void findAllProductsSholdReturnExactList() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(
                Collections.singletonList(product1));

        List<ProductResponse> products = productService.findAllProducts();
        Assertions.assertNotNull(products);
        Assertions.assertNotEquals(0, products.size());
        Assertions.assertEquals(1, products.size(),"List size is not 1");

        ProductResponse productResponse = products.get(0);
        Assertions.assertEquals(productResponse.getId(), product1.getId());
        Assertions.assertEquals(productResponse.getName(), product1.getName());
        Assertions.assertEquals(productResponse.getDescription(), product1.getDescription());
        Assertions.assertEquals(productResponse.getPrice(), product1.getPrice());

        assertAll(
                () -> productResponse.getName().startsWith("L"),
                () -> productResponse.getId().equals("L-101"),
                () -> assertEquals(productResponse.getPrice(),255.99,"Prices are not equal")
        );

        verify(productRepository, times(1)).findAll();

    }
    @Test
    void findAllProductsShouldThrowProductNotFoundException() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThrows(ProductNotFoundException.class,
                () -> productService.findAllProducts(),"There is no product in the database");
        verify(productRepository, times(1)).findAll();

    }
    @Test
    void testFindAllProducts_ThrowsProductNotFoundException() {
        // Arrange: Mock the repository to return an empty list
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert: Expect ProductNotFoundException to be thrown
        assertThrows(ProductNotFoundException.class,
                () -> productService.findAllProducts(),
                "Expected findAllProducts() to throw ProductNotFoundException when no products are found");
        verify(productRepository, times(1)).findAll();

    }
    @Test
    void findProductById() throws ProductNotFoundException {
        when(productRepository.findById("M-1001")).thenReturn(Optional.of(product2));

        ProductResponse productResponse = productService.findProductById("M-1001");

        assertNotNull(productResponse);
        assertEquals(productResponse.getId(), "M-1001");
        assertEquals(productResponse.getName(), product2.getName());
        verify(productRepository, times(1)).findById("M-1001");
    }
    @Test
    void findProductById_showThrowProductNotFoundException() throws ProductNotFoundException {
        when(productRepository.findById("M-1003")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,()-> productService.findProductById("M-1003"));
    }

    @Test
    void findProductByName() throws ProductNotFoundException {
        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product1));

        ProductResponse productResponse = productService.findProductByName("Laptop");
        assertNotNull(productResponse);
        assertEquals(productResponse.getName(), product1.getName());


        assertThrows(ProductNotFoundException.class, ()-> productService.findProductByName("Lap"));
        verify(productRepository, times(1)).findByName("Laptop");
        verify(productRepository, times(1)).findByName("Lap");

    }

    @Test
    void findProductByName_ShouldThrowProductNotFoundException() throws ProductNotFoundException {

        when(productRepository.findByName("Lap")).thenReturn(Optional.empty());


        assertThrows(ProductNotFoundException.class, ()-> productService.findProductByName("Lap"));

    }

    @Test
    void saveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        ProductResponse productResponse = productService.saveProduct(productRequest);

        assertNotNull(productResponse);
        assertEquals(productResponse.getId(), product1.getId());
        assertEquals(productResponse.getName(), productRequest.getName());
        verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    void updateProduct() throws ProductNotFoundException {
        Product updatedProduct = Product.builder()
                .id("L-101")
                .name("Keyboard")
                .description("This is a keyboard.")
                .price(300.99)
                .build();
        ProductRequest productRqst = ProductRequest.builder()
                .name("Keyboard")
                .description("This is a keyboard.")
                .price(300.99)
                .build();
        when(productRepository.existsById("L-101")).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        ProductResponse productResponse = productService.updateProduct("L-101", productRqst);

        assertNotNull(productResponse);
        assertAll(
                () -> assertEquals(productResponse.getId(), updatedProduct.getId(), "IDs do not match"),
                () -> assertEquals(productResponse.getName(), updatedProduct.getName()),
                () -> assertEquals(productResponse.getPrice(), updatedProduct.getPrice()),
                () -> assertTrue(productResponse.getName().startsWith("K")),
                () -> assertTrue(productResponse.getId().startsWith("L"))
        );
    }

    @Test
    void updateProductShouldThroughProductNodFoundException() throws ProductNotFoundException {

        when(productRepository.existsById("L-101")).thenReturn(false);
        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct("L-101", productRequest));
    }
    @Test
    void deleteProductByIdThatReturnsVoid() throws ProductNotFoundException {
        when(productRepository.findById("1")).thenReturn(Optional.of(product1));
        // Arrange mock the delete behavior.
        doNothing().when(productRepository).deleteById("1");

        // Act call the service method
        productService.deleteProductById("1");

        verify(productRepository, times(1)).deleteById("1");
        verify(productRepository, times(1)).findById("1");
    }
    @Test
    void deletByIdShouldThrougProductNotFoundException() throws ProductNotFoundException
    {
        when(productRepository.findById("tt")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProductById("tt"));
    }



    @Test
    void deleteAllProducts() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        // Arrange mock the delete behavior.
        doNothing().when(productRepository).deleteAll();

        // Act call the service method
        productService.deleteAllProducts();

        verify(productRepository, times(1)).findAll();
        verify(productRepository, times(1)).deleteAll();
    }
    @Test
    void deleteAllProductsShouldThrougException() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(List.of());
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteAllProducts());
    }
}