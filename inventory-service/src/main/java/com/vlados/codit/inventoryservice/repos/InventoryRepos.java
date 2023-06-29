package com.vlados.codit.inventoryservice.repos;

import com.vlados.codit.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepos extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySkuCodeIn(List<String> codes);
}
