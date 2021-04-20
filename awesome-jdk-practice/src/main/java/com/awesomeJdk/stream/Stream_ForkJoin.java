package com.awesomeJdk.stream;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;
import org.junit.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/4/20 15:40.
 */
public class Stream_ForkJoin {

    @Test
    public void test() {
        int[] ints = IntStream.range(1, 901).toArray();
        System.out.println(IntStream.range(1,901).sum());
        ForkJoinExample forkJoinExample = new ForkJoinExample(ints);
        Integer compute = forkJoinExample.compute();
        System.out.println(compute);
    }

    class ForkJoinExample extends RecursiveTask<Integer> {
        private final int[] numbers;
        private final int begin;
        private final int end;

        public ForkJoinExample(int[] numbers) {
            this.numbers = numbers;
            this.begin   = 0;
            this.end     = numbers.length;
        }

        ForkJoinExample(int[] numbers, int begin, int end) {
            this.numbers = numbers;
            this.begin   = begin;
            this.end     = end;
        }

        @Override
        protected Integer compute() {
            int length =  end-begin;
            if (length <= 6) {
                int sum = 0;
                for (int i = begin; i < end; i++) {
                    sum += numbers[i];
                }
                return sum;
            } else {
                int middle = (this.begin +this.end) / 2;
                ForkJoinExample left = new ForkJoinExample(numbers, begin, middle);
                ForkJoinExample right = new ForkJoinExample(numbers, middle, end);
                ForkJoinTask<Integer> leftFork = left.fork();
                ForkJoinTask<Integer> rightFork = right.fork();
                Integer leftRes = leftFork.join();
                Integer rightRes = rightFork.join();
                return leftRes + rightRes;
            }

        }
    }
}
