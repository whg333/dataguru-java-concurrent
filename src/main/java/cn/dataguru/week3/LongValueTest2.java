package cn.dataguru.week3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class LongValueTest2 {

	private static final String jvmBitStr = System.getProperty("sun.arch.data.model");
	private static final int longSize = Long.SIZE/8;
	
	private static final long firstLong = -456L;
	private static final long secondLong = 456L;
	private static final long thirdLong = -4294966840L;
	private static final long fourthLong = 4294966840L;
	
	public static void main(String[] args) {
		testByteOrder();
		//testLongValue();
	}
	
	private static void testLongValue(){
		System.out.println("Java程序运行在"+jvmBitStr+"位JVM下...");
		LongValue longValue = new LongValue();
		new WriteLongThread("Write-First", firstLong, longValue).start();
		new WriteLongThread("Write-Second", secondLong, longValue).start();
		new ReadThread("Read", longValue).start();
	}
	
	private static final class WriteLongThread extends Thread{
		private final long value;
		private final LongValue longValue;
		private WriteLongThread(String name, long value, LongValue longValue){
			super(name);
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
		private ReadThread(String name, LongValue longValue){
			super(name);
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
	
	public static byte[] long2bytes(long input) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (input & 0xff);
            input >>= 8;
        }
        return result;
    }
	
	private static void testByteOrder(){
		ByteBuffer buf = ByteBuffer.allocate(longSize*4);
		System.out.println("Default java endian: " + buf.order().toString());
		//buf.putLong(1);
		
		//buf.order(ByteOrder.LITTLE_ENDIAN);
		//System.out.println("Current java endian: " + buf.order().toString());
		//buf.putLong(2);
		
		buf.putLong(firstLong);
		buf.putLong(secondLong);
		buf.putLong(thirdLong);
		buf.putLong(fourthLong);

		buf.flip();
//		for (int i = 0; i < buf.limit(); i++){
//			System.out.println(buf.get() & 0xff);
//		}
//		
//		buf.rewind();
		byte[] bytes = new byte[longSize];
		for (int i = 0; i < buf.limit()/longSize; i++){
			buf.get(bytes);
			printBinStr(bytes);
		}

		System.out.println("My PC: " + ByteOrder.nativeOrder().toString());
	}
	
	private static void printBinStr(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(toBinStr(b));
			builder.append(":");
		}
		builder.deleteCharAt(builder.length()-1);
		System.out.println(builder.toString());
	}
	
	private static String toBinStr(byte b) {
		int result = b & 0xff;
		StringBuilder sb = new StringBuilder(Integer.toBinaryString(result));
		while (sb.length() < 8) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}

}
