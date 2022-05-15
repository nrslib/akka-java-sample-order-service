package com.example.order.service;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import com.example.order.service.config.dependency.DependencyConfig;
import com.example.order.service.http.controllers.Routes;
import com.google.inject.Key;

public class Guardian {
    public static Behavior<Void> create() {
        return Behaviors.setup(context -> {
                    DependencyConfig.module.bind(context);
                    launchApp(context);

                    return Behaviors.empty();
                }
        );
    }

    private static void launchApp(ActorContext<?> context) {
//        var routes = new Routes();
        var routes = DependencyConfig.injector.getInstance(Key.get(Routes.class));

        var app = new OrderServiceApp(context.getSystem(), context.getSystem().settings().config(), routes);
        app.start();
    }
}