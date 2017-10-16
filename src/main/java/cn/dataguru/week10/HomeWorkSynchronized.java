package cn.dataguru.week10;

public class HomeWorkSynchronized {

	public static void main(String[] args) {
		ShareObj a = new ShareObj("A");
		ShareObj b = new ShareObj("B");

		new LockThread("SynchronizedLockThread-1", a, b).start();
		new LockThread("SynchronizedLockThread-2", b, a).start();
	}

	private static class ShareObj {
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
			synchronized(first){
				System.out.println(currThreadName+" locked "+ first.name+" successful!");
				
				sleepToLetOtherThreadLock();
				
				System.out.println(currThreadName+" trying lock "+ second.name);
				synchronized(second){
					System.out.println(currThreadName+" locked "+ second.name+" successful!");
				}
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