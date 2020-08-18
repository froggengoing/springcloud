package com.froggengo.class12ProtoBuf;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoBufMain {


    public static void main(String[] args) throws InvalidProtocolBufferException {
        DataInfo.Studeng.Builder builder = DataInfo.Studeng.newBuilder();
        DataInfo.Studeng studeng = builder.setName("菠萝").setAge(12).setAddress("中国").build();
        byte[] bytes = studeng.toByteArray();
        /**
         * 从字节数组反序列获取studeng对象
         */
        DataInfo.Studeng studeng1 = DataInfo.Studeng.parseFrom(bytes);
        System.out.println(studeng1.getAge());
        System.out.println(studeng1.getAddress());
        System.out.println(studeng1.getName());
    }
}
