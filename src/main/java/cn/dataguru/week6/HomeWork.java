package cn.dataguru.week6;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeWork {
	
	enum CachedThreadPoolType{
		JdkCachedThreadPool {
			@Override
			ExecutorService cachedThreadPool() {
				return Executors.newCachedThreadPool();
			}
		},
		MyCachedThreadPool {
			@Override
			ExecutorService cachedThreadPool() {
				return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
		                60L, TimeUnit.SECONDS,
		                new SynchronousQueue<Runnable>()){
					@Override
					protected void afterExecute(Runnable r, Throwable t) {
						//如果t不为null则代表调用execute且执行的是Runnable的run方法
						//否则t为null则代表调用submit且执行的是FutureTask的run方法，
						//且异常被FutureTask的setException吃掉
						if(t != null) return;
						try {
							((Future<?>) r).get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}catch (ExecutionException e) { 
							//吃掉的异常被封装在ExecutionException里面，
							//所以我们使用getCause来获取最初的Exception来源
							e.getCause().printStackTrace();
						}
					}
				};
			}
		};
		abstract ExecutorService cachedThreadPool();
	}

	public static void main(String[] args){
		testSubmitCatchException(CachedThreadPoolType.JdkCachedThreadPool);
		testSubmitCatchException(CachedThreadPoolType.MyCachedThreadPool);
	}
	
	private static void testSubmitCatchException(CachedThreadPoolType type){
		System.err.println("start test "+type);
		ExecutorService executor = type.cachedThreadPool();
		//executor.execute(new NullPointerExceptionThread(type.toString(), 1));
		//executor.submit(new NullPointerExceptionThread(type.toString(), 2));
		executor.execute(new NullPointerExceptionThread(type.toString(), 1));
		executor.submit(new NullPointerExceptionThread(type.toString(), 2));
		executor.shutdown();
		try {
			executor.awaitTermination(4, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		System.err.println(type+" isTerminated="+executor.isTerminated());
		System.err.println("finished test "+type+"\n");
	}
	
	private static class NullPointerExceptionThread extends Thread{
		private final int delay;
		private NullPointerExceptionThread(String name, int delay){
			super(name+"-NPEThread-"+delay);
			this.delay = delay;
		}
		@Override
		public void run() {
			try {
				new NullPointerExceptionCallable(delay).call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static class NullPointerExceptionCallable implements Callable<String>{
		private final int delay;
		private NullPointerExceptionCallable(int delay){
			this.delay = delay;
		}
		@Override
		public String call() throws Exception {
			try {
				TimeUnit.SECONDS.sleep(delay);
			} catch (InterruptedException e) {
			}
			System.err.println(Thread.currentThread().getName()+" next line throw NullPointerException...");
			throw new NullPointerException(Thread.currentThread().getName());
		}
	}
	
}
