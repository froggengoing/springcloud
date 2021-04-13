package com.awesomeJdk.practise.athread;


import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 启动时检查是否有lock文件，如果有退出运行。没有则创建。
 * 退出运行时删除lock文件，这样来确保只能启动一个实例。
 * 钩子方法可以在一下几种场景中被调用：
 * 1、程序正常退出
 * 2、使用System.exit()
 * 3、终端使用Ctrl+C触发的中断
 * 4、系统关闭
 * 5、OutOfMemory宕机
 * 6、使用Kill pid命令干掉进程（注：在使用kill -9 pid时，是不会被调用的）
 */
public class Thread13_Hook {

    public static final String LOCK_PATH = System.getProperty("user.dir");
    public static final String LOCK_FIEL = ".lock";
    public static volatile boolean isExists = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("退出运行！");
                if (!isExists) {
                    Paths.get(LOCK_PATH, LOCK_FIEL).toFile().delete();
                }

            }
        });
        System.out.println("lock文件位置:" + LOCK_PATH);
        checkRunning();
        while (true) {
            System.out.println("running");
            Thread.sleep(2_000);
        }
    }

    private static void checkRunning() throws IOException {
        Path path = Paths.get(LOCK_PATH, LOCK_FIEL);
        if (path.toFile().exists()) {
            isExists = true;
            throw new RuntimeException("程序已启动！");
        }
        //Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(PERMISSION);
        Files.createFile(path);
    }

    @Test
    public void test() throws IOException {
        Path path = Paths.get(LOCK_PATH, LOCK_FIEL);
        if (path.toFile().exists()) {
            isExists = true;
            System.out.println("文件存在：" + path.toFile().getCanonicalPath());
            path.toFile().delete();
        }
    }
}
