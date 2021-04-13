package com.vertx.verticle.learn.a2eventbus;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author froggengo@qq.com
 * @date 2021/2/5 8:29.
 */
public class MainVerticle {

    public static void main(String[] args) {
        DeploymentOptions vertxOptions = new DeploymentOptions().setMaxWorkerExecuteTime(300_000);
        Vertx.vertx().deployVerticle(new A2EventBus(),vertxOptions,res->{
            System.out.println("cg");
        });
    }

}
