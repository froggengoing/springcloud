package com.awesomeJdk.practise.MyThreadPool;

public interface MyDenypolicy{
    void reject(Runnable runnable,MyThreadPool myThreadPool);
    class MyDiscardDenyPolicy implements MyDenypolicy{

        @Override
        public void reject(Runnable runnable, MyThreadPool myThreadPool) {
            //do mothing
        }
    }
}
