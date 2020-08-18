package com.awesomeJdk.practise.MyThreadPool;

public class MyInternelTask implements Runnable{
    private final MyRunnableQueue myRunnableQueue;
    private volatile boolean isRunning = false;

    public MyInternelTask(MyRunnableQueue myRunnableQueue) {
        this.myRunnableQueue =myRunnableQueue;
    }
    @Override
    public void run() {
        while(isRunning && !Thread.currentThread().isInterrupted()){
            try{
                Runnable take = myRunnableQueue.take();
                take.run();
            }catch (InterruptedException e){
                isRunning =false;
                break;
            }
        }
    }
    public void stop(){
        this.isRunning =false;
    }
}
