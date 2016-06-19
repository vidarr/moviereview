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
package data.formats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.filechooser.FileNameExtensionFilter;

import tools.AppLogger;
import data.Bogen;
import data.wrappers.StringWrapper;

/**
 * Dient dem Ablegen/ Laden des Bogens von Platte
 * 
 * @author mibeer
 * 
 */
public class HardDisc extends BareFiles {

	public static final String ARCHIVE_EXTENSION = "xml";

	public HardDisc(Bogen bogen) {
		super(bogen);
	}
	
	public HardDisc(Bogen bogen, File file) {
		this(bogen);
		this.setFile(file);
	}
	
	@Override
	public String getFormatExtension() {
		return "xml";

	}

	public FileNameExtensionFilter getFileNameExtensionFilter() {
		return HardDisc.getFileNameExtensionFilterFromStatic();
	}
	
	public static FileNameExtensionFilter getFileNameExtensionFilterFromStatic () {
		return new FileNameExtensionFilter("XML-Dateien", "xml", "XML");
	}
	
	public static class ImageException extends IOException {

		private static final long serialVersionUID = 1L;

		public ImageException() {
			super();
		}
		
		public ImageException(String msg) {
			super(msg);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// INTERNALS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	protected void write() throws IOException {
		if (bogen.getCoverImage() != null) {
			this.writeBareImageFile();
		}
		this.writeBareXmlFile();
	}

	protected void writeBareImageFile() throws IOException {
		File imageFile = new File(this.getFullImageFileName());
		PrintStream out = new PrintStream(new FileOutputStream(imageFile));
		try {
			this.writeImage(out);
			meldung += this.getFullImageFileName() + " gespeichert;  ";
			AppLogger.info(this.getFullImageFileName() + " gespeichert;  ");
		} finally {
			out.close();
		}
	}

	protected void writeBareXmlFile() throws IOException {
		File xmlFile = new File(this.getFullXmlFileName());
		PrintStream out = new PrintStream(new FileOutputStream(xmlFile));
		try {
			this.writeXML(out);
			meldung += this.getXmlFileName() + " gespeichert";
			AppLogger.info(this.getXmlFileName() + " gespeichert");
		} finally {
			out.close();
		}
	}

	@Override
	protected void read() throws IOException {
		readXmlFile();
		if (!bogen.getCover().getText().equals(StringWrapper.EMPTY_STRING)) {
			readImageFile();
		}
	}

	protected void readXmlFile() throws IOException {
		XML xml = new XML(bogen);
		try {
			xml.read(in);
		} finally {
			in.close();
		}
	}

	protected void readImageFile() throws IOException {
		int scaledWidth = getScaledWidth();
		URL url = new File(getImageFileName()).toURI().toURL();
		InputStream in;
		in = url.openStream();
		try {
			BogenFormat imageSerializer = new JPEG(this.bogen, scaledWidth);
			imageSerializer.read(in);
		} catch (Exception e) {
			throw new ImageException("Konnte Bilddatei " + url.toString()
					+ " nicht laden!");
		} finally {
			in.close();
		}
	}
}
