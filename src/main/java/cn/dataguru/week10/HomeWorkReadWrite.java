package cn.dataguru.week10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class HomeWorkReadWrite {

	public static void main(String[] args) throws InterruptedException {
		int testCount = args.length > 0 ? Integer.parseInt(args[0]) : 5;
		testReadWrite(10, testCount);
		testReadWrite(100, testCount);
		testReadWrite(1000, testCount);
		testReadWrite(10000, testCount);
		testReadWrite(100000, testCount);
		testReadWrite(1000000, testCount);
	}
	
	private static void testReadWrite(int dataAmount, int testCount) throws InterruptedException {
		testReadWrite(20, 1, LockType.ReadWriteLock, dataAmount, testCount);
		testReadWrite(20, 1, LockType.StampedLock, dataAmount, testCount);
	}
	
	private static void testReadWrite(int readThreadNum, int writeThreadNum, LockType type, int dataAmount, int testCount) 
			throws InterruptedException {
		long sum = 0;
		for(int i=0;i<testCount;i++){
			sum += testReadWrite(readThreadNum, writeThreadNum, type, dataAmount);
		}
		System.out.printf("%-15sdataAmount=%-9dtestCount=%-3davgTime=%-9.2fmillisecond\n", 
				type, dataAmount, testCount, sum/testCount/1.0e6);
	}
	
	private static long testReadWrite(int readThreadNum, int writeThreadNum, LockType type, int dataAmount) 
			throws InterruptedException {
		ShareObj shareObj = getShareObj(type);
		List<Thread> threads = new ArrayList<Thread>();
		for(int i=0;i<readThreadNum;i++){
			threads.add(new ReadThread("ReadThread-"+(i+1), shareObj, dataAmount));
		}
		for(int i=0;i<writeThreadNum;i++){
			threads.add(new WriteThread("WriteThread-"+(i+1), shareObj, dataAmount));
		}
		
		long start = System.nanoTime();
		for(Thread thread:threads){
			thread.start();
		}
		for(Thread thread:threads){
			thread.join();
		}
		long time = System.nanoTime() - start;
		
		//线程安全的ReentrantReadWriteLock/StampedLock可以确保最终ShareObj的value肯定和count相等
		if(shareObj.get() != dataAmount){
			throw new IllegalArgumentException("Maybe something wrongs...");
		}
		return time;
	}
	
	private static ShareObj getShareObj(LockType type){
		switch(type){
		case ReadWriteLock:
			return new ShareObjReadWriteLock();
		case StampedLock:
			return new ShareObjStampedLock();
		}
		throw new IllegalArgumentException("Unkonwn LockType = "+type);
	}
	
	enum LockType{
		ReadWriteLock, StampedLock
	}
	
	private static interface ShareObj{
		void set(int value);
		int get();
	}
	
	private static class ShareObjReadWriteLock implements ShareObj{
		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private int value;
		@Override
		public void set(int value){
			readWriteLock.writeLock().lock();
			try{
				this.value = value;
			}finally{
				readWriteLock.writeLock().unlock();
			}
		}
		@Override
		public int get(){
			readWriteLock.readLock().lock();
			try{
				return value;
			}finally{
				readWriteLock.readLock().unlock();
			}
		}
	}
	
	private static class ShareObjStampedLock implements ShareObj{
		private final StampedLock stampedLock = new StampedLock();
		private int value;
		@Override
		public void set(int value) {
			long stamp = stampedLock.writeLock();
			try{
				this.value = value;
			}finally{
				stampedLock.unlockWrite(stamp);
			}
		}

		@Override
		public int get() {
			long stamp = stampedLock.tryOptimisticRead();
			int currentValue = value;
			if(!stampedLock.validate(stamp)){
				stamp = stampedLock.readLock();
				try{
					currentValue = value;
				}finally{
					stampedLock.unlockRead(stamp);
				}
			}
			return currentValue;
		}
		
	}
	
	private static class ReadThread extends Thread{
		private final ShareObj shareObj;
		private final int count;
		public ReadThread(String name, ShareObj shareObj, int count){
			super(name);
			this.shareObj =shareObj;
			this.count = count;
		}
		@Override
		public void run() {
			while(true){
				if(shareObj.get() >= count){
					break;
				}
			}
		}
	}
	
	private static class WriteThread extends Thread{
		private final ShareObj shareObj;
		private final int count;
		public WriteThread(String name, ShareObj shareObj, int count){
			super(name);
			this.shareObj =shareObj;
			this.count = count;
		}
		@Override
		public void run() {
			for(int i=1;i<=count;i++){
				shareObj.set(i);
			}
		}
	}
	
}
