package cn.dataguru.week4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;

/**
 * 非线程安全的Stack实现，和LockFreeStack类似的是内部使用了一个List来存储栈元素，
 * 但由于没有使用AtomicReference的CAS或者Synchronize或者锁来确保线程安全，
 * 所以此类仅仅适合在只有单线程访问的情况下使用
 * 
 * @author whg
 * 2015年7月25日 下午1:29:17
 */
public class ListStack<E> implements IStack<E> {
	
	private final List<E> list;
	
	public ListStack() {
		list = new ArrayList<E>();
	}

	@Override
	public void push(E item) {
		while(true){
			try {
				list.add(item);
				return;
			} catch (IndexOutOfBoundsException e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public E pop() {
		while(true){
			try {
				if(isEmpty()) {
					throw new EmptyStackException();
				}
				return list.remove(list.size() - 1);
			} catch (IndexOutOfBoundsException e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public E peek() {
		if(isEmpty()){
			throw new EmptyStackException();
		}
		return list.get(list.size() - 1);
	}
	
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void print() {
		if(isEmpty()){
			System.out.println("Stack is Empty!!!");
		}else{
			System.out.println(list.size());
			System.out.println(Arrays.toString(list.toArray(new Object[0])));
		}
	}

}
