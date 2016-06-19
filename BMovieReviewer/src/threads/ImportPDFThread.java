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
package threads;

import gui.Gui;

import java.io.File;

import javax.swing.JOptionPane;

import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.formats.PDF;

public class ImportPDFThread implements Runnable {

	protected File file;
	protected Gui gui;
	
	public ImportPDFThread(File file, Gui gui) {
		if(file == null || gui == null) {
			throw new IllegalArgumentException();
		}
		
		this.gui = gui;
		this.file = file;
	}
	
	
	@Override
	public void run() {
		Bogen bogen = null;
		String status = file.toString();
		int timeout = Globals.getInstance().getPropertyAsInt("iotimeout");
		
		ThreadRegistry.getInstance().registerThread(this, "ImportPDF");
		this.gui.setStatus(status + " wird konvertiert...");
		
		ImportThread importThread = new ImportThread(file);
		Thread imp = new Thread(new TimeoutThread(importThread, timeout));
		imp.start();
		try{
		    imp.join(); // Das Ende des TimeoutThreads abwarten
		}catch(InterruptedException e){
		    AppLogger.throwing("ImportPDFThread", "run()", e);
		}
		bogen = importThread.getBogen();  // falls timeout, gibt es hier null
		
		if (bogen != null) {
			int returnVal = JOptionPane.showConfirmDialog(this.gui, status + " importiert.\nSollen die Daten übernommen werden?", "Import von PDF", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if(returnVal == JOptionPane.YES_OPTION)	{
				AppLogger.info(bogen.toString());
				gui.setBogen(bogen);
			gui.getBogen().setFileName(file.getName());
			gui.getBogen().setFilePath(file.getPath());
			status += " importiert.";
		}
		} else {
			status += "konnte nicht importiert werden!";
			JOptionPane.showMessageDialog(this.gui, status, "Fehler beim Import von PDF", JOptionPane.ERROR_MESSAGE);
		}
		this.gui.setStatus(status);
		ThreadRegistry.getInstance().unregisterThread(this);
	}
	
	
	public String toString() {
		return "ImportPDFThread : " + file.toString();
	}

	
	protected static class ImportThread implements Runnable {

	    protected Bogen bogen = null;
	    protected File file;
	    
	    public ImportThread(File file){
	        this.file = file;
	    }
	    
        @Override
        public void run() {
            bogen = PDF.read(file);         
        }
        
        public Bogen getBogen() {
            return bogen;
        }
	    
	}
	
}
