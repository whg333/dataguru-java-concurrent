package cn.dataguru.week4;

import java.util.concurrent.atomic.AtomicInteger;

public class TestMyAtomicInteger {

	public static void main(String[] args) {
//		MyAtomicInteger mai = new MyAtomicInteger();
//		new AtomicIntegerThread("T1", mai).start();
//		new AtomicIntegerThread("T2", mai).start();
		System.out.println(31-Integer.numberOfLeadingZeros(Integer.MAX_VALUE));
		System.out.println(31-Integer.numberOfLeadingZeros(8));
		System.out.println(31-Integer.numberOfLeadingZeros(4));
		System.out.println(31-Integer.numberOfLeadingZeros(2));
		System.out.println(32-Integer.numberOfLeadingZeros(0));
	}
	
	public static final class AtomicIntegerThread extends Thread{
		private final MyAtomicInteger mai;
		private AtomicIntegerThread(String name, MyAtomicInteger mai){
			super(name);
			this.mai = mai;
		}
		@Override
		public void run() {
			while(true){
				System.out.println(Thread.currentThread().getName()+" outside current="+mai.getAndIncrement());
			}
		}
	}
	
}
