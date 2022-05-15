package com.example.order.api.domain.model.order;

import com.example.supportstack.akka.serialization.CborSerializable;

public interface OrderEvents {
    sealed interface Event extends CborSerializable {
        OrderId orderId();
    }

    record OrderCreated(OrderId orderId, AccountId accountId, OrderDetail detail) implements Event {
    }

    record OrderCanceled(OrderId orderId) implements Event {
    }

    record OrderCanceledDueToDiscontinuation(OrderId orderId) implements Event {
    }

    record OrderShipped(OrderId orderId) implements Event {
    }
}
