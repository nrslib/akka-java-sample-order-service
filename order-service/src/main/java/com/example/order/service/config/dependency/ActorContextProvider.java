package com.example.order.service.config.dependency;

import akka.actor.typed.javadsl.ActorContext;
import com.google.inject.Provider;

public class ActorContextProvider implements Provider<ActorContext<Void>> {
    private ActorContext<Void> actorContext;

    public void setActorContext(ActorContext<Void> actorContext) {
        this.actorContext = actorContext;
    }

    @Override
    public ActorContext<Void> get() {
        return actorContext;
    }
}
