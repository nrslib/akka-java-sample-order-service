package com.example.order.service.config.dependency;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.ActorContext;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import com.example.order.api.adaptor.aggregate.order.OrderAggregateProtocol;
import com.example.order.service.Guardian;
import com.example.order.service.aggregate.order.OrderAggregates;
import com.example.order.service.application.interactors.order.cancel.OrderCancelInteractor;
import com.example.order.service.application.interactors.order.cancelduetodiscontinuation.OrderCancelDueToDiscontinuationInteractor;
import com.example.order.service.application.interactors.order.create.OrderCreateInteractor;
import com.example.order.service.application.interactors.order.makeshipped.OrderMakeShippedInteractor;
import com.example.order.service.config.dependency.providers.ClusterShardingProvider;
import com.example.order.service.config.dependency.providers.ObjectMapperProvider;
import com.example.order.service.config.dependency.providers.SchedulerProvider;
import com.example.order.service.http.controllers.OrderController;
import com.example.order.service.http.controllers.Routes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.UUID;

public class BasicModule extends AbstractModule {
    private final TypeLiteral<ActorSystem<Void>> actorSystemType = new TypeLiteral<>() {
    };
    private final TypeLiteral<ActorContext<Void>> actorContextType = new TypeLiteral<>() {
    };
    private final ActorContextProvider actorContextProvider = new ActorContextProvider();

    public void bind(ActorContext<Void> context) {
        actorContextProvider.setActorContext(context);
    }

    @Override
    protected void configure() {
        bindAkka();
    }

    private void bindAkka() {
        bind(actorSystemType)
                .toProvider(() -> ActorSystem.create(Guardian.create(), "order-service"))
                .in(Singleton.class);

        bind(Config.class)
                .toProvider(ConfigFactory::load)
                .in(Singleton.class);

        bind(Scheduler.class)
                .toProvider(SchedulerProvider.class)
                .in(Singleton.class);

        bind(ClusterSharding.class)
                .toProvider(ClusterShardingProvider.class)
                .in(Singleton.class);

        bind(actorContextType)
                .toProvider(actorContextProvider);

        bind(Duration.class)
                .annotatedWith(Names.named("askTimeout"))
                .toProvider(() -> Duration.ofSeconds(3))
                .in(Singleton.class);

        bind(ObjectMapper.class)
                .toProvider(ObjectMapperProvider.class)
                .in(Singleton.class);
    }

    /* UseCases */
    @Provides
    private OrderCreateInteractor orderCreateInteractor(
            Scheduler scheduler,
            ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef,
            @Named("askTimeout") Duration askTimeout
    ) {
        return new OrderCreateInteractor(scheduler, orderAggregateRef, askTimeout);
    }

    @Provides
    private OrderMakeShippedInteractor orderMakeShippedInteractor(
            Scheduler scheduler,
            ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef,
            @Named("askTimeout") Duration askTimeout
    ) {
        return new OrderMakeShippedInteractor(scheduler, orderAggregateRef, askTimeout);
    }

    @Provides
    private OrderCancelInteractor orderCancelInteractor(
            Scheduler scheduler,
            ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef,
            @Named("askTimeout") Duration askTimeout
    ) {
        return new OrderCancelInteractor(scheduler, orderAggregateRef, askTimeout);
    }

    @Provides
    private OrderCancelDueToDiscontinuationInteractor orderCancelDueToDiscontinuationInteractor(
            Scheduler scheduler,
            ActorRef<OrderAggregateProtocol.CommandRequest> orderAggregateRef,
            @Named("askTimeout") Duration askTimeout
    ) {
        return new OrderCancelDueToDiscontinuationInteractor(scheduler, orderAggregateRef, askTimeout);
    }

    /* Controllers */
    @Provides
    @Singleton
    private ActorRef<OrderAggregateProtocol.CommandRequest> shardedOrderAggregates(ActorContext<Void> context) {
        return context.spawn(OrderAggregates.create(), "shardedOrderAggregates-" + UUID.randomUUID());
    }

    @Provides
    @Singleton
    private OrderController orderController(
            OrderCreateInteractor orderCreateInteractor,
            OrderMakeShippedInteractor orderMakeShippedInteractor,
            OrderCancelInteractor orderCancelInteractor,
            OrderCancelDueToDiscontinuationInteractor orderCancelDueToDiscontinuationInteractor) {
        return new OrderController(
                orderCreateInteractor,
                orderMakeShippedInteractor,
                orderCancelInteractor,
                orderCancelDueToDiscontinuationInteractor);
    }

    @Provides
    @Singleton
    private Routes routes(OrderController orderController) {
        return new Routes(orderController);
    }
}
