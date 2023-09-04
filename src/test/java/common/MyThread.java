package common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyThread implements Runnable{
    @Override
    public void run(){
        Thread thread = Thread.currentThread();
        for(int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("errrrrr");
                e.printStackTrace();
            }
            System.out.println(thread.getName()+"打印"+i);
        }
    }
}
