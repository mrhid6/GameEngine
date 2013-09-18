package com.mrhid6.gameengine.utils;

import java.util.ArrayList;

public class Node {
	private ArrayList<Node> children = new ArrayList<Node>();

	private Node parent;

	public ArrayList<Node> getChildren() {
		return children;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int attachChild(Node child)
	{
		if (child == null) {
			throw new NullPointerException();
		}
		if ((child.getParent() != this) && (child != this)) {
			if (child.getParent() != null) {
				child.getParent().detachChild(child);
			}
			child.setParent(this);
			this.children.add(child);

		}

		return this.children.size();
	}

	public int detachChild(Node child)
	{
		if (child == null) {
			throw new NullPointerException();
		}
		if (child.getParent() == this) {
			int index = this.children.indexOf(child);
			if (index != -1) {
				detachChildAt(index);
			}
			return index;
		}

		return -1;
	}

	public Node detachChildAt(int index)
	{
		Node child = (Node)this.children.remove(index);
		if (child != null) {
			child.setParent(null);
		}
		return child;
	}

	public Node getParent() {
		return parent;
	}
}
