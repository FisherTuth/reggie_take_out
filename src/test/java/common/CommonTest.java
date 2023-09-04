package common;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@SpringBootTest
public class CommonTest {
    @Test
    public void test01(){
        System.out.println("测试");
    }
    @Test
    public void testNewThread(){
        Thread thread1 = new Thread(new MyThread());
        thread1.setName("线程1");
        Thread thread2 = new Thread(new MyThread());
        thread2.setName("线程2");
        thread1.start();
        thread2.start();
    }

    @Test
    public void testNewCallable() throws ExecutionException, InterruptedException {
        Callable callable = new MyCallable();
        FutureTask<Integer>futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask);
        thread.start();
        Integer integer = futureTask.get();
        System.out.println(integer);
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyThread());
        thread1.setName("线程1");
        Thread thread2 = new Thread(new MyThread());
        thread2.setName("线程2");
        thread1.start();
        thread2.start();
    }
}
