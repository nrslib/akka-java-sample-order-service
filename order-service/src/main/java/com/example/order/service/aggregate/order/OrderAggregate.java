package com.example.order.service.aggregate.order;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.Order;
import com.example.order.api.domain.model.order.OrderEvents;
import com.example.order.api.domain.model.order.OrderId;

public class OrderAggregate extends EventSourcedBehavior<OrderAggregateProtocol.CommandRequest, OrderEvents.Event, Order> {
    public OrderAggregate(OrderId orderId) {
        super(PersistenceId.ofUniqueId(orderId.asString()));
    }

    public static Behavior<OrderAggregateProtocol.CommandRequest> create(OrderId orderId) {
        return Behaviors.setup(context -> new OrderAggregate(orderId));
    }

    @Override
    public boolean shouldSnapshot(Order order, OrderEvents.Event event, long sequenceNr) {
        if (event instanceof OrderEvents.OrderCreated) {
            return true;
        }

        return false;
    }
    @Override
    public Order emptyState() {
        return null;
    }

    @Override
    public CommandHandler<OrderAggregateProtocol.CommandRequest, OrderEvents.Event, Order> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(OrderAggregateProtocol.CreateOrderRequest.class, (__, command) -> {
                    var result = Order.create(command.orderId(), command.accountId(), command.detail());
                    if (result.isRight()) {
                        return Effect().persist(result.right().value())
                                .thenReply(command.replyTo(), order ->
                                        new OrderAggregateProtocol.CreateOrderSucceeded(
                                                order.orderId())
                                );
                    } else {
                        return Effect().none()
                                .thenReply(command.replyTo(), ___ ->
                                        new OrderAggregateProtocol.CreateOrderFailed(
                                                command.orderId(),
                                                result.left().value()));
                    }
                })
                .onCommand(OrderAggregateProtocol.MakeOrderShipped.class, (order, command) -> {
                    var result = order.makeShipped();

                    if (result.isRight()) {
                        return Effect().persist(result.right().value())
                                .thenReply(command.replyTo(), it ->
                                        new OrderAggregateProtocol.MakeOrderShippedSucceeded(
                                                it.orderId()));
                    } else {
                        return Effect().none()
                                .thenReply(command.replyTo(), ___ ->
                                        new OrderAggregateProtocol.MakeOrderShippedFailed(
                                                command.orderId(),
                                                result.left().value()));
                    }
                })
                .onCommand(OrderAggregateProtocol.CancelOrder.class, (order, command) -> {
                    var result = order.cancel(command.accountId());

                    if (result.isRight()) {
                        return Effect().persist(result.right().value())
                                .thenReply(command.replyTo(), it ->
                                        new OrderAggregateProtocol.CancelOrderSucceeded(
                                                it.orderId()));

                    } else {
                        return Effect().none()
                                .thenReply(command.replyTo(), ___ ->
                                        new OrderAggregateProtocol.CancelOrderFailed(
                                                command.orderId(),
                                                result.left().value()));
                    }
                })
                .onCommand(OrderAggregateProtocol.CancelOrderDueToDiscontinuation.class, (order, command) -> {
                    var result = order.cancelDueToDiscontinuation();

                    if (result.isRight()) {
                        return Effect().persist(result.right().value())
                                .thenReply(command.replyTo(), it ->
                                        new OrderAggregateProtocol.CancelOrderDueToDiscontinuationSucceeded(
                                                it.orderId()));

                    } else {
                        return Effect().none()
                                .thenReply(command.replyTo(), ___ ->
                                        new OrderAggregateProtocol.CancelOrderDueToDiscontinuationFailed(
                                                order.orderId(),
                                                result.left().value()));
                    }
                })
                .build();
    }

    @Override
    public EventHandler<Order, OrderEvents.Event> eventHandler() {
        return newEventHandlerBuilder()
                .forAnyState()
                .onEvent(OrderEvents.OrderCreated.class, (__, event) -> {
                    var order = Order.newEmpty(event.orderId(), event.accountId());

                    return order.applyEvent(event);
                })
                .onAnyEvent(Order::applyEvent);
    }
}