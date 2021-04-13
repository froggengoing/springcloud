package com.froggengo.class8ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import java.lang.management.BufferPoolMXBean;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class MyByteBufTest {

    public static void main(String[] args) throws InterruptedException, IllegalAccessException {
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);
//        byteBuffer.put((byte) 1);
//        //netty
//        ByteBuf byteBuf = Unpooled.directBuffer(99);
//        byteBuf.writeInt(99);
//
//        List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactoryHelper.getBufferPoolMXBeans();
//        BufferPoolMXBean directBufferMXBean = bufferPoolMXBeans.get(0);
//        // hasCleaner的DirectBuffer的数量
//        long count = directBufferMXBean.getCount();
//        // hasCleaner的DirectBuffer的堆外内存占用大小，单位字节
//        long memoryUsed = directBufferMXBean.getMemoryUsed();
//        System.out.println(count);
//        System.out.println(memoryUsed);
//        //netty
//        Field memoryCounter = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
//        memoryCounter.setAccessible(true);
//        AtomicLong o = (AtomicLong) memoryCounter.get(PlatformDependent.class);
//        System.out.println("计数" + o);
//        synchronized (directBufferMXBean) {
//            directBufferMXBean.wait();
//        }
    }

/*    public static void main(String[] args) {
        ByteBuf buffer = Unpooled.buffer();
        ByteBuf byteBuf = Unpooled.directBuffer();
        CompositeByteBuf byteBufs = Unpooled.compositeBuffer();
        PooledByteBufAllocator aDefault = PooledByteBufAllocator.DEFAULT;
        ByteBuf buffer1 = aDefault.buffer();
        ByteBuffer allocate = ByteBuffer.allocate(1);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1);
        //no-clearn
        ByteBuf buffer2 = UnpooledByteBufAllocator.DEFAULT.buffer(1024);


    }*/
//
//    @Test
//    public void test() throws IllegalAccessException, InterruptedException {
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);
//        byteBuffer.put((byte) 1);
//        //netty
//        ByteBuf byteBuf = Unpooled.directBuffer(99);
//        byteBuf.writeInt(99);
//
//        List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactoryHelper.getBufferPoolMXBeans();
//        BufferPoolMXBean directBufferMXBean = bufferPoolMXBeans.get(0);
//        // hasCleaner的DirectBuffer的数量
//        long count = directBufferMXBean.getCount();
//        // hasCleaner的DirectBuffer的堆外内存占用大小，单位字节
//        long memoryUsed = directBufferMXBean.getMemoryUsed();
//        System.out.println(count);
//        System.out.println(memoryUsed);
//        //netty
//        Field memoryCounter = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
//        memoryCounter.setAccessible(true);
//        AtomicLong o = (AtomicLong) memoryCounter.get(PlatformDependent.class);
//        System.out.println("计数" + o);
//        memoryCounter.wait();
//    }
}
