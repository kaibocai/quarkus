package io.quarkus.vertx.web.deployment;

import io.quarkus.builder.item.MultiBuildItem;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public final class DefaultRouteBuildItem extends MultiBuildItem {

    private final Handler<HttpServerRequest> handler;

    public DefaultRouteBuildItem(Handler<HttpServerRequest> handler) {
        this.handler = handler;
    }

    public Handler<HttpServerRequest> getHandler() {
        return handler;
    }
}
