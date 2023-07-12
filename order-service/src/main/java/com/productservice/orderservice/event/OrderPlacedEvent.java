package com.productservice.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String orderNumber;

    public OrderPlacedEvent(Object source, String orderNumber) {
        super();
        this.orderNumber = orderNumber;
    }
}
