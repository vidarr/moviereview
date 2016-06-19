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
package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import data.Globals;
import data.ResourceManager;

public class Utils {
	
	public static void copy(URL s, File d) throws IOException,
			IllegalArgumentException {
		if (s == null || d == null) {
			throw new IllegalArgumentException();
		}
		// Auf Gleichheit testen - dann nat. nicht kopieren...
		if (s.equals(d)) {
			return;
		}

		// falls schon existent, wird die Datei geloescht
		if (d.exists()) {
			d.delete();
		}

		// sonst kopieren
		InputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = s.openStream();
			outStream = new FileOutputStream(d);
			byte[] readBuffer = new byte[255];
			int readBytes = inStream.read(readBuffer);
			while (0 < readBytes) {
				outStream.write(readBuffer, 0, readBytes);
				readBytes = inStream.read(readBuffer);
			}
		} finally {
			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();
		}
	}

	public static void checkForSTY(String dir) throws IOException {
		File dest = new File(dir + "bmovie.sty");
		if (!dest.exists()) {
			URL urlToSTY = ResourceManager.getInstance().getResource(
					"xml.bmoviesty");
			copy(urlToSTY, dest);
		}
	}

	public static boolean isTexProvided() {
		boolean provided = false;

		try {
			Process p = Runtime.getRuntime().exec(
					Globals.getInstance().getProperty("texcommand")
							+ " --version");
			p.waitFor();

			if (p.exitValue() == 0) {
				provided = true;
			}
		} catch (Exception e) {
			// Fehler => tex deaktivieren
		}

		return provided;
	}

	public static void setBaseDir() {
		Globals.getInstance().setProperty("basedirectory",
				System.getProperty("user.dir"));
		System.out.println("BaseDir: " + System.getProperty("user.dir"));
	}

	/**
	 * Liefert den reinen Verzeichnispfadanteil eines Dateipfades zurueck
	 * 
	 * @param url
	 * @return
	 */
	public static String getDirPath(String url) {
		int pathEnd = url.lastIndexOf(File.separatorChar);
		if (pathEnd > 0) {
			return url.substring(0, pathEnd + 1);
		}
		return "";
	}

}
