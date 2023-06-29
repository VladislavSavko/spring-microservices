package com.vlados.codit.orderservice.service;

import com.vlados.codit.orderservice.dto.InventoryResponse;
import com.vlados.codit.orderservice.dto.OrderLineItemsDto;
import com.vlados.codit.orderservice.dto.OrderRequest;
import com.vlados.codit.orderservice.event.OrderPlacedEvent;
import com.vlados.codit.orderservice.model.Order;
import com.vlados.codit.orderservice.model.OrderLineItems;
import com.vlados.codit.orderservice.repos.OrderRepos;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepos orderRepository;
    private final WebClient.Builder webClient;

    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final String INVENTORY_SERVICE_URL = "http://inventory-service/api/inventory";
    private final String NOTIFICATION_TOPIC = "notificationTopic";


    @Autowired
    public OrderService(OrderRepos orderRepository, WebClient.Builder webClientConfiguration, Tracer tracer, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.webClient = webClientConfiguration;
        this.tracer = tracer;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void placeOrder(OrderRequest or) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderLineItemsList(or.getOrderLineItemsDtoList().
                stream().
                map(this::mapToDto).
                collect(Collectors.toList()));

        Span span = tracer.nextSpan().name("inventory-service lookup");
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(span.start())) {
            if (Arrays.stream(Objects.requireNonNull(webClient.build()
                            .get()
                            .uri(INVENTORY_SERVICE_URL,
                                    uriBuilder -> uriBuilder.queryParam("skuCode", order.getOrderLineItemsList().stream().
                                            map(OrderLineItems::getSkuCode).
                                            collect(Collectors.toList())).build())
                            .retrieve()
                            .bodyToMono(InventoryResponse[].class)
                            .block()))
                    .allMatch(InventoryResponse::isInStock)) {
                kafkaTemplate.send(NOTIFICATION_TOPIC, new OrderPlacedEvent(order.getOrderNumber()));
                orderRepository.save(order);
                log.info(String.format("Order %s was successfully placed!", order));
            } else {
                throw new IllegalArgumentException(String.format("Order with number %s is not in stock!", order.getOrderNumber()));
            }

        } finally {
            span.end();
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
