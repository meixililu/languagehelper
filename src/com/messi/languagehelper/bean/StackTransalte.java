package com.messi.languagehelper.bean;

import java.util.LinkedList;

public class StackTransalte {

	private LinkedList list = new LinkedList();

	public void push(Object v) {
		list.addFirst(v);
	}

	public Object top() {
		return list.getFirst();
	}

	public Object pop() {
		return list.removeFirst();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
