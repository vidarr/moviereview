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

import data.QualifiedString;

/**
 * Verwaltet eine Liste von HTTP-Links zur Anzeige in einer JList
 * Synchronisiert
 * @author mibeer
 * 
 */
public class QualifiedStringList extends ListWrapper<QualifiedString> implements TableModel {

	List<TableModelListener> listeners;
	String name = null;

	
	public QualifiedStringList(String name) {
		if(name == null) {
			throw new IllegalArgumentException();
		}
		listeners = Collections.synchronizedList(new LinkedList<TableModelListener>());
		this.name = name;
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return QualifiedString.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Typ";
		case 1:
			return this.name;
		default:
			throw new IllegalArgumentException();
		}
	}

	public int getRowCount() {
		return size();
	}

	public synchronized Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= size() || columnIndex > 1) {
			throw new IllegalArgumentException();
		}
		QualifiedString str = get(rowIndex);
		if(str == null) {
			return null;
		}
		return (columnIndex == 0) ? str.getTypes()[str.getTyp()] : str
				.getText();
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
			try {
				get(rowIndex).setTyp(aValue.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}
			break;
		case 1:
			String str;
			if (aValue == null) {
				str = "";
			} else {
				str = aValue.toString();
			}
			synchronized(this) {
				get(rowIndex).setText(str);
			}
			break;
		default:
			throw new IllegalArgumentException();
		}
		if (columnIndex > 1) {
			throw new IllegalArgumentException();
		}

	}

	public synchronized boolean add(QualifiedString l) {
		contentChanged();
		return super.add(l);
	}

	public synchronized void remove(QualifiedString l) {
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
