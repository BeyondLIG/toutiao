package com.nowcoder;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread{
    private int tid;

    public MyThread(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; ++i ){
                Thread.sleep(1000);
                System.out.println(String.format("T%d:%d", tid, i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
                System.out.println("Producer:" + String.valueOf(i));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}


class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (true){
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class MutilThread {
    public static void testThread(){
        for (int i = 0; i < 10; ++i){
//             new MyThread(i).start();
        }

        for (int i = 0; i < 10; ++i){
            final int tid = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        for (int i = 0; i < 10; ++i){
                            Thread.sleep(1000);
                            System.out.println(String.format("T%d:%d", tid, i));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static Object object = new Object();

    public static void testSynchronized1(){
        synchronized (object){
            try {
                for (int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("T3:%d",i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2(){
        synchronized (object){
            try{
                for (int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4:%d",i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized3(){
        synchronized (new Object()){
            try{
                for (int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4:%d",i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static synchronized void testSynchronized4(){
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                System.out.println(String.format("T5:%d", i));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
//                    testSynchronized2();
                    testSynchronized3();
                }
            }).start();
        }
    }

    public static void testSynchronizedMethod(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        testSynchronized4();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();

    }


    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void sleep(int mill){
        try {
            Thread.sleep(mill);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void testWithAtomic(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }


    public static void testWithoutAtomic(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        System.out.println(++counter);
                    }
                }
            }).start();
        }
    }

    public static void testAtomic(){
//        testWithAtomic();
        testWithoutAtomic();
    }

    public static void testExecutor(){
//        ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(3);
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);

//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    sleep(1000);
//                    System.out.println("Executor1 " + i);
//                }
//            }
//        });

        service.submit(new Producer(q));
        service.submit(new Consumer(q), "Consumer1");
        service.submit(new Consumer(q), "Consumer2");

        service.shutdown();
        while (!service.isTerminated()){
            sleep(1000);
            System.out.println("Wait for termination.");
        }
    }

    public static void main(String[] argv){
//        testThread();
//        testSynchronized();
//        testSynchronizedMethod();
        testBlockingQueue();
//        testAtomic();
        testExecutor();
    }
}
