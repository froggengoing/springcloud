package com.froggengo.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WindowWordCount {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> text = env
                .socketTextStream("localhost", 9999);
        System.out.println(text.print());
        DataStream<Tuple2<String, Integer>> dataStream = text
                .flatMap(new Splitter())
                .keyBy(0)
                //感觉这个滑动窗口的意思是：有数据过来等10秒后统计结果，从下一次没有统计的数据开始在重新计算10秒再输出结果
                //a-5-b-6-c-7-d
                //a数据过来开始计算10秒，5秒后b过来，在10秒时统计一次结果
                //11秒c过来，重新在c这个点计算10秒，即统计c和d
                .timeWindow(Time.seconds(10))
                .sum(1);
        dataStream.print();
        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}
