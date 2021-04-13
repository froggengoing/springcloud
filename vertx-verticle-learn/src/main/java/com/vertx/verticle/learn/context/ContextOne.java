package com.vertx.verticle.learn.context;

import io.netty.channel.nio.NioEventLoopGroup;
import io.vertx.core.Vertx;

/**
 * @author froggengo@qq.com
 * @date 2021/2/6 22:40.
 */
public class ContextOne {

    public static void main(String[] args) {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        nioEventLoopGroup.execute(()->{
            System.out.println("hello");
        });
    }

}
