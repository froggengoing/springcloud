package com.froggengo.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
/**
 * http://flink.apache.org/docs/stable/
 */
public class BatchJob {
    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromArgs(args);
        //1、环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //env.getConfig().setGlobalJobParameters(params);
        DataSet<String> text;
        //2、数据源
        text = env.readTextFile("D:\\1PracticeProject\\cloud2020\\awe-flink-practise\\src\\main\\resources\\wordcount.txt");
        //text = WordCountData.getDefaultTextLineDataSet(env);
        text.print();
        //3、处理
        DataSet<Tuple2<String, Integer>> counts = text.flatMap(new BatchJob.Tokenizer()).groupBy(new int[] { 0 }).sum(1);
        //4、目的地
        counts.print();
    }
    // 自定义函数
    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {

            // 统一大小写并把每一行切割为单词
            String[] tokens = value.toLowerCase().split("\\W+");
            // 消费二元组
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<String, Integer>(token, 1));
                }
            }
        }
    }
}
