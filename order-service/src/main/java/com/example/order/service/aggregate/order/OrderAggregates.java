package com.example.order.service.aggregate.order;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.api.domain.model.order.OrderId;

import java.util.HashMap;
import java.util.Map;

public class OrderAggregates extends AbstractBehavior<OrderAggregateProtocol.CommandRequest> {
    private final Map<OrderId, ActorRef<OrderAggregateProtocol.CommandRequest>> children = new HashMap<>();

    public OrderAggregates(ActorContext<OrderAggregateProtocol.CommandRequest> context) {
        super(context);
    }

    public static Behavior<OrderAggregateProtocol.CommandRequest> create() {
        return Behaviors.setup(context -> {
            return new OrderAggregates(context);
        });
    }

    @Override
    public Receive<OrderAggregateProtocol.CommandRequest> createReceive() {
        return newReceiveBuilder()
                .onMessage(OrderAggregateProtocol.OrderTerminated.class, msg -> {
                    children.remove(msg.orderId());

                    return this;
                })
                .onAnyMessage(msg -> {
                    var aggregate = getOrSpawn(msg.orderId());
                    aggregate.tell(msg);

                    return this;
                })
                .build();
    }

    private ActorRef<OrderAggregateProtocol.CommandRequest> getOrSpawn(OrderId orderId) {
        return children.computeIfAbsent(orderId, id -> {
            var aggregate = getContext().spawn(OrderAggregate.create(orderId), "orderAggregate-" + orderId.asString());
            getContext().watchWith(aggregate, new OrderAggregateProtocol.OrderTerminated(id));

            return aggregate;
        });
    }
}