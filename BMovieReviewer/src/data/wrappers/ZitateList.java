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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import data.Link;

/**
 * Verwaltet eine Liste von Zitaten zur Anzeige mittels einer JList
 * Synchronisiert
 * @author mibeer
 * 
 */
public class ZitateList extends ListWrapper<String> implements TableModel {

	List<TableModelListener> listeners;

	public ZitateList() {
		listeners = Collections.synchronizedList(new LinkedList<TableModelListener>());
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 1;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Zitate";
		default:
			throw new IllegalArgumentException();
		}
	}

	public int getRowCount() {
		return size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= size() || columnIndex > 0) {
			throw new IllegalArgumentException();
		}
		return get(rowIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			String str;
			if (aValue == null) {
				str = "";
			} else {
				// In String verwandeln und "falsche Anfuehrungszeichen
				// rausnehmen
				str = aValue.toString().replaceAll("„", "\"").replaceAll("“",
						"\"");
			}
			set(rowIndex, str);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public synchronized boolean add(String l) {
		contentChanged();
		return super.add(l);
	}

	public synchronized void remove(Link l) {
		super.remove(l);
		contentChanged();
	}

	public void contentChanged() {
			Iterator<TableModelListener> it = listeners.iterator();
			TableModelEvent ev = new TableModelEvent(this);
			while (it.hasNext()) {
				it.next().tableChanged(ev);
			}
	}

	public List<TableModelListener> getTableListeners() {
		return listeners;
	}
}
