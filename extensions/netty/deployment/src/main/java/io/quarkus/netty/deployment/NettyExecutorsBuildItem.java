package io.quarkus.netty.deployment;

import java.util.function.Supplier;

import io.quarkus.builder.item.SimpleBuildItem;

public final class NettyExecutorsBuildItem extends SimpleBuildItem {
    private Supplier<Object> executor;
    private Supplier<Object> io;

    public NettyExecutorsBuildItem(Supplier<Object> executor, Supplier<Object> io) {
        this.executor = executor;
        this.io = io;
    }

    public Supplier<Object> getExecutor() {
        return executor;
    }

    public Supplier<Object> getIo() {
        return io;
    }
}
