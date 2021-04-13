package com.vertx.verticle.learn.a3Future;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author froggengo@qq.com
 * @date 2021/2/2 18:49.
 * future不执行complete或failed
 */
public class FutureVerticle1 extends AbstractVerticle {

    @Override
    public void start() {
        AtomicInteger atomicInteger = new AtomicInteger();
        vertx.setPeriodic(10,res->{
            int i = atomicInteger.incrementAndGet();
            AtomicInteger other = new AtomicInteger(i);
            System.out.println("test:"+other.get());
            Future<Boolean> booleanFuture = runFirstWork();
            booleanFuture.onComplete(boolRes->{
                System.out.println("上一步成功:"+other.get());
            });
        });


    }

    private Future<Boolean> runFirstWork() {
        Promise<Boolean> promise = Promise.promise();
        vertx.setTimer(100_000,res->{
            promise.complete();
        });
        return promise.future();
    }
}
