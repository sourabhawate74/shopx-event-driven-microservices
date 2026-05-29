package com.productservice.dto;

import jakarta.annotation.Generated;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
}
