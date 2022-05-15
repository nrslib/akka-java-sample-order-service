package com.example.order.service;

import akka.actor.typed.ActorSystem;
import akka.http.javadsl.Http;
import com.example.order.service.http.controllers.Routes;
import com.typesafe.config.Config;

class OrderServiceApp {
    private final ActorSystem<?> actorSystem;
    private final Config config;
    private final Routes routes;

    OrderServiceApp(ActorSystem<?> actorSystem, Config config, Routes routes) {
        this.actorSystem = actorSystem;
        this.config = config;
        this.routes = routes;
    }

    void start() {
        var host = config.getString("http.host");
        var port = config.getInt("http.port");

        startServer(host, port);
    }

    private void startServer(String host, int port) {
        Http.get(actorSystem)
                .newServerAt(host, port)
                .bind(routes.getRoutes());
    }
}
