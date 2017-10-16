package cn.dataguru.week2;

public class Product{
	
	private int id;
	
	public Product(){
	
	}
	
	@Override
	public String toString() {
		return "Product[id=" + id + "]";
	}
	
	public void produce(int id){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.id = id;
	}
	
	public void consume(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
