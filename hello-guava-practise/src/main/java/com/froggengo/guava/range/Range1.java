package com.froggengo.guava.range;

import com.google.common.collect.Range;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 15:39.
 */
public class Range1 {

    public static void main(String[] args) {
        Range<Integer> integerRange = Range.lessThan(1);
        Range<Integer> open = Range.open(1, 10);

    }

}
