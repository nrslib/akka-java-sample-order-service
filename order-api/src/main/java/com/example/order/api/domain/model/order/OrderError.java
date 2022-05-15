package com.example.order.api.domain.model.order;

public sealed interface OrderError {
    String message();

    record AlreadyExistError(OrderId orderId) implements OrderError {
        @Override
        public String message() {
            return "The order exists {orderId:\"%s\"}"
                    .formatted(orderId);
        }
    }

    record IllegalStateChangeError(OrderId orderId, Order.State currentState) implements OrderError {
        @Override
        public String message() {
            return "An illegal state change requested. {orderId:\"%s\",currentState:\"%s\"}"
                    .formatted(orderId.asString(), currentState);
        }
    }

    record InvalidOwnerError(Order order, AccountId requestFrom) implements OrderError {
        @Override
        public String message() {
            return "Not owned. {orderId:%s,accountId:\"%s\",requestAccountId:\"%s\"}"
                    .formatted(order.orderId().asString(), order.accountId().asString(), requestFrom.asString());
        }
    }
}
