package com.vertx.verticle.learn.a2eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 17:11.
 */
public class A1EventBus extends AbstractVerticle {

    /**
     * @see HazelcastClusterManager
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                vertx.eventBus().<JsonObject>request("hello", new JsonObject().put("name", "fly"), res -> {
                    JsonObject body = res.result().body();
                    String msg = body.getString("msg");
                    System.out.println(LocalDateTime.now() + " : " + msg);
                });
            }
        }, 0L, 1000L);

    }
}

