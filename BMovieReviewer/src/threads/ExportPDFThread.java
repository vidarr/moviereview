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
package threads;

import gui.Gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import tools.AppLogger;
import tools.Utils;
import data.Bogen;
import data.Globals;
import data.formats.PDF;

public class ExportPDFThread implements Runnable {

	protected Gui gui = null;
	protected String fileName = null;
	protected String dir = null;
	
	public ExportPDFThread(Gui gui, String dir, String fileName) {
		if(gui == null || fileName == null || dir == null) {
			throw new IllegalArgumentException();
		}
		this.gui = gui;
		this.fileName = fileName;
		this.dir = dir;
	}
	
	@Override
	public void run() {
		ThreadRegistry.getInstance().registerThread(this, "ExportPDF");
		this.gui.getStatusBar().setStatus(dir + File.separator + fileName + ".pdf wird exportiert...");
        int timeout = Globals.getInstance().getPropertyAsInt("iotimeout");
        
		try{
			// sicherstellen, dass movie.sty im Arbeitsverzeichnis liegt
			//Utils.checkForSTY(dir);
			Utils.checkForSTY(Globals.getInstance().getProperty("basedirectory") + File.separator);
		}catch(IOException e) {
			this.gui.getStatusBar().setStatus("Fehler beim Export von " + fileName + ".pdf ");
			AppLogger.throwing("ExportPDFThread", "run()", e);
			JOptionPane.showMessageDialog(this.gui, "bmovie.sty konnte nicht im Zileverzeichnis erzeugt werden", 
					"Ein/Ausgabefehler", JOptionPane.ERROR_MESSAGE);
			ThreadRegistry.getInstance().unregisterThread(this);
			return;
		}
		
		PDF.write(this.gui.getBogen(), dir + File.separator + fileName + ".tex");
		
		ExportThread exportThread = new ExportThread(this.gui.getBogen(), dir + fileName + ".tex");
        Thread imp = new Thread(new TimeoutThread(exportThread, timeout));
        imp.start();
        try{
            imp.join(); // Das Ende des TimeoutThreads abwarten
        }catch(InterruptedException e){
            AppLogger.throwing("ExportPDFThread", "run()", e);
        }
		
		this.gui.getStatusBar().setStatus("Erfolgreich nach " + fileName + ".pdf exportiert");			
		JOptionPane.showMessageDialog(this.gui, "Erfolreich nach " + fileName + ".pdf exportiert", 
				"PDF-Export", JOptionPane.INFORMATION_MESSAGE);
		ThreadRegistry.getInstance().unregisterThread(this);
	}
	
	
	public String toString() {
		return "ExportPDFThread : " + fileName;
	}

	   protected static class ExportThread implements Runnable {

	        protected Bogen bogen = null;
	        protected String fileName;
	        public ExportThread(Bogen bogen, String name){
	            this.bogen = bogen;
	            this.fileName = name;
	        }
	        
	        @Override
	        public void run() {
	            PDF.write(this.bogen, fileName);         
	        }
        
	    }
	
}
