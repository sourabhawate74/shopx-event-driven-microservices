package com.productservice.service;

import com.productservice.dto.ProductRequest;
import com.productservice.dto.ProductResponse;
import com.productservice.model.Product;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        //creating object with all the details
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} is saved",product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        log.info("get All Products");
        List<Product> products;
        try {
            products = productRepository.findAll();
        } catch (Exception ex) {
            log.error("Exception while fetching products from repository", ex);
            return Collections.emptyList();
        }
        if (products.isEmpty()) {
            log.error("No products found in database");
            return Collections.emptyList();
        }
        log.info("Fetched {} products", products.size());
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public ProductResponse getProductById(String id) {
        log.info("Fetching product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });

        return mapToProductResponse(product);
    }

    public void updateProduct(String id, ProductRequest productRequest) {
        log.info("Updating product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for update with id: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());

        productRepository.save(existingProduct);

        log.info("Product updated successfully with id: {}", id);
    }

    public void deleteProduct(String id) {
        log.info("Deleting product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for deletion with id: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });

        productRepository.delete(existingProduct);

        log.info("Product deleted successfully with id: {}", id);
    }
}
