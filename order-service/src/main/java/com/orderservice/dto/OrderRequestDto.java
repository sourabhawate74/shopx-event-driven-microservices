package com.orderservice.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderRequestDto {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
