package com.vlados.codit.inventoryservice.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vlados.codit.inventoryservice.dto.InventoryResponse;
import com.vlados.codit.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam(name = "skuCode") List<String> codes) {
        return inventoryService.isInStock(codes);
    }

}
