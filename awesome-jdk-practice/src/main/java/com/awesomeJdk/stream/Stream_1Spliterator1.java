package com.awesomeJdk.stream;

import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/3/14 21:30.
 */
public class Stream_1Spliterator1 {

    /**
     * 如果我们调用trySplit一个分隔符，它将返回一个分隔符，其中包含调用方分隔符不会覆盖的元素。
     * 这就是说，我们用它将分离器分成多个部分。这样做的主要好处trySplit是我们可以并行处理分割的元素。
     * @link https://baijiahao.baidu.com/s?id=1649268592254063537&wfr=spider&for=pc
     */
    public static void main(String[] args) {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Spliterator<Integer> firstSpliterator = stream.spliterator();
        Spliterator<Integer> secondSpliterator = firstSpliterator.trySplit();
        Spliterator<Integer> thirdSpliterator = firstSpliterator.trySplit();
        System.out.println("firstSpliterator");
        while (firstSpliterator.tryAdvance(n -> System.out.println("first =>" + n))) {

        }
        System.out.println("secondSpliterator");
        while (secondSpliterator.tryAdvance(n -> System.out.println("second =>" + n))) {

        }
        System.out.println("thirdSpliterator");
        while (thirdSpliterator.tryAdvance(n -> System.out.println("third =>" + n))) {

        }
    }

}
