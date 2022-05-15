package com.example.order.api.domain.model.order;

public record ItemNr(int value) {
    public ItemNr {
        if (value <= 0) {
            throw new IllegalArgumentException("value must not be less than zero.");
        }
    }
}
