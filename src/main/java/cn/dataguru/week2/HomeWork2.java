package cn.dataguru.week2;

public class HomeWork2 {
	
	public static void main(String[] args) {
		Queue queue = new Queue(5);
		for(int i=1;i<=3;i++){
			new Producer("Producer-"+i, queue).start();
		}
		for(int i=1;i<=3;i++){
			new Consumer("Consumer-"+i, queue).start();
		}
	}
	
}
