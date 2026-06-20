package com.orderservice.service;

import com.orderservice.dto.InventoryResponse;
import com.orderservice.dto.OrderLineItemsDto;
import com.orderservice.dto.OrderRequestDto;
import com.orderservice.model.Order;
import com.orderservice.model.OrderLineItems;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequestDto orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        //Order contains list of Items,getting all the skucodes and checking for the product present in stock or not.
        List<String> skuCodes= order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //calling the inventory the check the item in stock or not.
        InventoryResponse[] inventoryRespArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder
                                .queryParam("skuCode", skuCodes)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryRespArray)
                .allMatch(r -> Boolean.TRUE.equals(r.getIsInStock()));

        if (Boolean.TRUE.equals(allProductsInStock)) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock");
        }
    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;

    }
}
