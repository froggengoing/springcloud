package com.vertx.verticle.learn.a3Future;

import io.vertx.core.Vertx;

/**
 * @author froggengo@qq.com
 * @date 2021/2/2 18:43.
 */
public class Future1 {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new FutureVerticle1());
    }
}
