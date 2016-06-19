/*
 * BMovieReviewer Copyright (C) 2009, 2010 Michael J. Beer
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
package gui.dialogs;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Einfacher Keylistener, der auf die Eingabetaste dahingehen reagiert, dass er den Fokus an eine 
 * bestimmte Komponente weitergibt
 * @author mibeer
 *
 */
class KeyFocusNextListener implements KeyListener {

	protected Component comp = null;
	protected int key = 0;
	
	public KeyFocusNextListener(Component comp) {
		this(comp, KeyEvent.VK_ENTER);
	}
	
	public KeyFocusNextListener(Component comp, int key) {
		if(comp == null) {
			throw new IllegalArgumentException();
		}
		this.comp = comp;
		this.key = key;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			this.comp.requestFocus();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			this.comp.requestFocus();
		}
	}
}
