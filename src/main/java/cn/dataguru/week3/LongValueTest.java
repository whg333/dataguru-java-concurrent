package cn.dataguru.week3;

import java.util.concurrent.atomic.AtomicInteger;

public class LongValueTest {

	private static final String jvmBitStr = System.getProperty("sun.arch.data.model");
	
	private static final long firstLong = -456L;
	private static final long secondLong = 456L;
	private static final long thirdLong = -4294966840L;
	private static final long fourthLong = 4294966840L;
	
	public static void main(String[] args) {
		System.out.println("Java程序运行在"+jvmBitStr+"位JVM下...");
		LongValue longValue = new LongValue();
		new WriteLongThread(firstLong, longValue).start();
		new WriteLongThread(secondLong, longValue).start();
		new ReadThread(longValue).start();
	}
	
	private static final class WriteLongThread extends Thread{
		private final long value;
		private final LongValue longValue;
		private WriteLongThread(long value, LongValue longValue){
			this.value = value;
			this.longValue = longValue;
		}
		@Override
		public void run() {
			while(true){
				longValue.setValue(value);
			}
		}
	}
	
	private static final class ReadThread extends Thread{
		private final AtomicInteger count = new AtomicInteger(0);
		private final LongValue longValue;
		private ReadThread(LongValue longValue){
			this.longValue = longValue;
		}
		@Override
		public void run() {
			while(true){
				final int cv = count.incrementAndGet();
				if(longValue.value == thirdLong || longValue.value == fourthLong){
					System.out.println("在"+jvmBitStr+"位JVM下long不是原子操作！count="+cv+", value="+longValue.value);
				}
			}
		}
	}
	
	private static final class LongValue{
		private long value;
		public void setValue(final long value) {
			this.value = value;
		}
	}

}
