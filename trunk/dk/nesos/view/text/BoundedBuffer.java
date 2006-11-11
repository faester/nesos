package dk.nesos.view.text;

import java.util.*;

/**
 * Simple bounded buffer implementation
 *
 * @author ndhb
 */
public final class BoundedBuffer<T> {

	private List<T> buffer;
	private int capacity;

	public BoundedBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new ArrayList<T>(capacity);
	} // constructor

	public void add(T t) {
		if (t == null) {
			throw new RuntimeException("Attempt to invoke add(null)!");
		} // if invalid arguments
		if (buffer.size() >= capacity) {
			buffer.remove(0);
		} // if
		buffer.add(t);
	} // method

	public T get(int index) {
		return buffer.get(index);
	} // method

	public int getCapacity() {
		return capacity;
	} // method

	public int getSize() {
		return buffer.size();
	} // method

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.size(); i++) {
			sb.append(buffer.get(i).toString() + " ");
		} // for all elements in array
		return sb.toString();
	} // method

} // class
