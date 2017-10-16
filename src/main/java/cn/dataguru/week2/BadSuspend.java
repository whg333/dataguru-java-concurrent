package cn.dataguru.week2;

import java.util.concurrent.TimeUnit;

public class BadSuspend {

	private static final Object lock = new Object();
	
	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new SuspendThread("t1");
		Thread t2 = new SuspendThread("t2");
		
		t1.start();
		TimeUnit.SECONDS.sleep(1);
		t2.start();
		TimeUnit.SECONDS.sleep(1);
		
		t1.resume();
		System.out.println(t1.getName()+" has resumes...");
		//TimeUnit.SECONDS.sleep(1);
		t2.resume();
		System.out.println(t2.getName()+" has resumes...");
		
		t1.join();
		t2.join();
	}
	
	private static final class SuspendThread extends Thread{
		private SuspendThread(String name){
			super(name);
		}
		@Override
		public void run() {
			System.out.println(getName()+" ready get into synchronized(lock)...");
			synchronized (lock) {
				System.out.println(getName()+" begin suspend...");
				Thread.currentThread().suspend();
				System.out.println(getName()+" had been resumed,ending suspend...");
			}
			System.out.println(getName()+" out of synchronized(lock)...");
		}
	}
	
}
