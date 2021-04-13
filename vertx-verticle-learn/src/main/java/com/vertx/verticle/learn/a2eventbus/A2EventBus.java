package com.vertx.verticle.learn.a2eventbus;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.time.LocalDateTime;
import java.util.Map.Entry;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 17:11.
 */
public class A2EventBus extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer("hello", res -> {
            JsonObject body = res.body();
            String name = body.getString("name");
            System.out.println(LocalDateTime.now() + ":" + name);
            res.reply(new JsonObject().put("msg", "hello " + name));
        });
        Context context = vertx.getOrCreateContext();
        @Nullable JsonObject config = context.config();
        for (Entry<String, Object> entry : config) {
            System.out.println(entry.getKey()+"--"+entry.getValue());
        }
        System.out.println(Vertx.currentContext().getInstanceCount());
        System.out.println(Vertx.currentContext().isEventLoopContext());
        System.out.println(Vertx.currentContext().isWorkerContext());
    }
}
