package com.productservice;

import com.productservice.dto.ProductRequest;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class shopXApplicationTests {
//    this will load the  moongodb image to start the integration testing We are not using local mongoDB here, using docker mongodb image
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    To load the url from application.roperties file
    @DynamicPropertySource
        static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductService productService;

    @BeforeEach
    void cleanDbBefore() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest1 = getProductRequest1();
        String productJson1 = objectMapper.writeValueAsString(productRequest1);

        ProductRequest productRequest2 = getProductRequest2();
        String productJson2 = objectMapper.writeValueAsString(productRequest2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        //to check the all products are available
        Assertions.assertEquals(2, productRepository.findAll().size());
    }

    public ProductRequest getProductRequest1(){
        return ProductRequest.builder()
                .name("Apple")
                        .description("Apple phone")
                                .price(BigDecimal.valueOf(6000)).build();
    }
    public ProductRequest getProductRequest2(){
        return ProductRequest.builder()
                .name("Samsung")
                .description("Samsung phone")
                .price(BigDecimal.valueOf(2000)).build();
    }

    @Test
    public void shouldGetProducts() throws Exception {
        ProductRequest productRequest1 = getProductRequest1();
        String productJson1 = objectMapper.writeValueAsString(productRequest1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void shouldGetProductById() throws Exception {

        ProductRequest productRequest = getProductRequest1();
        String productJson = objectMapper.writeValueAsString(productRequest);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = productRepository.findAll().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {

        ProductRequest productRequest = getProductRequest1();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());

        String id = productRepository.findAll().get(0).getId();

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Apple")
                .description("Updated description")
                .price(BigDecimal.valueOf(9999))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/product/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Apple"))
                .andExpect(jsonPath("$.price").value(9999));
    }

    @Test
    void shouldDeleteProduct() throws Exception {

        ProductRequest productRequest = getProductRequest1();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());

        String id = productRepository.findAll().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/" + id))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, productRepository.findAll().size());
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/invalid-id-123"))
                .andExpect(status().isNotFound());
    }
}
