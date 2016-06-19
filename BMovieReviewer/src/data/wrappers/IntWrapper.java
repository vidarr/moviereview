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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Verwaltet einen String, der sich zu einem int evaluieren laesst
 * Synchronisiert
 * 
 * @author mibeer
 * 
 */
public class IntWrapper extends StringWrapper {

	private static final long serialVersionUID = 1L;

	public IntWrapper(int i) {
		super(Integer.toString(i));
	}

	public IntWrapper(int i, int m) {
		super(Integer.toString(i), m);
	}

	public IntWrapper(String s) {
		super(s);
		// int i;
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
	}

	// super.setText() ist synchronisiert...
	public void setText(String s) {
		// int i;
		try {
			Integer.parseInt(s);
			super.setText(s);
		} catch (NumberFormatException e) {
			return;
		}
	}

	public synchronized int getInt() {
		return Integer.parseInt(getText());
	}

	public synchronized void setInt(int i) {
		setText(Integer.toString(i));
	}

	public synchronized void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return;
		}
		super.insertString(offs, str, a);
	}
}
