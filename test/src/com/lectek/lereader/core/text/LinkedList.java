package com.lectek.lereader.core.text;


public class LinkedList<E> extends java.util.LinkedList<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4866846189914949888L;
	
	public E peekFirst() {
        return peek();
    }
	
	public E peekLast() {
        return (size() == 0) ? null : getLast();
    }
	
	public E pollFirst() {
        return (size() == 0) ? null : removeFirst();
    }
	
	public E pollLast() {
        return (size() == 0) ? null : removeLast();
    }
}
