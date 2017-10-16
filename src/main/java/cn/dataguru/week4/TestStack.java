package cn.dataguru.week4;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class TestStack {
	
	private static final int TEST_TIMES = 10;
	private static final int THREAD_NUM = 100;
	private static final int LOOP_TIMES = 1000;

	public static void main(String[] args) {
		for(int i=0;i<TEST_TIMES;i++){
			testStack();
		}
	}
	
	private static IStack<String> getStack(){
		//return new LockFreeStack<String>();
		//return new ConcurrentStack<String>();
		return new JdkStack<String>();
		//return new ListStack<String>();
	}
	
	private static void testStack(){
		IStack<String> stack = getStack();
		List<Thread> runThreads = new ArrayList<Thread>();
		for(int i=0;i<THREAD_NUM;i++){
			PushThread pushThread = new PushThread("PushThread-"+(i+1), stack);
			pushThread.start();
			runThreads.add(pushThread);
			
			PopThread popThread = new PopThread("PopThread-"+(i+1), stack);
			popThread.start();
			runThreads.add(popThread);
		}
		
		try {
			for(Thread thread:runThreads){
				thread.join();
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		
		stack.print();
	}
	
	private static final class PushThread extends Thread{
		private final IStack<String> stack;
		private PushThread(String name, IStack<String> stack){
			super(name);
			this.stack = stack;
		}
		@Override
		public void run() {
			for(int i=0;i<LOOP_TIMES;i++){
				stack.push("{");
			}
		}
	}
	
	private static final class PopThread extends Thread{
		private final IStack<String> stack;
		private PopThread(String name, IStack<String> stack){
			super(name);
			this.stack = stack;
		}
		@Override
		public void run() {
			for(int i=0;i<LOOP_TIMES;i++){
				boolean isDone = false;
				while(!isDone){
					try{
						stack.pop();
						isDone = true;
					}catch(EmptyStackException e){
						//使用while循环是为了捕获EmptyStackException异常确保pop操作成功
					}
				}
			}
		}
	}
	
}
