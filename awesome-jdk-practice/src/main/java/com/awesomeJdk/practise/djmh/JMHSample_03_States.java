package com.awesomeJdk.practise.djmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMHSample_03_States {
/*Benchmark                                                      Mode  Cnt          Score         Error  Units
awesomeJdk.practise.djmh.JMHSample_03_States.measureShared    thrpt    5   43535134.368 ±  865465.188  ops/s
awesomeJdk.practise.djmh.JMHSample_03_States.measureUnshared  thrpt    5  223228661.188 ± 2343482.475  ops/s
*/
    /**
     *运行相同测试的所有线程将共享实例。可以用来测试状态对象的多线程性能
     */

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double x = Math.PI;
    }

    /**
     * 默认的 State，每个测试线程分配一个实例
     */
    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }


    @Benchmark
    public void measureUnshared(ThreadState state) {
        state.x++;
    }

    @Benchmark
    public void measureShared(BenchmarkState state) {
        state.x++;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States.class.getSimpleName())
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
