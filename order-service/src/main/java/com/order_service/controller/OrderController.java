package com.order_service.controller;

import com.order_service.dto.OrderRequestDto;
import com.order_service.model.Order;
import com.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // Create Order
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequestDto orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order placed successfully";
    }

//    // Get All Orders
//    @GetMapping("/all")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Order> getAllOrders() {
//        return orderService.getAllOrders();
//    }
//
//    // Get Order By Id
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public Order getOrderById(@PathVariable Long id) {
//        return orderService.getOrderById(id);
//    }
//
//    // Delete Order
//    @DeleteMapping("/delete/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public String deleteOrder(@PathVariable Long id) {
//        orderService.deleteOrder(id);
//        return "Order deleted successfully";
//    }
}
