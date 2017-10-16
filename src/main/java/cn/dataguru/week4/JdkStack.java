package cn.dataguru.week4;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * 使用JDK API的Stack来适配IStack接口，
 * 因为JDK的Stack使用Synchronize来确保线程安全，
 * 故本JdkStack也是线程安全的，只是性能不如无锁的好
 * 
 * @author whg
 * 2015年7月25日 下午1:27:03
 */
public class JdkStack<E> implements IStack<E> {
	
	private final Stack<E> stack;
	
	public JdkStack(){
		stack = new Stack<E>();
	}

	@Override
	public void push(E e) {
		stack.push(e);
	}

	@Override
	public E pop() {
		if (isEmpty()) {
			throw new EmptyStackException();
		}
		return stack.pop();
	}

	@Override
	public E peek() {
		return stack.peek();
	}

	@Override
	public boolean isEmpty() {
		return stack.empty();
	}

	@Override
	public void print() {
		if(isEmpty()){
			System.out.println("Stack is Empty!!!");
		}else{
			System.out.println(stack.size());
			System.out.println(Arrays.toString(stack.toArray(new Object[0])));
		}
	}

}
