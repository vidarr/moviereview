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
package main;

import gui.Gui;

import javax.swing.JApplet;

import tools.AppLogger;
import tools.Utils;
import data.Bogen;
import data.Globals;

public class BMovieApplet 
	extends JApplet
	implements Runnable {
	
	private static final long serialVersionUID = 1L;
	

	
	public void init() {
	}
	
	public BMovieApplet() {
	    Globals.setAppType(Globals.APPLET | Globals.MIT_SENDEN);
	    // Alle anderen Flags loeschen
	    //Globals.setAppType(Globals.getAppType() & Globals.APPLET);
		AppLogger.config(Globals.getInstance().toString());
		
		// Wenn Dictionarybase unbekannt, Rechtschreibpruefung deakt.
        String base = Globals.getInstance().getProperty("spellcheck.mode");
        if(!base.equals("disabled")) {
            base = Globals.getInstance().getProperty("spellcheck.base");
            if(base.equals("unknown")) {
                // Falls keine URL zu Woerterbuch angegeben, Rechtschreibprueufung ausschalten
                Globals.getInstance().setProperty("spellcheck.mode", "disabled");
                AppLogger.warning("dictionary_base=unknown - Rechtschreibprüfung abgeschalten.");
            }
        }
		
		Gui g = new Gui(Globals.APP_NAME, new Bogen());
		g.setVisible(true);
		getContentPane().add(g);
		this.setVisible(true);
	}
	
	public BMovieApplet(Bogen bogen) {
		if((Globals.getAppType() & Globals.STAND_ALONE) == Globals.STAND_ALONE) {
			Utils.setBaseDir();
		}
		AppLogger.config(Globals.getInstance().toString());
		Gui g = new Gui(Globals.APP_NAME, bogen);
		g.setVisible(true);
		getContentPane().add(g);
		this.setVisible(true);
	}
	
    public void run() {
    }

}
