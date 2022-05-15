package com.example.order.api.domain.model.order;

import java.util.UUID;

public record OrderId(UUID value) {
    public String asString() {
        return value().toString();
    }

    @Override
    public String toString() {
        return "OrderId{" +
                "value=" + value +
                '}';
    }
}
