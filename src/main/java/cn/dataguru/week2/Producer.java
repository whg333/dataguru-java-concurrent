package cn.dataguru.week2;

public class Producer extends Thread{
	private static int idSeq;
	private final Queue queue;
	public Producer(String name, Queue queue){
		super(name);
		this.queue = queue;
	}
	@Override
	public void run() {
		try {
			while(true){
				Product product = new Product();
				product.produce(nextId());	//模拟产品生产所消耗的时间
				queue.add(product);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private static synchronized int nextId(){
		return ++idSeq;
	}
}
