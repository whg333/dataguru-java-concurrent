package cn.dataguru.week2;

public class Consumer extends Thread{
	private final Queue queue;
	public Consumer(String name, Queue queue){
		super(name);
		this.queue = queue;
	}
	@Override
	public void run() {
		try {
			while(true){
				Product product = queue.get();
				product.consume(); //模拟产品消耗所消耗的时间
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
