package com.example.order.service.application.interactors.order.cancel;

import com.example.order.service.application.interactors.order.OrderException;

public class OrderCancelException extends OrderException {
    public OrderCancelException(String message) {
        super(message);
    }
}
