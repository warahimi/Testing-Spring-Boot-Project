package com.cwc.Unit_Integration_Testing.service;

import com.cwc.Unit_Integration_Testing.dto.ProductRequest;
import com.cwc.Unit_Integration_Testing.dto.ProductResponse;
import com.cwc.Unit_Integration_Testing.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Slf4j
class ProductServiceIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");
    private ProductRequest productRequest1 = ProductRequest.builder()
            .name("Laptop")
            .description("This is a dell laptop.")
            .price(1000.99)
            .build();
    private ProductRequest productRequest2 = ProductRequest.builder()
            .name("Mouse")
            .description("This is a dell mouse.")
            .price(10.99)
            .build();

    @Autowired
    private ProductService productService;

    @BeforeAll
    public static void beforeAllSetUp() {
        mongoDBContainer.start();
    }
    @AfterAll
    static void afterAllSetUp()
    {
        mongoDBContainer.stop();
    }

    /*
    Ensure Test Isolation
    As an additional safeguard, ensure that each test starts with a clean database state.
    This can be achieved using the @BeforeEach method:
     */
    @BeforeEach
    void setUp() throws ProductNotFoundException {
        try {
            if (!productService.findAllProducts().isEmpty()) {
                productService.deleteAllProducts();
            }
        }catch (ProductNotFoundException e)
        {
            log.info("Data is already empty");
        }

    }

    @AfterEach
    void tearDown() throws ProductNotFoundException {
        try {
            if (!productService.findAllProducts().isEmpty()) {
                productService.deleteAllProducts();
            }
        }catch (ProductNotFoundException e)
        {
            log.info("Databas is already empty");
        }

    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void testFindAllProducts() throws ProductNotFoundException {
        productService.saveProduct(productRequest1);
        productService.saveProduct(productRequest2);

        List<ProductResponse> productResponses = productService.findAllProducts();

        assertThat(productResponses).isNotNull();
        assertThat(productResponses.size()).isEqualTo(2);
        assertThat(productResponses.stream().anyMatch(p -> p.getName().equals("Laptop"))).isTrue();
        assertThat(productResponses.stream().anyMatch(p -> p.getName().equals("Mouse"))).isTrue();
        assertThat(productResponses
                .stream()
                .filter(p->p.getName().equals("Laptop"))
                .collect(Collectors.toList())
                .get(0)
                .getDescription().equals(productRequest1.getDescription())
        );
    }

    @Test
    void testFindProductById() throws ProductNotFoundException {
        ProductResponse productResponse = productService.saveProduct(productRequest1);
        ProductResponse productById = productService.findProductById(productResponse.getId());
        assertThat(productById).isNotNull();
        assertThat(productById.getId()).isEqualTo(productResponse.getId());
        assertThat(productById.getName()).isEqualTo(productResponse.getName());
        assertThat(productById.getDescription()).isEqualTo(productResponse.getDescription());
        assertThat(productById.getPrice()).isEqualTo(productResponse.getPrice());
    }
    @Test
    void testFindProductById_ShoulThrowProductNotFoundException() throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class,
                () -> productService.findProductById("ttt"));
    }

    @Test
    void testFindProductByName() throws ProductNotFoundException {
        ProductResponse productResponse = productService.saveProduct(productRequest1);
        ProductResponse productByName = productService.findProductByName(productResponse.getName());
        assertThat(productByName.getName()).isEqualTo(productResponse.getName());
        assertThat(productByName.getId()).isEqualTo(productResponse.getId());
        assertThat(productByName.getPrice()).isEqualTo(productResponse.getPrice());

        assertThrows(ProductNotFoundException.class,
                () -> productService.findProductByName("ttt"));
    }

    @Test
    void testSaveProduct() {
        ProductResponse productResponse = productService.saveProduct(productRequest1);
        assertNotNull(productResponse);
        assertEquals("Laptop", productResponse.getName());
        assertEquals(1000.99, productResponse.getPrice());
        assertEquals(productRequest1.getDescription(), productResponse.getDescription());
        assertAll(
                () -> assertEquals(1,productService.findAllProducts().size()),
                () -> assertNotNull(productService.findAllProducts())
        );
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getName())
                .isNotNull()
                .isEqualTo("Laptop")
                .startsWith("L")
                .endsWith("p")
                .hasSize(6);
        assertThat(productResponse.getPrice())
                .isEqualTo(1000.99)
                .isEqualTo(productRequest1.getPrice());
        assertThat(productResponse.getDescription()).isEqualTo(productRequest1.getDescription());

    }

    @Test
    void testUpdateProduct() throws ProductNotFoundException {
        ProductResponse productResponse = productService.saveProduct(productRequest1);
        String id = productResponse.getId();
        ProductRequest update = ProductRequest.builder()
                .name("Pc")
                .description(productRequest1.getDescription())
                .price(400)
                .build();

        ProductResponse updatedProductResponse = productService.updateProduct(id, update);

        assertThat(updatedProductResponse).isNotNull();
        assertThat(updatedProductResponse.getName()).isEqualTo("Pc");
        assertThat(updatedProductResponse.getPrice()).isEqualTo(400);

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct("ttt",update));
    }

    @Test

    void deleteProductById() throws ProductNotFoundException {
        ProductResponse productResponse = productService.saveProduct(productRequest1);
        String id = productResponse.getId();

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getId()).isNotNull();

        assertThat(productService.findAllProducts().size()).isEqualTo(1);

        ProductResponse deletedProductResponse = productService.deleteProductById(id);
        assertThat(deletedProductResponse).isNotNull();
        assertThat(deletedProductResponse.getId()).isEqualTo(productResponse.getId());
        assertThat(deletedProductResponse.getName()).isEqualTo(productResponse.getName());
        assertThat(deletedProductResponse.getDescription()).isEqualTo(productResponse.getDescription());

        assertThrows(ProductNotFoundException.class,
                () -> productService.findAllProducts());
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProductById("ttt"));
    }

    @Test
    void deleteAllProducts() throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteAllProducts());

        productService.saveProduct(productRequest1);
        productService.saveProduct(productRequest2);

        assertThat(productService.findAllProducts().size()).isEqualTo(2);
        productService.deleteAllProducts();

        assertThrows(ProductNotFoundException.class,
                () -> productService.findAllProducts());
    }
}