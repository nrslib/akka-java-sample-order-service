package com.example.order.service.config.dependency.providers;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SchedulerProvider implements Provider<Scheduler> {
    private final ActorSystem<Void> system;

    @Inject
    public SchedulerProvider(ActorSystem<Void> system) {
        this.system = system;
    }


    @Override
    public Scheduler get() {
        return system.scheduler();
    }
}
