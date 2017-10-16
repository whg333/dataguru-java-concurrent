package cn.dataguru.week7;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


public class HomeWorkWithBigDecimal {

	private static final double MIN = 1;
	//private static final double MAX = 1000000;
	private static final double MAX = 10000;
	private static final double AccuracyDiff = 1/Math.pow(10, Math.log10(MAX)+4);
	private static final double STEP = 0.01;
	
	private static final double THERSHOLD = 0.1;
	private static final int SEPARATOR = 10;
	
	private static final boolean useBigDecimal = false;
	
	public static void main(String[] args) {
		System.out.println("sum: "+((MAX-MIN)/STEP+1)+" (ANSWER)");
		long singleThreadTime = testSingleThread();
		long multiThreadTime = testMultiThread();
		System.out.println("timeDiff: "+(singleThreadTime-multiThreadTime));
		
		//System.out.println(79.43888888888957-79.3388888888896);
		//System.out.println((79.43888888888957-79.3388888888896)/10);
		//System.out.println(22.998200000000004%10);
		//System.out.println(Math.round(0.10789099999999988*100)/100.0);
		//System.out.println(1/Math.pow(10, Math.log10(MAX)+4));
		
//		System.out.println(1/1.01);
//		System.out.println(1.01*1/1.01);
//		System.out.println(div(1, 1.01, 5));
//		System.out.println(mul(1.01, div(1, 1.01, 5)));
	}

	private static long testSingleThread() {
		long begin = System.nanoTime();
		double sum = calculateArea(MIN, MAX);
		long time =System.nanoTime() - begin;
		System.out.println("sum: "+sum+", Time taken: "+time);
		return time;
	}
	
	private static double calculateArea(double min, double max){
		double sum = 0;
		for(double x=min;x<max+STEP-0.000001;x+=STEP){
			if(useBigDecimal){
				double y = BigDecimalUtil.div(1.0, x, 20);
				double area = BigDecimalUtil.mul(x, y);
				sum = BigDecimalUtil.add(sum, area);
			}else{
				double y = 1.0 / x;
				double area = x * y;
				sum += area;
			}
		}
		return sum;
	}
	
	private static long testMultiThread() {
		long begin = System.nanoTime();
		double sum = 0;
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		AreaSumTask task = new AreaSumTask(MIN, MAX);
		Future<Double> futrue = forkJoinPool.submit(task);
        try{
            sum = futrue.get();
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        long time =System.nanoTime() - begin;
        System.out.println("sum: "+sum+", Time taken: "+time);
        return time;
	}
	
	@SuppressWarnings("serial")
	private static class AreaSumTask extends RecursiveTask<Double>{

		private final double begin;
		private final double end;
		private final double diff;
		private final double subStep;
		private final boolean canCompute;
		
		private AreaSumTask(double begin, double end){
			this.begin =round2(begin);
			this.end = round2(end);
			this.diff = round2(end - begin);
			if(this.begin < 0 || this.end < 0 || diff <= 0){
				throw new IllegalArgumentException("Why???");
			}
			this.subStep = round2(diff / SEPARATOR);
			this.canCompute = subStep <= THERSHOLD;
		}
		
		@Override
		protected Double compute() {
			double sum = 0;
			if(canCompute){
				sum = calculateArea(begin, end);
			}else{
	            ArrayList<AreaSumTask> subTasks = new ArrayList<AreaSumTask>();
	            double subBegin = begin;
	            for(int i=0;i<SEPARATOR;i++){
	                double subEnd = subBegin+subStep;
	                subEnd = subEnd > end ? end : subEnd;
	                subBegin = i==0 ?  subBegin : subBegin+STEP;
	                AreaSumTask subTask = new AreaSumTask(subBegin, subEnd);
	                subBegin = subEnd;
	                subTasks.add(subTask);
	                subTask.fork();
	            }
	            for(AreaSumTask task:subTasks){
	                sum += task.join();
	            }
	            //System.out.println(this+", sum="+sum+"\n");
			}
			return sum;
		}
		
		private double round2(double d){
			if(useBigDecimal){
				return BigDecimalUtil.round(d, 2, BigDecimal.ROUND_DOWN);
			}
			return Math.round(d*100)/100.0;
		}

		@Override
		public String toString() {
			return "AreaSumTask [begin=" + begin + ", end=" + end 
					+ ", diff=" + diff + ", subStep=" + subStep 
					+ ", canCompute=" + canCompute + "]";
		}
		
	}
	
}
