package com.awesomeJdk.practise.djmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class JMHSample_05_StateFixtures {

    double x;

    /**
     * 这里setup类似于before，即基准测试前执行 的方法（可能是一组也可能是一次）
     */
    @Setup
    public void prepare() {
        x = Math.PI;
    }

    /**
     * 这里 TearDown 类似于after，即基准测试后执行 的方法（可能是一组也可能是一次）
     */

    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }


    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_05_StateFixtures.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }

}
