package com.vlados.codit.orderservice.repos;

import com.vlados.codit.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepos extends JpaRepository<Order, Long> {
}
