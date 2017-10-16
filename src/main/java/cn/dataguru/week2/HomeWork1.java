package cn.dataguru.week2;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeWork1 {

	public static void main(String[] args) throws InterruptedException {
		startExample();
		//joinExample();
		//aliveExample();
		//waitExample();
		//poolExample();
	}
	
	private static void startExample(){
		Thread t3 = new StartThread("st3");
		Thread t2 = new StartThread("st2", t3);
		Thread t1 = new StartThread("st1", t2);
		
		t1.start();
	}
	
	private static final class StartThread extends Thread{
		private final Thread nextThread;
		private StartThread(String name){
			this(name, null);
		}
		private StartThread(String name, Thread preThread){
			super(name);
			this.nextThread = preThread;
		}
		@Override
		public void run() {
			System.out.println(getName()+" is running...");
			if(nextThread != null){
				nextThread.start();
			}
		}
	}
	
	private static void joinExample() throws InterruptedException{
		Thread t1 = new JoinThread("jt1");
		Thread t2 = new JoinThread("jt2");
		Thread t3 = new JoinThread("jt3");
		
		t1.start();
		t1.join();
		t2.start();
		t2.join();
		t3.start();
	}
	
	private static final class JoinThread extends Thread{
		private JoinThread(String name){
			super(name);
		}
		@Override
		public void run() {
			System.out.println(getName()+" is running...");
		}
	}
	
	private static void aliveExample(){
		Thread t1 = new AliveThread("at1");
		Thread t2 = new AliveThread("at2", t1);
		Thread t3 = new AliveThread("at3", t2);
		
		t1.start();
		t2.start();
		t3.start();
	}
	
	private static final class AliveThread extends Thread{
		private final Thread preThread;
		private AliveThread(String name){
			this(name, null);
		}
		private AliveThread(String name, Thread preThread){
			super(name);
			this.preThread = preThread;
		}
		@Override
		public void run() {
			while(preThread != null && preThread.isAlive()){
				;
			}
			System.out.println(getName()+" is running...");
		}
	}
	
	private static void waitExample(){
		Order order = new Order();
		Thread t1 = new WaitThread("wt1", order);
		Thread t2 = new WaitThread("wt2", order);
		Thread t3 = new WaitThread("wt3", order);
		
		order.add(t1);
		order.add(t2);
		order.add(t3);
		
		t1.start();
		t2.start();
		t3.start();
	}
	
	private static final class WaitThread extends Thread{
		private final Order order;
		private WaitThread(String name){
			this(name, null);
		}
		private WaitThread(String name, Order order){
			super(name);
			this.order = order;
		}
		@Override
		public void run() {
			try {
				order.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final class Order{
		private final LinkedList<Thread> queue;
		private Order(){
			this.queue = new LinkedList<Thread>();
		}
		public synchronized void add(Thread thread){
			queue.offer(thread);
		}
		public synchronized void run() throws InterruptedException{
			while(!queue.isEmpty() && queue.peek() != Thread.currentThread()){
				wait();
			}
			queue.poll();
			System.out.println(Thread.currentThread().getName()+" is running...");
			notifyAll();
		}
	}
	
	private static void poolExample(){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Thread t1 = new JoinThread("pt1");
		Thread t2 = new JoinThread("pt2");
		Thread t3 = new JoinThread("pt3");
		
		executor.execute(t1);
		executor.execute(t2);
		executor.execute(t3);
	}
	
}
