package com.order_service.dto;

import com.order_service.model.OrderLineItems;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderRequestDto {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
