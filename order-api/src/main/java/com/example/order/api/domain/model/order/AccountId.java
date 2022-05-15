package com.example.order.api.domain.model.order;

public record AccountId(String value) {
    public String asString() {
        return value;
    }
}
