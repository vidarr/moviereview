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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

import tools.AppLogger;
import data.Bogen;
import data.wrappers.StringWrapper;

public class Zip extends BareFiles {

	public static final String ARCHIVE_EXTENSION = "bmr";

	public static final String INDEX_FILE = "index.xml";

	public Zip(Bogen bogen) {
		super(bogen);
	}

	public Zip(Bogen bogen, File file) {
		super(bogen);
		setFile(file);
	}

	@Override
	public String getFormatExtension() {
		return Zip.ARCHIVE_EXTENSION;
	}

	@Override
	public void read(InputStream in) throws IOException {
		throw new RuntimeException(
				"Cannot read Zip from bare InputStream - internal error");
	}

	@Override
	public void read(File file) throws IOException {
		setFile(file);
		read();
	}

	@Override
	public FileNameExtensionFilter getFileNameExtensionFilter() {
		return Zip.getFileNameExtensionFilteFromStatic();
	}

	public static FileNameExtensionFilter getFileNameExtensionFilteFromStatic() {
		return new FileNameExtensionFilter("B-MovieReviews",
				Zip.ARCHIVE_EXTENSION, Zip.ARCHIVE_EXTENSION.toUpperCase());
	}

	// TODO: Remove this method entirely
	public void setFile(File file) {
		this.inFile = file;
		super.setFile(file);
	}

	// /////////////////////////////////////////////////////////////////////////
	// INTERNALS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	protected void write() throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(out);
		try {
			writeFilesToZipArchiveStream(zipOut);
			AppLogger.info(getArchiveFileName() + " gespeichert");
		} finally {
			zipOut.close();
		}
	}

	protected void writeFilesToZipArchiveStream(ZipOutputStream out)
			throws IOException {
		if (bogen.getCoverImage() != null) {
			this.writeImageFileToArchive(out);
		}
		this.writeXmlFileToArchive(out);
	}

	protected void writeImageFileToArchive(ZipOutputStream out)
			throws IOException {
		ZipEntry imageZipEntry = new ZipEntry(this.getImageFileName());
		out.putNextEntry(imageZipEntry);
		this.writeImage(new PrintStream(out));

	}

	protected void writeXmlFileToArchive(ZipOutputStream out)
			throws IOException {
		ZipEntry xmlZipEntry = new ZipEntry(this.getXmlFileName());
		out.putNextEntry(xmlZipEntry);
		this.writeXML(new PrintStream(out));
	}

	@Override
	protected void read() throws IOException {
		loadXmlFromArchive();
		loadImageFromArchive();
	}

	protected void loadXmlFromArchive() throws IOException {
		ZipInputStream in = getZipInputStream();
		try {
			loadXmlFromZipStream(in);
		} finally {
			in.close();
		}
	}

	protected ZipInputStream getZipInputStream() throws IOException {
		InputStream inStream = new FileInputStream(this.inFile);
		return new ZipInputStream(inStream);
	}

	protected void loadXmlFromZipStream(ZipInputStream in) throws IOException {
		InputStream inStream = getXmlStreamFromArchive(in);
		try {
			XML xml = new XML(bogen);
			xml.read(in);
		} finally {
			inStream.close();
		}
	}

	protected InputStream getXmlStreamFromArchive(ZipInputStream in)
			throws IOException {
		return getInputStreamForFile(in, Zip.INDEX_FILE);
	}

	protected InputStream getInputStreamForFile(ZipInputStream in, String name)
			throws IOException {
		for (ZipEntry entry = in.getNextEntry(); in.available() > 0; in
				.closeEntry(), entry = in.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().equals(name)) {
				return in;
			}
		}
		throw new IOException("No valid data in file");
	}

	protected void loadImageFromArchive() throws IOException {
		ZipInputStream in = getZipInputStream();
		try {
			loadImageFromArchive(in);
		} catch(NoImageException ex) {
		} finally {
			in.close();
		}
	}

	protected InputStream getImageStreamFromArchive(ZipInputStream in)
			throws IOException, NoImageException {
		StringWrapper cover = this.bogen.getCover();
		if (cover == null || cover.toString().equals(StringWrapper.EMPTY_STRING)) {
			throw new NoImageException();
		}
		String imageFileName = cover.toString();
		return getInputStreamForFile(in, imageFileName);
	}

	protected void loadImageFromArchive(ZipInputStream in) throws IOException,
			NoImageException {
		int scaledWidth = getScaledWidth();
		InputStream inStream = getImageStreamFromArchive(in);
		try {
			BogenFormat imageSerializer = new JPEG(this.bogen, scaledWidth);
			imageSerializer.read(inStream);
		} finally {
			inStream.close();
		}
	}

	protected String getArchiveFileName() {
		return bogen.getFilePath() + File.separator
				+ this.fileNameWithoutEnding + "." + ARCHIVE_EXTENSION;
	}

	@Override
	protected String getXmlFileName() {
		return INDEX_FILE;
	}

	File inFile = null;

	public static class NoImageException extends Exception {

		public NoImageException() {
			super();
		}

		private static final long serialVersionUID = 1L;
	}
}
