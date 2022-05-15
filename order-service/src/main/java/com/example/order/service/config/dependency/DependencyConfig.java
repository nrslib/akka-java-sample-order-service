package com.example.order.service.config.dependency;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DependencyConfig {
    public static BasicModule module = new BasicModule();
    public static Injector injector;

    public static void run() {
        injector = createInjector();
    }

    private static Injector createInjector() {
        return Guice.createInjector(module);
    }
}
