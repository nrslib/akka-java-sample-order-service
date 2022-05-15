package com.example.order.service.config.dependency.providers;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ClusterShardingProvider implements Provider<ClusterSharding> {
    private final ActorSystem<Void> system;

    @Inject
    public ClusterShardingProvider(ActorSystem<Void> system) {
        this.system = system;
    }

    @Override
    public ClusterSharding get() {
        return ClusterSharding.get(system);
    }
}
