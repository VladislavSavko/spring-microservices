package com.vlados.codit.inventoryservice.service;

import com.vlados.codit.inventoryservice.dto.InventoryResponse;
import com.vlados.codit.inventoryservice.repos.InventoryRepos;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class InventoryService {
    private final InventoryRepos inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepos inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream().
                map(inventory ->
                        InventoryResponse.builder().
                                skuCode(inventory.getSkuCode()).
                                isInStock(inventory.getQuantity() > 0).
                                build())
                .toList();
    }
}
