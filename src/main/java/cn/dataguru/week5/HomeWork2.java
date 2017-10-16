package cn.dataguru.week5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeWork2 {

	private static final int PRODUCT_NUM = 10000000;
	
	enum QueueType{
		ConcurrentLinkedQueue,
		LinkedBlockingQueue;
	}
	
	public static void main(String[] args){
		int testCount = 3;
//		testQueue(testCount, 1);
//		testQueue(testCount, 2);
//		testQueue(testCount, 3);
//		testQueue(testCount, 4);
//		testQueue(testCount, 5);
//		testQueue(testCount, 8);
//		testQueue(testCount, 10);
//		testQueue(testCount, 15);
//		testQueue(testCount, 20);
//		testQueue(testCount, 30);
		testQueue(testCount, 50);
		testQueue(testCount, 80);
		testQueue(testCount, 100);
		testQueue(testCount, 110);
		testQueue(testCount, 125);
		testQueue(testCount, 150);
	}
	
	private static void testQueue(int testCount, int threadNum){
		int readThreadNum = threadNum;
		int writeThreadNum = threadNum;
		System.out.println("\n--------- testCount="+testCount+", readThreadNum="+readThreadNum+", writeThreadNum="+writeThreadNum+" ---------");
		testQueue(QueueType.ConcurrentLinkedQueue, testCount, readThreadNum, writeThreadNum);
		testQueue(QueueType.LinkedBlockingQueue, testCount, readThreadNum, writeThreadNum);
	}
	
	private static void testQueue(QueueType type, int testCount, int readThreadNum, int writeThreadNum){
		double sumTime = 0;
		for(int i=0;i<testCount;i++){
			sumTime += subTestQueue(type, i+1, readThreadNum, writeThreadNum);
		}
		System.out.println("type="+type+",\tAvgTime taken: "+sumTime/testCount+" seconds");
	}
	
	private static double subTestQueue(QueueType type, int testCount, int readThreadNum, int writeThreadNum){
		final Queue<String> fromQueue = getQueue(type);
		final Queue<String> toQueue = getQueue(type);
		final List<Thread> runThreads = new ArrayList<Thread>();
		for(int i=0;i<PRODUCT_NUM;i++){
			fromQueue.offer("a");
		}
		
		long begin = System.nanoTime();
		final AtomicInteger pollNum = new AtomicInteger(0);
		for(int i=0;i<readThreadNum;i++){
			runThreads.add(new Reader(testCount+"-"+type+"-Reader-"+(i+1), fromQueue, toQueue, pollNum));
		}
		for(int i=0;i<writeThreadNum;i++){
			runThreads.add(new Writer(testCount+"-"+type+"-Writer-"+(i+1), fromQueue, toQueue, pollNum));
		}
		
		for(Thread thread:runThreads){
			thread.start();
		}
		
		try {
			for(Thread thread:runThreads){
				thread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		double time = (System.nanoTime()-begin)/1.0e9;
//		if(!fromQueue.isEmpty()){
//			System.out.println("fromQueue队列非空！！！queue="+Arrays.toString(fromQueue.toArray(new String[0])));
//		}
//		int toQueueSize = toQueue.size();
//		if(toQueueSize != PRODUCT_NUM){
//			System.out.println("toQueue队列size="+toQueueSize);
//		}
		//System.out.println("testTimes="+(i+1)+",\tthreadNum="+threadNum+",\tTime taken: "+time+" seconds");
		return time;
	}
	
	private static Queue<String> getQueue(QueueType type) {
		switch (type) {
		case LinkedBlockingQueue:
			return new LinkedBlockingQueue<String>();
		case ConcurrentLinkedQueue:
			return new ConcurrentLinkedQueue<String>();
		default:
			throw new IllegalArgumentException("Unsupported Type!");
		}
	}
	
	
	/**
	 * 写线程，队列写操作（poll）线程，模拟并发修改
	 * 
	 * @author whg
	 * 2015-7-31上午10:23:38
	 */
	private static class Writer extends Thread{
		private final Queue<String> fromQueue;
		private final Queue<String> toQueue;
		private Writer(String name, Queue<String> fromQueue, Queue<String> toQueue, AtomicInteger pollNum){
			super(name);
			this.fromQueue = fromQueue;
			this.toQueue = toQueue;
		}
		@Override
		public void run() {
			while(!fromQueue.isEmpty()){
				String str = null;
				if((str=fromQueue.poll()) != null){
					toQueue.offer(str);
				}
			}
		}
	}
	
	/**
	 * 读线程，队列读操作（peek）的线程，模拟并发访问
	 * 
	 * @author whg
	 * 2015-7-31上午10:22:51
	 */
	private static class Reader extends Thread{
		private final Queue<String> fromQueue;
		private final Queue<String> toQueue;
		private Reader(String name, Queue<String> fromQueue, Queue<String> toQueue, AtomicInteger pollNum){
			super(name);
			this.fromQueue = fromQueue;
			this.toQueue = toQueue;
		}
		@Override
		public void run() {
			while(!fromQueue.isEmpty()){
				fromQueue.peek();
				toQueue.peek();
			}
		}
	}
	
}

