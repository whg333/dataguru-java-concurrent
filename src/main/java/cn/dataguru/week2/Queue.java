package cn.dataguru.week2;

public class Queue {
	
	private final Product[] productLine;
	private int addIndex = 0;
	private int getIndex = 0;
	
	public Queue(int size){
		this.productLine = new Product[size];
	}
	
	public synchronized void add(Product product) throws InterruptedException{
		while(isFull()){
			wait();
		}
		
		productLine[addIndex] = product;
		System.out.println(Thread.currentThread().getName()+" add "+product+" to queue["+addIndex+"]");
		
		addIndex = (addIndex+1) % productLine.length;
		notifyAll();
	}
	
	private boolean isFull() {
		return productLine[addIndex] != null;
	}
	
	public synchronized Product get() throws InterruptedException{
		while(isEmpty()){
			wait();
		}
		
		Product product = productLine[getIndex];
		productLine[getIndex] = null;
		System.out.println(Thread.currentThread().getName()+" get "+product+" from queue["+getIndex+"]");
		
		getIndex = (getIndex+1) % productLine.length;
		notifyAll();
		return product;
	}

	private boolean isEmpty() {
		return productLine[getIndex] == null;
	}
	
}
