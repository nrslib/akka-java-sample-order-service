package com.example.order.api.domain.model.order;

public record ItemId(String value) {
    public String asString() {
        return value;
    }
}
