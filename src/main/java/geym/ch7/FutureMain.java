package geym.ch7;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FutureMain {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //����FutureTask
        FutureTask<String> future = new FutureTask<String>(new RealData("a"));
        ExecutorService executor = Executors.newFixedThreadPool(1);
        //ִ��FutureTask���൱�������е� client.request("a") ��������
        //�����￪���߳̽���RealData��call()ִ��
        Future<?> f = executor.submit(future);
        if(f == future){
        	System.out.println("f == future");
        }
        while(!f.isDone()){
        	
        }
        System.out.println(f.get());
        System.out.println(future.get());

        System.out.println("�������");
        try {
        //������Ȼ��������������ݲ���������ʹ��sleep��������ҵ���߼��Ĵ���
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        //�൱��data.getResult ()��ȡ��call()�����ķ���ֵ
        //�����ʱcall()����û��ִ����ɣ�����Ȼ��ȴ�
        System.out.println("���� = " + future.get());
    }
}
