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

import javax.swing.text.BadLocationException;


/** 
 * Verwaltet einen String
 * Endet dieser nicht auf ein Satzendezeichen, wird ein Punkt "." angefuegt
 * Synchronisiert
 * @author mibeer
 *
 */
public class TextWrapper extends StringWrapper {

	private static final long serialVersionUID = 1L;

	public TextWrapper(String s) {
		super(s);
	}

	public TextWrapper(String s, int mlen) {
		super(s, mlen);
	}

	public synchronized String getText() {
		String eintrag;
		try {
			eintrag = getText(0, getLength());
		} catch (BadLocationException ex) {
			throw new RuntimeException();
		}

		eintrag = eintrag.trim();
		eintrag = eintrag.replaceAll("<br>", "<br/>").replaceAll("<li>", "<li/>");
		
		if (eintrag.length() > 0) {
			char sig = eintrag.charAt(eintrag.length() - 1);
			if (sig == '.' || sig == '!' || sig == '?' || sig == '\"'
					|| sig == ';') {
				return eintrag;
			}
		}
		return eintrag + '.';
	}
}



