package com.inventoryservice.controller;

import com.inventoryservice.Dto.InventoryResponse;
import com.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    //http://localhost:8082/api/inventory?SkuCode=MacBook1&SkuCode=MacBook2
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(
            @RequestParam("skuCode") List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
