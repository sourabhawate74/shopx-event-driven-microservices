package com.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//DTO is the sinple object with data no logic
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
