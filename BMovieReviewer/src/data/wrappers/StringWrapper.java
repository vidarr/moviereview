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
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;


/**
 * Verwaltet einen String Ersetzt dabei Zeilenumbrueche durch Leerzeichen
 * Synchronisiert
 * @author mibeer
 * 
 */
public class StringWrapper extends  PlainDocument {

	private static final long serialVersionUID = 1L;

	protected int maxLen = 0;

	public final static String EMPTY_STRING = "";

	public StringWrapper(String s) {
		super(new StringContent());
		setText(s);
	}

	public StringWrapper(String s, int m) {
		super(new StringContent());
		setText(s);
		setMaxLen(m);
	}

	public synchronized String getText() {
		String eintrag;
		try {
			eintrag = getText(0, getLength());
		} catch (BadLocationException ex) {
			throw new RuntimeException();
		}
		return eintrag;
	}

	public synchronized void setText(String s) {
		if (s == null) {
			throw new IllegalArgumentException();
		}
		if (maxLen > 0 && s.length() > maxLen) {
			return;
		}
		try {
			remove(0, this.getLength());
			insertString(0, s.replaceAll("[\r\n]", " ").replaceAll("„", "\"")
					.replaceAll("“", "\""), null);
		} catch (BadLocationException e) {
			throw new RuntimeException();
		}
	}

	public String toString() {
		return getText();
	}

	public synchronized void setMaxLen(int m) {
		if (m < 0) {
			throw new IllegalArgumentException();
		}
		maxLen = m;
	}

	public synchronized void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if (maxLen > 0 && offs + str.length() > maxLen) {
			return;
		}
		
		super.insertString(offs, str.replaceAll("[\r\n]", " ").replaceAll("„",
				"\"").replaceAll("“", "\""), a);
	}

}