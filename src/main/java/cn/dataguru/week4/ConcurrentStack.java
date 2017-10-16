package cn.dataguru.week4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 高性能的无锁Stack的实现，内部使用了类似链表般的节点Node结构来存储元素，
 * 即把栈看作是栈顶为根节点的链表，每个Node节点都包含一个值（item）和指向下一个（next）节点的引用，
 * 如此一来的push和pop的实现，可以使用AtomicReference的CAS仅仅针对栈顶（根节点）操作即可，
 * 由于仅仅对栈顶（根节点）引用的CAS修改，避免了像LockFreeStack的大量数据拷贝，所以性能得到了提升
 * 
 * @author whg
 * 2015年7月25日 下午1:19:14
 */
public class ConcurrentStack<E> implements IStack<E> {
	
	private final AtomicReference<Node<E>> topRef;
	
	private static final class Node<E>{
		final E item;
		Node<E> next;
		Node(E item){
			this.item = item;
		}
	}
	
	public ConcurrentStack(){
		topRef = new AtomicReference<Node<E>>();
	}

	@Override
	public void push(E e) {
		while(true){
			Node<E> oldTop = topRef.get();
			Node<E> newTop = new Node<E>(e);
			newTop.next = oldTop;
			if(topRef.compareAndSet(oldTop, newTop)){
				return;
			}
		}
	}

	@Override
	public E pop() {
		while(true){
			Node<E> oldTop = topRef.get();
			if(oldTop == null){
				throw new EmptyStackException();
			}
			Node<E>  newTop = oldTop.next;
			if(topRef.compareAndSet(oldTop, newTop)){
				return oldTop.item;
			}
		}
	}

	@Override
	public E peek() {
		return topRef.get().item;
	}

	@Override
	public boolean isEmpty() {
		return topRef.get() == null;
	}

	@Override
	public void print() {
		Node<E> top = topRef.get();
		if(top == null){
			System.out.println("Stack is Empty!!!");
		}else{
			List<E> list = new ArrayList<E>();
			list.add(top.item);
			while((top = top.next) != null){
				list.add(top.item);
			}
			System.out.println(list.size());
			System.out.println(Arrays.toString(list.toArray(new Object[0])));
		}
	}

}
