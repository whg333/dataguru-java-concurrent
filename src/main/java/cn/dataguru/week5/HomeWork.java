package cn.dataguru.week5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeWork {

	private static final int PRODUCT_NUM = 10000000;
	
	enum QueueType{
		LinkedBlockingDeque,
		ConcurrentLinkedQueue;
	}
	
	public static void main(String[] args) throws InterruptedException {
		testQueueTypes(10, 1, 1, 1);
		testQueueTypes(10, 2, 2, 2);
		testQueueTypes(10, 5, 5, 5);
		testQueueTypes(10, 10, 10, 10);
		testQueueTypes(10, 20, 10, 20);
		testQueueTypes(10, 20, 10, 5);
		testQueueTypes(10, 5, 20, 10);
		testQueueTypes(10, 20, 5, 10);
		testQueueTypes(10, 30, 30, 30);
	}
	
	private static void testQueueTypes(int testCount, int offerThreadNum, int pollThreadNum, int peekThreadNum){
		System.out.println("\n--------- testCount="+testCount+", offerThreadNum="+offerThreadNum
				+", pollThreadNum="+pollThreadNum+", peekThreadNum="+peekThreadNum+" ---------");
		testQueue(QueueType.LinkedBlockingDeque, testCount, offerThreadNum, pollThreadNum, peekThreadNum);
		testQueue(QueueType.ConcurrentLinkedQueue, testCount, offerThreadNum, pollThreadNum, peekThreadNum);
	}
	
	private static void testQueue(QueueType type, int testCount, int offerThreadNum, int pollThreadNum, int peekThreadNum){
		double sumTime = 0;
		for(int i=0;i<testCount;i++){
			long begin = System.nanoTime();
			subTestQueue(type, i+1, offerThreadNum, pollThreadNum, peekThreadNum);
			double time =(System.nanoTime()-begin)/1.0e9;
			//System.out.println("testTimes="+(i+1)+",\tthreadNum="+threadNum+",\tTime taken: "+time+" seconds");
			sumTime += time;
			//System.out.println("------------------------"+(i+1)+"-------------------------");
		}
		System.out.println("type="+type+",\tAvgTime taken: "+sumTime/testCount+" seconds");
	}
	
	private static void subTestQueue(QueueType type, int testCount, int offerThreadNum, int pollThreadNum, int peekThreadNum){
		final Queue<String> queue = getQueue(type);
		final AtomicInteger offerNum = new AtomicInteger(PRODUCT_NUM);
		final AtomicInteger pollNum = new AtomicInteger(0);
		final List<Thread> runThreads = new ArrayList<Thread>();
		for(int i=0;i<offerThreadNum;i++){
			runThreads.add(new Producer(testCount+"-Producer-"+(i+1), queue, offerNum));
		}
		for(int i=0;i<pollThreadNum;i++){
			runThreads.add(new Comsumer(testCount+"-Comsumer-"+(i+1), queue, pollNum));
		}
		for(int i=0;i<peekThreadNum;i++){
			runThreads.add(new Peeker(testCount+"-Peeker-"+(i+1), queue, pollNum));
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
		
		if(!queue.isEmpty()){
			System.out.println("队列非空！！！queue="+Arrays.toString(queue.toArray(new String[0])));
		}
	}
	
	private static Queue<String> getQueue(QueueType type) {
		switch (type) {
		case LinkedBlockingDeque:
			return new LinkedBlockingDeque<String>();
		case ConcurrentLinkedQueue:
			return new ConcurrentLinkedQueue<String>();
		default:
			throw new IllegalArgumentException("Unsupported Type!");
		}
	}
	
	private static class Producer extends Thread{
		private final Queue<String> queue;
		private final AtomicInteger offerNum;
		private Producer(String name, Queue<String> queue, AtomicInteger offerNum){
			super(name);
			this.queue = queue;
			this.offerNum = offerNum;
		}
		@Override
		public void run() {
			while(offerNum.get() > 0){
				if(queue.offer("a")){
					while(true){
						int oldVal = offerNum.get();
						if(oldVal <= 0){
							break;
						}
						offerNum.compareAndSet(oldVal, oldVal-1);
					}
					//System.out.println(Thread.currentThread().getName()+" Offer "+product);
				}
			}
			//System.out.println(Thread.currentThread().getName()+" Final offerNum="+offerNum.get());
		}
	}
	
	private static class Comsumer extends Thread{
		private final Queue<String> queue;
		private final AtomicInteger pollNum;
		private Comsumer(String name, Queue<String> queue, AtomicInteger pollNum){
			super(name);
			this.queue = queue;
			this.pollNum = pollNum;
		}
		@Override
		public void run() {
			while(pollNum.get() < PRODUCT_NUM
					|| !queue.isEmpty()){
				if(queue.poll() != null){
					while(true){
						int oldVal = pollNum.get();
						if(oldVal >= PRODUCT_NUM){
							break;
						}
						pollNum.compareAndSet(oldVal, oldVal+1);
					}
					//System.out.println(Thread.currentThread().getName()+" Poll "+product);
				}
			}
			//System.out.println(Thread.currentThread().getName()+" Final pollNum="+pollNum.get());
		}
	}
	
	private static class Peeker extends Thread{
		private final Queue<String> queue;
		private final AtomicInteger pollNum;
		private Peeker(String name, Queue<String> queue, AtomicInteger pollNum){
			super(name);
			this.queue = queue;
			this.pollNum = pollNum;
		}
		@Override
		public void run() {
			while(pollNum.get() < PRODUCT_NUM){
				queue.peek();
				//System.out.println(Thread.currentThread().getName()+" Poll "+product);
			}
			//System.out.println(Thread.currentThread().getName()+" Final takeNum="+takeNum.get());
		}
	}
	
}
