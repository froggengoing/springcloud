package com.awesomeJdk.practise.djmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

    /*
     * TDead-Code Elimination (DCE): 编译器会推断一些计算是多余的并消除他们。这会影响测试结果
     *显示返回结果，会避免这个问题
     * 永远从状态对象读取测试输入并返回计算的结果。
     */
    /**
     * Benchmark                                                    Mode  Cnt   Score    Error  Units
     * awesomeJdk.practise.djmh.JMHSample_08_DeadCode.baseline      avgt    5   0.480 ±  0.205  ns/op
     * awesomeJdk.practise.djmh.JMHSample_08_DeadCode.measureRight  avgt    5  36.608 ± 28.672  ns/op
     * awesomeJdk.practise.djmh.JMHSample_08_DeadCode.measureWrong  avgt    5   0.469 ±  0.511  ns/op
     */
    private double x = Math.PI;

    @Benchmark
    public void baseline() {
        // do nothing, this is a baseline
    }

    @Benchmark
    public void measureWrong() {
        // This is wrong: result is not used and the entire computation is optimized away.
        Math.log(x);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the result is being used.
        return Math.log(x);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_08_DeadCode.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
