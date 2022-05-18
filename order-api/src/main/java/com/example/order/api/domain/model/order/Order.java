package com.example.order.api.domain.model.order;

import com.example.supportstack.domain.AggregateRoot;
import fj.data.Either;

public record Order(OrderId orderId, AccountId accountId, State state) implements AggregateRoot {
    public static Either<OrderError, OrderEvents.OrderCreated> create(OrderId orderId, AccountId accountId, OrderDetail detail) {
        return Either.right(new OrderEvents.OrderCreated(orderId, accountId, detail));
    }

    public static Order applyEvent(OrderEvents.OrderCreated event) {
        return new Order(event.orderId(), event.accountId(), State.CREATED);
    }

    public Either<OrderError, OrderEvents.OrderShipped> makeShipped() {
        if (state != State.CREATED) {
            return Either.left(new OrderError.IllegalStateChangeError(orderId, state));
        }

        return Either.right(new OrderEvents.OrderShipped(orderId));
    }

    public Either<OrderError, OrderEvents.OrderCanceled> cancel(AccountId accountId) {
        if (state != State.CREATED) {
            return Either.left(new OrderError.IllegalStateChangeError(orderId, state));
        }

        if (!accountId.equals(this.accountId)) {
            return Either.left(new OrderError.InvalidOwnerError(this, accountId));
        }

        return Either.right(new OrderEvents.OrderCanceled(orderId));
    }

    public Either<OrderError, OrderEvents.OrderCanceledDueToDiscontinuation> cancelDueToDiscontinuation() {
        if (state != State.CREATED) {
            return Either.left(new OrderError.IllegalStateChangeError(orderId, state));
        }

        return Either.right(new OrderEvents.OrderCanceledDueToDiscontinuation(orderId));
    }

    public Order applyEvent(OrderEvents.Event event) {
        if (event instanceof OrderEvents.OrderCreated) {
            return new Order(orderId, accountId, State.CREATED);
        } else if (event instanceof OrderEvents.OrderShipped) {
            return new Order(orderId, accountId, State.SHIPPED);
        } else if (event instanceof OrderEvents.OrderCanceled) {
            return new Order(orderId, accountId, State.CANCELED);
        } else if (event instanceof OrderEvents.OrderCanceledDueToDiscontinuation) {
            return new Order(orderId, accountId, State.CANCELED_DUE_TO_DISCONTINUATION);
        }

        throw new IllegalStateException("Unexpected event: " + event.getClass().getName());
    }

    public enum State {
        CREATED,
        SHIPPED,
        CANCELED,
        CANCELED_DUE_TO_DISCONTINUATION
    }
}