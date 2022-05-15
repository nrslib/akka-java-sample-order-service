package com.example.order.service.application.interactors.order.cancelduetodiscontinuation;

import com.example.order.service.application.interactors.order.OrderException;

public class OrderCancelDueToDiscontinuationException extends OrderException {
    public OrderCancelDueToDiscontinuationException(String message) {
        super(message);
    }
}
