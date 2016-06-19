/*
 * BMovieReviewer Copyright (C) 2009 Michael J. Beer
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package data.wrappers;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;


/**
 * Verwaltet eine Liste, die mit einer JList dargestellt werden kann
 * Synchronisiert
 * @author mibeer
 *
 * @param <T>
 */
public class ListWrapper<T> 
implements List<T>, ListModel {

	protected List<ListDataListener> listeners;
	protected List<T> list;

	public ListWrapper() {
		super();
		listeners = Collections.synchronizedList(new LinkedList<ListDataListener>());
		list = Collections.synchronizedList(new LinkedList<T>());
	}

	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public Object getElementAt(int index) {
		return get(index);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public int getSize() {
		return size();
	}

	public List<ListDataListener> getListeners() {
		return listeners;
	}

	@Override
	public boolean add(T e) {
		return list.add(e);
	}

	@Override
	public void add(int index, T element) {
		list.add(index, element);			
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return list.addAll(index, c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public T remove(int index) {
		return list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <C> C[] toArray(C[] a) {
		return list.toArray(a);
	}
}
