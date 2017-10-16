package cn.dataguru.week4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 低性能的无锁Stack的实现，内部使用了一个List来存储栈元素，
 * 结合AtomicReference的CAS操作来实现无锁的push和pop，
 * 缺点是每次push和pop都需要new一个ArrayList来确保List引用的改变，
 * 如此会导致元素很多时拷贝的开销很大，类似CopyOnWrite的意思
 * 
 * @author whg
 * 2015年7月25日 下午1:14:15
 */
public class LockFreeStack<E> implements IStack<E> {

	private final AtomicReference<List<E>> listRef;
	
	public LockFreeStack(){
		listRef = new AtomicReference<List<E>>(new ArrayList<E>());
	}
	
	@Override
	public void push(E e) {
		while(true){
			List<E> oldList = listRef.get();
			List<E> newList = new ArrayList<E>(oldList);
			newList.add(e);
			if(listRef.compareAndSet(oldList, newList)){
				return;
			}
		}
	}

	@Override
	public E pop() {
		while(true){
			List<E> oldList = listRef.get();
			if(oldList.isEmpty()){
				throw new EmptyStackException();
			}
			List<E> newList = new ArrayList<E>(oldList);
			E removed = newList.remove(newList.size() - 1);
			if(listRef.compareAndSet(oldList, newList)){
				return removed;
			}
		}
	}

	@Override
	public E peek() {
		List<E> list = listRef.get();
		if(list.isEmpty()){
			throw new EmptyStackException();
		}
		return list.get(list.size() - 1);
	}
	
	@Override
	public boolean isEmpty() {
		return listRef.get().isEmpty();
	}

	@Override
	public void print() {
		List<E> list = listRef.get();
		if(list.isEmpty()){
			System.out.println("Stack is Empty!!!");
		}else{
			System.out.println(list.size());
			System.out.println(Arrays.toString(list.toArray(new Object[0])));
		}
	}

}
