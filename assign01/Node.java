package assign01;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {

	private Node right;
	private Node left;
	private List<Byte> listByte = new ArrayList<>(2);
	private int freq;

	public Node(Node left, Node right, List<Byte> listByte, int freq) {
		this.left = left;
		this.right = right;
		this.listByte = listByte;
		this.freq = freq;
	}

	public int getFreq() {
		return freq;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

	public List<Byte> getListByte() {
		return listByte;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public void setListByte(List<Byte> listByte) {
		this.listByte = listByte;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public int compareTo(Node nodeOther) {
		if (Integer.compare(this.getFreq(), nodeOther.getFreq()) == 0) {
			return compareByte(nodeOther);
		}
		return Integer.compare(this.getFreq(), nodeOther.getFreq());

	}

	private int compareByte(Node Other) {

		byte leftThis = this.getListByte().get(0);
		byte rightThis = this.getListByte().get(1);

		byte leftOther = Other.getListByte().get(0);
		byte rightOther = Other.getListByte().get(1);

		if (Byte.compare(leftThis, leftOther) == 0) {
			return Byte.compare(rightThis, rightOther);
		}
		return Byte.compare(leftThis, leftOther);

	}

	public boolean isLeaf() {

		if (this.left == null && this.right == null)
			return true;

		return false;
	}
}
