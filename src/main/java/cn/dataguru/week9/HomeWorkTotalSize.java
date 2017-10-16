package cn.dataguru.week9;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

public class HomeWorkTotalSize {

	private static final int GROUP_NUM = 10;
	private static final int TEST_COUNT = 10;
	private static final int TOTAL_SIZE = 1000000;
	
	public static void main(String[] args) throws InterruptedException {
		testBiasedLocking(1);
		testBiasedLocking(2);
		testBiasedLocking(3);
		testBiasedLocking(4);
		testBiasedLocking(5);
		testBiasedLocking(6);
		testBiasedLocking(7);
		testBiasedLocking(8);
		testBiasedLocking(9);
		testBiasedLocking(10);
		testBiasedLocking(15);
		testBiasedLocking(20);
		testBiasedLocking(30);
		
//		testBiasedLocking(40);
//		testBiasedLocking(50);
//		testBiasedLocking(80);
//		testBiasedLocking(100);
	}
	
	private static void testBiasedLocking(int threadNum) throws InterruptedException{
		testBiasedLocking(GROUP_NUM, TEST_COUNT, threadNum);
	}
	
	private static void testBiasedLocking(int group, int count, int threadNum) throws InterruptedException{
		long sum = 0;
		for(int i=0;i<group;i++){
			sum += testBiasedLockingGroup(i+1, count, threadNum);
		}
		System.out.printf("group=%d\tcount=%d\tthreadNum=%d\tavgTime=%.2f\tmillisecond\n", 
				group, count, threadNum, sum/group/1.0e6);
	}
	
	private static long testBiasedLockingGroup(int griupId, int count, int threadNum) throws InterruptedException{
		long sum = 0;
		for(int i=0;i<count;i++){
			sum += testBiasedLockingCount(griupId, i+1, threadNum);
		}
		long avgTime = sum/count;
		//System.out.printf("group-%d test count is %d, avg time taken %d nanosecond\n", griupId, count, avgTime);
		return avgTime;
	}
	
	private static long testBiasedLockingCount(int groupId, int countId, int threadNum) throws InterruptedException{
		List<Integer> vector = new Vector<Integer>(TOTAL_SIZE); //直接分配好totalSize个空间避免Vector的自动扩容影响测试时间
		List<Thread> threads = new ArrayList<Thread>();
		AtomicLong addNum = new AtomicLong(TOTAL_SIZE);
		
		long start = System.nanoTime();
		for(int i=0;i<threadNum;i++){
			threads.add(new VectorThread(groupId+"-"+countId+"-VectorThread-"+(i+1)+"-", vector, addNum));
		}
		for(Thread thread:threads){
			thread.start();
		}
		for(Thread thread:threads){
			thread.join();
		}
		long time = System.nanoTime() - start;
		
		//System.out.printf("group-%d-%d Time taken: %d nanosecond\n", groupId, countId, time);
		if(vector.size() < TOTAL_SIZE){ //线程安全的Vector可以确保最终Vector的size肯定和totalSize相等
			throw new IllegalArgumentException("Maybe something wrongs...");
		}
		return time;
	}
	
	private static class VectorThread extends Thread{
		private final List<Integer> vector;
		private final AtomicLong addNum;
		private VectorThread(String name, List<Integer> vector, AtomicLong addNum){
			super(name);
			this.vector = vector;
			this.addNum = addNum;
		}
		@Override
		public void run() {
			while(addNum.get() > 0){
				vector.add((int)addNum.get());
				addNum.decrementAndGet();
			}
		}
	}
	
}
