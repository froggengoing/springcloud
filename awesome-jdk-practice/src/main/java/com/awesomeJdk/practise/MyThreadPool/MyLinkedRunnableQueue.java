package com.awesomeJdk.practise.MyThreadPool;

import java.util.LinkedList;

public class MyLinkedRunnableQueue implements  MyRunnableQueue{
    final  int limit;

    final MyDenypolicy denypolicy;

    final LinkedList<Runnable> runnableLinkedList=new LinkedList<>();

    final MyThreadPool myThreadPool;

    public MyLinkedRunnableQueue(int limit, MyDenypolicy denypolicy, MyThreadPool myThreadPool) {
        this.limit = limit;
        this.denypolicy = denypolicy;
        this.myThreadPool = myThreadPool;
    }


    @Override
    public void off(Runnable runnable) {
        synchronized (runnableLinkedList){
            if(runnableLinkedList.size() >=limit){
                denypolicy.reject(runnable,myThreadPool);
            }else {
                runnableLinkedList.add(runnable);
                runnableLinkedList.notifyAll();
            }
        }
    }

    @Override
    public Runnable take() throws InterruptedException {
        synchronized (runnableLinkedList){
            while(runnableLinkedList.isEmpty()){
                try{
                    //没有可执行任务，将当前线程挂起
                    runnableLinkedList.wait();
                }catch(InterruptedException e){
                    throw e;
                }
            }
            return runnableLinkedList.removeFirst();
        }
    }

    @Override
    public int size() {
        synchronized (runnableLinkedList){
            return runnableLinkedList.size();
        }
    }
}
