package com.example.order.api.adaptor.aggregate.order;


import akka.actor.typed.ActorRef;
import com.example.order.api.domain.model.order.AccountId;
import com.example.order.api.domain.model.order.OrderDetail;
import com.example.order.api.domain.model.order.OrderError;
import com.example.order.api.domain.model.order.OrderId;

public interface OrderAggregateProtocol {
    sealed interface CommandRequest {
        OrderId orderId();
    }

    /* Create Order */
    record CreateOrderRequest(
            OrderId orderId,
            AccountId accountId,
            OrderDetail detail,
            ActorRef<CreateOrderReply> replyTo) implements CommandRequest {
    }

    sealed interface CreateOrderReply {}

    record CreateOrderSucceeded(OrderId orderId) implements CreateOrderReply {}

    record CreateOrderFailed(OrderId orderId, OrderError error) implements CreateOrderReply {}

    /* MakeOrderShipped */
    record MakeOrderShipped(
            OrderId orderId,
            ActorRef<MakeOrderReply> replyTo) implements CommandRequest {}

    sealed interface MakeOrderReply {}

    record MakeOrderShippedSucceeded(OrderId orderId) implements MakeOrderReply {}

    record MakeOrderShippedFailed(OrderId orderId, OrderError error) implements MakeOrderReply {}

    /* Cancel Order */
    record CancelOrder(
            OrderId orderId,
            AccountId accountId,
            ActorRef<CancelOrderReply> replyTo) implements CommandRequest {}

    sealed interface CancelOrderReply {}

    record CancelOrderSucceeded(OrderId orderId) implements CancelOrderReply {}

    record CancelOrderFailed(OrderId orderId, OrderError error) implements CancelOrderReply {}

    /* Cancel Order Due To Discontinuation */
    record CancelOrderDueToDiscontinuation(
            OrderId orderId,
            ActorRef<CancelOrderDueToDiscontinuationReply> replyTo) implements CommandRequest {}

    sealed interface CancelOrderDueToDiscontinuationReply {}

    record CancelOrderDueToDiscontinuationSucceeded(OrderId orderId) implements CancelOrderDueToDiscontinuationReply {}

    record CancelOrderDueToDiscontinuationFailed(
            OrderId orderId,
            OrderError error) implements CancelOrderDueToDiscontinuationReply {}

    /* The classes below are for Aggregates */
    record OrderTerminated(OrderId orderId) implements CommandRequest {}
}