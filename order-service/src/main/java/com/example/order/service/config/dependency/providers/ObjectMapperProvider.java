package com.example.order.service.config.dependency.providers;

import akka.actor.typed.ActorSystem;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ObjectMapperProvider implements Provider<ObjectMapper> {
    private final ActorSystem<Void> actorSystem;

    @Inject
    public ObjectMapperProvider(ActorSystem<Void> actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public ObjectMapper get() {
        return new ObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }
}
