package cn.dataguru.week7;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

public class AreaTask extends RecursiveTask<BigDecimal> {
    private static final BigDecimal THRESHOLD = new BigDecimal(0.01);
    private BigDecimal start;
    private BigDecimal end;
    private static final AtomicLong count = new AtomicLong();

    public AreaTask(BigDecimal start, BigDecimal end) {
        this.start = start;
        this.end = end;
    }

    public BigDecimal compute() {
        BigDecimal area = new BigDecimal(0);
        boolean canCompute = end.subtract(start).compareTo(THRESHOLD) <= 0;
        if (canCompute) {
        	System.out.println("canCompute start="+start+", end="+end);
            //近似为梯形来计算面积
            area = (new BigDecimal(1))
            		.divide(end, 32, RoundingMode.HALF_EVEN)
                    .add((new BigDecimal(1)).divide(start, 32, RoundingMode.HALF_EVEN))
                    .multiply(end.subtract(start))
                    .divide(new BigDecimal(2), 32, RoundingMode.HALF_EVEN);
        } else {
        	//System.out.println("start="+start+", end="+end);
            //分成50个小任务
            BigDecimal step = end.subtract(start).divide(new BigDecimal(50), 32, RoundingMode.HALF_EVEN);
            ArrayList<AreaTask> subTasks = new ArrayList<AreaTask>();
            BigDecimal pos = start;
            for (int i = 0; i < 50; i++) {
                BigDecimal lastOne = pos.add(step);
                if (lastOne.compareTo(end) > 0) lastOne = end;
                AreaTask subTask = new AreaTask(pos, lastOne);
                count.incrementAndGet();
                subTasks.add(subTask);
                subTask.fork();
                pos = pos.add(step);
            }
            for (AreaTask t : subTasks) {
                area = area.add(t.join());
            }
        }
        return area;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AreaTask task = new AreaTask(new BigDecimal(1), new BigDecimal(100));
        ForkJoinTask<BigDecimal> result = forkJoinPool.submit(task);
        try {
            BigDecimal res = result.get();
            System.out.println("area=" + res.doubleValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(count.get());
    }
}
