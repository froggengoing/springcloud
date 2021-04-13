package com.awesomeJdk.practise.dstream.myinterface;

import java.util.function.Consumer;

/**
 * @author fly
 */
public interface ForEach <T>{

    /**
     * 迭代器遍历
     * */
    void forEach(Consumer<? super T> action) ;
}