package cn.dataguru.week10;

import java.util.concurrent.locks.ReentrantLock;

public class HomeWorkReenterantLock {

	public static void main(String[] args) {
		ShareObj a = new ShareObj("A");
		ShareObj b = new ShareObj("B");

		new LockThread("ReentrantLockThread-1", a, b).start();
		new LockThread("ReentrantLockThread-2", b, a).start();
	}

	private static class ShareObj {
		public final ReentrantLock lock = new ReentrantLock();
		public final String name;
		public ShareObj(String name) {
			this.name = name;
		}
	}

	private static class LockThread extends Thread {

		private final ShareObj first;
		private final ShareObj second;

		public LockThread(String name, ShareObj first, ShareObj second) {
			super(name);
			this.first = first;
			this.second = second;
		}

		@Override
		public void run() {
			String currThreadName = Thread.currentThread().getName();
			System.out.println(currThreadName+" trying lock "+ first.name);
			first.lock.lock();
			try{
				System.out.println(currThreadName+" locked "+ first.name+" successful!");
				
				sleepToLetOtherThreadLock();
				
				System.out.println(currThreadName+" trying lock "+ second.name);
				second.lock.lock();
				try{
					System.out.println(currThreadName+" locked "+ second.name+" successful!");
				}finally{
					second.lock.unlock();
				}
			}finally{
				first.lock.unlock();
			}
			
		}
		
		/** 睡一会让其他线程获取锁 */
		private void sleepToLetOtherThreadLock(){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}