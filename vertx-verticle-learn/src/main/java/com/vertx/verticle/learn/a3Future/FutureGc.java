package com.vertx.verticle.learn.a3Future;

import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * @author froggengo@qq.com
 * @date 2021/2/10 0:19.
 */
public class FutureGc {
    private Future<Boolean> runFirstWork() {
        Promise<Boolean> promise = Promise.promise();
        return promise.future();
    }
}
