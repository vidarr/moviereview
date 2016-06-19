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

package data;




/**
 * Verwaltet einen String mit einem zusaetzlichen Integerwert als Typspezifikation des Strings
 * @author mibeer
 *
 */
public class QualifiedString implements Cloneable {
	
	protected int typ = 0;
	protected String text = null;
	protected String[] types = null;

	public QualifiedString(int typ, String text, String[] types) {
		if (text == null || types == null || types.length < 1) {
			throw new IllegalArgumentException();
		}
		this.types = types;
		setText(text);
		setTyp(typ);
	}

	public QualifiedString(String text, String[] types) {
		this(0, text, types);		
	}

	public synchronized String getText() {
		return text;
	}

	public synchronized void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		synchronized(this) {
			this.text = text;
		}
	}

	public synchronized int getTyp() {
		return typ;
	}

	public synchronized void setTyp(int typ) {
		if (typ < 0 || typ > this.types.length - 1) {
			throw new IllegalArgumentException();
		}
		this.typ = typ;
	}

	public synchronized void setTyp(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < this.types.length; i++) {
			if (this.types[i].equals(str)) {
				setTyp(i);
				return;
			}
		}
		throw new IllegalArgumentException();
	}
	
	
	public String toString() {
		return text;
	}
	
	public QualifiedString clone() {
		//QualifiedString cp = new QualifiedString(getTyp(), new String(getText(), this.clazz));	
		QualifiedString cp = new QualifiedString(getTyp(), new String(getText()), this.types);
		return cp;
	}

	public  String[] getTypes() {
		return this.types;
	}
}

