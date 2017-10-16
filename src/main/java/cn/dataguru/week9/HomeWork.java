package cn.dataguru.week9;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HomeWork {

	private static final int GROUP_NUM = 10;
	private static final int TEST_COUNT = 10;
	private static final int LOOP_NUM = 100000;
	
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
		testBiasedLocking(GROUP_NUM, TEST_COUNT, LOOP_NUM, threadNum);
	}
	
	private static void testBiasedLocking(int group, int count, int loopNum, int threadNum) throws InterruptedException{
		long sum = 0;
		for(int i=0;i<group;i++){
			sum += testBiasedLockingGroup(i+1, count, loopNum, threadNum);
		}
		System.out.printf("group=%d\tcount=%d\tloopNum=%d\tthreadNum=%d\tavgTime=%.2f\tmillisecond\n", 
				group, count, loopNum, threadNum, sum/group/1.0e6);
	}
	
	private static long testBiasedLockingGroup(int griupId, int count, int loopNum, int threadNum) throws InterruptedException{
		long sum = 0;
		for(int i=0;i<count;i++){
			sum += testBiasedLockingCount(griupId, i+1, loopNum, threadNum);
		}
		long avgTime = sum/count;
		//System.out.printf("group-%d test count is %d, avg time taken %d nanosecond\n", griupId, count, avgTime);
		return avgTime;
	}
	
	private static long testBiasedLockingCount(int groupId, int countId, int loopNum, int threadNum) throws InterruptedException{
		int totalSize = threadNum * loopNum;
		List<Integer> vector = new Vector<Integer>(totalSize); //直接分配好totalSize个空间避免Vector的自动扩容影响测试时间
		List<Thread> threads = new ArrayList<Thread>();
		
		long start = System.nanoTime();
		for(int i=0;i<threadNum;i++){
			threads.add(new VectorThread(groupId+"-"+countId+"-VectorThread-"+(i+1)+"-", vector, loopNum));
		}
		for(Thread thread:threads){
			thread.start();
		}
		for(Thread thread:threads){
			thread.join();
		}
		long time = System.nanoTime() - start;
		
		//System.out.printf("group-%d-%d Time taken: %d nanosecond\n", groupId, countId, time);
		if(vector.size() != totalSize){ //线程安全的Vector可以确保最终Vector的size肯定和totalSize相等
			throw new IllegalArgumentException("Maybe something wrongs...");
		}
		return time;
	}
	
	private static class VectorThread extends Thread{
		private final List<Integer> vector;
		private final int loopNum;
		private VectorThread(String name, List<Integer> vector, int loopNum){
			super(name);
			this.vector = vector;
			this.loopNum = loopNum;
		}
		@Override
		public void run() {
			for(int i=0;i<loopNum;i++){
				vector.add(i);
			}
		}
	}
	
}
