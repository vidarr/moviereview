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
import java.util.Vector;

import javax.swing.filechooser.FileNameExtensionFilter;

import tools.AppLogger;
import data.Bogen;

public class BogenSerializerFactory {
	
	public static final String preferredExtension = Zip.ARCHIVE_EXTENSION;

	public static BogenFormat getSerializerForFile(File file, Bogen bogen) throws IOException {
		BogenFormat serializer = null;
		int startOfEnding = file.getName().lastIndexOf('.');
		String ending = file.getName().substring(startOfEnding + 1,
				file.getName().length());
		ending = ending.toLowerCase();
		if (ending.equals(Zip.ARCHIVE_EXTENSION)) {
			serializer = new Zip(bogen, file);
		} else if (ending.equals("xml")) {
			serializer = new HardDisc(bogen, file);
		} else {
			AppLogger.warning("Dateiendung " + ending + " unbekannt");
			throw new IOException();
		}
		return serializer;
	}
	
	public static Vector<FileNameExtensionFilter> getFileNameExtensionFilters() {
		Vector<FileNameExtensionFilter> fileNameExtensionFilters = new Vector<FileNameExtensionFilter>();
		FileNameExtensionFilter filter = HardDisc.getFileNameExtensionFilterFromStatic();
		fileNameExtensionFilters.add(filter);
		filter = Zip.getFileNameExtensionFilteFromStatic();
		fileNameExtensionFilters.add(filter);
		return fileNameExtensionFilters;
	}
}
