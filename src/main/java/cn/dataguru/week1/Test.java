package cn.dataguru.week1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Exchanger;

public class Test {

	public static void main(String[] args) {
		testExchanger();
	}
	
	private static void testExchanger(){
		final Exchanger<List<Integer>> ex = new Exchanger<List<Integer>>();
		
		new Thread(){
			@Override
			public void run() {
				List<Integer> l = new ArrayList<Integer>();
				l.add(1);
				l.add(2);
				try {
					l = ex.exchange(l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Thread1:"+Arrays.toString(l.toArray(new Integer[0])));
			};
		}.start();
		
		new Thread(){
			@Override
			public void run() {
				List<Integer> l = new ArrayList<Integer>();
				l.add(4);
				l.add(5);
				try {
					l = ex.exchange(l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Thread2:"+Arrays.toString(l.toArray(new Integer[0])));
			};
		}.start();
	}
	
}
