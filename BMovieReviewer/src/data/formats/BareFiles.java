/*
 * BMovieReviewer Copyright (C) 2012 Michael J. Beer
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
package data.formats;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import data.Bogen;
import data.Globals;

public abstract class BareFiles extends BogenFormat {

	protected BareFiles(Bogen bogen) {
		super(bogen);
		this.setBogen(bogen);
	};

	protected void setBogen(Bogen bogen) {
		super.setBogen(bogen);
		this.fileNameWithoutEnding = bogen.getFileNameWithoutEnding();
		this.absolutePath = bogen.getFilePath();
	}


	protected void setFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		this.absolutePath = file.getAbsolutePath();
	}
	
	protected void writeImage(PrintStream out) throws IOException {
		BogenFormat formatter = new JPEG(this.bogen);
		formatter.write(out);

	}

	protected void writeXML(PrintStream out) throws IOException {
		BogenFormat formatter = new XML(this.bogen);
		formatter.write(out);
	}

	protected String getXmlFileName() {
		return fileNameWithoutEnding + ".xml";
	}

	protected String getImageFileName() {
		return bogen.getCover().getText();
	}
	
	protected String getFullXmlFileName() {
		return absolutePath + File.pathSeparator + getXmlFileName();
	}
	
	protected String getFullImageFileName() {
		return absolutePath + File.pathSeparator + getImageFileName();
	}
	
	protected int getScaledWidth() {
		int scaledWidth = 300;
		try {
			scaledWidth = Integer.parseInt(globs.getProperty("coverwidth"));
		} catch (NumberFormatException e) {
			meldung = "Eigenschaft coverwidth nichtnumerisch!";
		}
		return scaledWidth;
	}

	protected String absolutePath          = null;
	protected String fileNameWithoutEnding = null;

	protected String meldung = "";

	protected Globals globs = Globals.getInstance();

}
