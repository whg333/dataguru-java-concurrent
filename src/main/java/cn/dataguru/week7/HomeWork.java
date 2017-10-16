package cn.dataguru.week7;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

public class HomeWork {

	private static final double MIN = 1;	 	//x最小值
	private static final double MAX = 100;	 	//x最大值
	
	/** 步长，可调整步长精确至0.0000001令计算结果接近答案ln(100)的值 */
	private static final double STEP = 0.01;
	
	private static final double THERSHOLD = 0.1;//ForkJoin阀值
	private static final int SEPARATOR = 10;	//ForkJoin拆分任务数
	
	private static final boolean calculateByTrapezoid = true;
	private static final boolean calculateByRectangle = !calculateByTrapezoid;
	
	public static void main(String[] args) {
		printCorrectAnswer();
		printSingleThread();
		printMultiThread();
	}
	
	private static void printCorrectAnswer(){
		System.out.println("【Correct Answer】 ln(100)-ln(1) = ln(100) = 2*ln(10) = "+2*ln(10)+"\n");
	}
	
	private static long printSingleThread(){
		long begin = System.nanoTime();
		AreaCalculator areaCalculator = new AreaCalculator(MIN, MAX);
		areaCalculator.calculate();
		long time =System.nanoTime() - begin;
		System.out.println("SingleThread sum: "+round7(areaCalculator.getSum())+", Time taken: "+time+" nanoseconds");
		System.out.println("SingleThread calculate rectangleArea count: "+areaCalculator.getCount()+"\n");
		return time;
	}

	private static class AreaCalculator{
		private final double min;
		private final double max;
		private double sum;
		private int count; //计算矩形面积的次数
		public AreaCalculator(double min, double max) {
			this.min = min;
			this.max = max;
		}
		public void calculate(){
			double unitNum = Math.round((max-min)/STEP);
			for(double x=min+(calculateByTrapezoid?0:STEP);count<unitNum;x+=STEP,count++){
				sum += calculateArea(x);
			}
		}
		private double calculateArea(double x){
			if(calculateByTrapezoid){
				double y1 = 1.0 / x;
				double y2 = 1.0 / (x+STEP);
				double trapezoidArea = STEP * (y1+y2) / 2;
				return trapezoidArea;
			}else{
				double y = 1.0 / x;
				double rectangleArea = STEP * y;
				return rectangleArea;
				
				//近似值无限接近的话可以不考虑直角三角形的面积
				//double rightAngledTriangleArea = STEP*y/2.0;
				//sum += rightAngledTriangleArea;
				//System.out.println("x="+x+", i="+i);
				//System.out.printf("%.2f * %.20f = %.20f, i=%.0f\n", STEP, y, area, i);
			}
		}
		public double getSum() {
			return sum;
		}
		public int getCount() {
			return count;
		}
	}
	
	private static long printMultiThread() {
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
        System.out.println("MultiThread sum: "+round7(sum)+", Time taken: "+time+" nanoseconds");
        System.out.println("MultiThread calculate rectangleArea count: "+AreaSumTask.sumCount.get()+"\n");
        return time;
	}
	
	@SuppressWarnings("serial")
	private static class AreaSumTask extends RecursiveTask<Double>{

		/** 汇总多线程内计算矩形面积的次数 */
		private static final AtomicLong sumCount = new AtomicLong();
		
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
				AreaCalculator areaCalculator = new AreaCalculator(begin, end);
				areaCalculator.calculate();
				sum = areaCalculator.getSum();
				sumCount.addAndGet(areaCalculator.getCount());
				//System.out.println("compute "+this+", sum="+sum+", count="+areaCalculator.getCount()+"\n");
			}else{
	            ArrayList<AreaSumTask> subTasks = new ArrayList<AreaSumTask>();
	            double subBegin = begin;
	            for(int i=0;i<SEPARATOR;i++){
	                double subEnd = subBegin+subStep;
	                subEnd = subEnd > end ? end : subEnd;
	                subBegin = i==0 ?  subBegin : subBegin/*+STEP*/;
	                AreaSumTask subTask = new AreaSumTask(subBegin, subEnd);
	                //System.out.println(subTask);
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
		
		@Override
		public String toString() {
			return "AreaSumTask [begin=" + begin + ", end=" + end 
					+ ", diff=" + diff + ", subStep=" + subStep 
					+ ", canCompute=" + canCompute + "]";
		}
		
	}
	
	private static double round2(double d){
		return roundDig(d, 2);
	}
	
	private static double round7(double d){
		return d;
		//return roundDig(d, 7);
	}
	
	/** 由于double计算结果不精确，所以需要适当取舍精度 */
	private static double roundDig(double d, int s){
		return Math.round(d*Math.pow(10, s))/(Math.pow(10, s));
	}
	
	public static double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
	
	public static double log2(double value) {
		return log(value, 2.0);
	}

	public static double log10(double value) {
		return log(value, 10.0);
	}
	
	public static double ln(double value){
		return Math.log(value);
		//return log(value, Math.E);
	}
}