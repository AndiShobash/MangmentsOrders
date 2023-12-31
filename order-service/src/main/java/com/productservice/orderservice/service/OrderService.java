package com.productservice.orderservice.service;

import brave.Span;
import brave.Tracer;
import com.productservice.orderservice.dto.InventoryResponse;
import com.productservice.orderservice.dto.OrderLineItemsDto;
import com.productservice.orderservice.dto.OrderRequest;
import com.productservice.orderservice.event.OrderPlacedEvent;
import com.productservice.orderservice.model.Order;
import com.productservice.orderservice.model.OrderLineItems;
import com.productservice.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);


        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        System.out.println("Calling inventory service");
        //Call Inventory Service, and place order if product is in stock
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notifactionTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("Product is not is stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
