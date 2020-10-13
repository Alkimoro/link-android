package cn.linked.commonlib.util.common;

import android.os.Handler;

public class LoopHandler extends Handler {
    private int num;
    private int currentNum;
    private long interval;
    private long delay;
    public LoopHandler(int num,long interval,long delay){
        super();
        this.num=num;
        this.interval=interval;
        this.delay=delay;
    }
    public void loopPost(Runnable r){
        LoopRunnable runnable=new LoopRunnable(r);
        postDelayed(runnable,delay);
    }
    public int getCurrentNum(){
        return currentNum;
    }
    public int getNum(){
        return num;
    }
    private class LoopRunnable implements Runnable{
        private Runnable r;
        LoopRunnable(Runnable r){
            this.r=r;
        }
        @Override
        public void run() {
            r.run();
            currentNum++;
            if(currentNum<num){
                postDelayed(this,interval);
            }
        }
    }
}
