package com.example.order.service;

import akka.actor.typed.ActorSystem;
import com.example.order.service.config.dependency.DependencyConfig;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

class Main {
    public static void main(String[] args) {
        DependencyConfig.run();
        var injector = DependencyConfig.injector;
        var actorSystem = injector.getInstance(Key.get(new TypeLiteral<ActorSystem<Void>>() {
        }));
    }
}
