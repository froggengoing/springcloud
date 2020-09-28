package com.froggengo.flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichReduceFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.ReduceOperator;

import java.util.HashSet;

public class MapJob {

    public static void main(String[] args) throws Exception {
        Trade trade1 = new Trade("1", "苹果", 1, 10);
        Trade trade2 = new Trade("2", "西瓜", 2, 20);
        Trade trade3 = new Trade("3", "苹果", 3, 40);
        Trade trade4 = new Trade("4", "西瓜", 4, 50);
        Trade trade5 = new Trade("5", "苹果", 5, 60);
        HashSet<Trade> map = new HashSet<>();
        map.add(trade5);
        map.add(trade4);
        map.add(trade3);
        map.add(trade2);
        map.add(trade1);
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<Trade> source = env.fromCollection(map);
        ReduceOperator<Trade> reduce = source.map(new MapFunction<Trade, Trade>() {
            @Override
            public Trade map(Trade value) throws Exception {
                value.setCount(1);
                return value;
            }
        }).groupBy("good").reduce(new RichReduceFunction<Trade>() {
            @Override
            public Trade reduce(Trade value1, Trade value2) throws Exception {
                Trade trade = new Trade();
                trade.setGood(value1.getGood());
                trade.setCount(value1.getCount() + value2.getCount());
                trade.setPay(value1.getPay() + value2.getPay());
                trade.setQuantity(value1.getQuantity() + value2.getQuantity());
                //这里无法计算平均值
                trade.setPayAvg((value1.getPay()+value2.getPay())/2);
                return trade;
            }
        });
        reduce.print();
    }


}
