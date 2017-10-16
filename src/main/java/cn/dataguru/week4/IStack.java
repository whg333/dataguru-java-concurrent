package cn.dataguru.week4;

/**
 * 栈的接口，除了经典的push、pop、peek和isEmpty，
 * 新增了print打印栈内所有元素的方法
 * 
 * @author whg
 * 2015年7月25日 下午1:25:43
 */
public interface IStack<E> {
	void push(E e);
	E pop();
	E peek();
	boolean isEmpty();
	void print();
}
