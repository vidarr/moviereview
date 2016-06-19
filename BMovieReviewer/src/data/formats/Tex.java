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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.swing.filechooser.FileNameExtensionFilter;

import tools.AppLogger;
import data.Bogen;
import data.QualifiedString;
import data.wrappers.QualifiedStringList;

public class Tex extends BogenFormat {

	// public static void write(OutputStream out, Bogen bogen) {
	// Tex tex = new Tex(out);
	// tex.format(out);
	// }
	public static final String FORMAT_EXTENSION = "xml";
	public static final String PREFERRED_ENCODING = "UTF-8";

	public Tex(Bogen bogen) {
		super(bogen);
	}

	@Override
	public String getFormatExtension() {
		return "tex";
	}

	@Override
	public FileNameExtensionFilter getFileNameExtensionFilter() {
		return new FileNameExtensionFilter("Tex-Dateien", "tex");
	}


	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC CONSTANTS
	// /////////////////////////////////////////////////////////////////////////

	public static final String[] KATEGORIEN = { "bmUnterhaltungswert",
			"bmPornofaktor", "bmGewaltdarstellung", "bmGewaltverherrlichung",
			"bmNiveau", "bmSexismus", "bmProfessionalitaet", "bmRealismus" };

	public static final String TEX_MAKE_BMOVIE_BOGEN = "bmMakeBMovieBogen";

	/*************************************************************************
	 * INTERNALS
	 *************************************************************************/

	@Override
	protected void write() throws IOException {
		this.makePrintStream(super.out);
		try {
			this.writeHeader();
			this.writeCategoryFields();
			this.writeTextFields();
			this.writeQuotations();
			this.writeMainPart();
		} finally {
			this.closePrintStream();
		}
	}
	
	private void makePrintStream(OutputStream out) {
		if (out == null) {
			throw new IllegalArgumentException();
		}
		try {
			this.out = new PrintStream(out, true, PREFERRED_ENCODING);
		} catch (UnsupportedEncodingException e) {
			AppLogger
					.warning("could not set encoding to " + PREFERRED_ENCODING);
			this.out = new PrintStream(out);
		}
	}

	private void closePrintStream() {
		this.out.close();
	}

	private void writeHeader() {
		out.print("\\documentclass [11pt]{article}");
		out.println();
		out.print("\\usepackage[utf8]{inputenc}");
		out.println();
		out.print("\\usepackage[ngerman]{babel}");
		out.println();
		out.print("\\usepackage {a4wide}");
		out.println();
		out.print("\\usepackage{bmovie}");
		out.println();
		out.print("\\begin{document}");
		out.println();
	}

	private void writeCategoryFields() {

		for (int index = 0; index < Bogen.KATEGORIEN.length; index++) {
			int punkt = bogen.getPunkt(index);
			if (punkt > 0) {
				out.print("\\" + KATEGORIEN[index]);
				String anmerkung = bogen.getAnmerkung(index);
				if (anmerkung != null) {
					out.print("[" + anmerkung + "]");
				}
				out.print("{" + punkt + "}");
				out.println();
			}
		}
	}

	private void writeTextFields() {
		out.println("\\bmTechnisch{"
				+ textToTex(bogen.getText(Bogen.I_TECHNISCH)) + "}");
		out.println("\\bmWissenschaft{"
				+ textToTex(bogen.getText(Bogen.I_WISSENSCHAFT)) + "}");
		out.println("\\bmInhalt{" + textToTex(bogen.getText(Bogen.I_INHALT))
				+ "}");
		out.println("\\bmBild{" + textToTex(bogen.getText(Bogen.I_BILD)) + "}");
		out.println("\\bmHandlung{"
				+ textToTex(bogen.getText(Bogen.I_HANDLUNG)) + "}");
		out.println("\\bmBemerkungen{"
				+ textToTex(bogen.getText(Bogen.I_BEMERKUNGEN)) + "}");
	}

	private void writeQuotations() {
		QualifiedStringList zitate = bogen.getZitate();
		if (zitate == null) {
			// TODO: What to do here?
		}
		if (!zitate.isEmpty()) {
			out.println("\\bmZitate{");
			Iterator<QualifiedString> it = zitate.iterator();
			while (it.hasNext()) {
				String zitat = it.next().getText();
				zitat = textToTex(zitat);
				out.println(zitat);
				if (it.hasNext()) {
					out.println("\\\\");
				}
			}
			out.println("}");
		}
	}

	private void writeMainPart() {
		out.println("\\" + TEX_MAKE_BMOVIE_BOGEN + "{"
				+ bogen.getText(Bogen.I_TITEL) + "}{"
				+ bogen.getText(Bogen.I_LAND) + "}{"
				+ bogen.getText(Bogen.I_ORIGINALTITEL) + "}{"
				+ bogen.getText(Bogen.I_JAHR) + "}{"
				+ bogen.getText(Bogen.I_FSK) + "}{"
				+ bogen.getText(Bogen.I_GENRE) + "}");
		out.print("\\end{document}");
		out.println();
	}

	private String textToTex(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		System.out.println("Processing '" + text + "'");
		if (text.length() == 0)
			return text;
		String result = text;
		// Anfuehrungszeichen konvertieren
		int oeffnend = 0;
		for (int i = 0; i < result.length(); i++) {
			if (result.charAt(i) == '\"') {
				switch (oeffnend) {
				case 0:
					result = result.replaceFirst("\"", "``");
					break;
				case 1:
					result = result.replaceFirst("\"", "\'\'");
					break;
				default:
					throw new RuntimeException();
				}
				oeffnend = 1 - oeffnend;
			}
		}

		result = result.replaceAll("\\\\", "\\\\textbackslash");
		result = result.replaceAll("\\$", "\\\\\\$");
		result = result.replaceAll("<br/>", "\\\\\\\\ ");
		result = result.replaceAll("<li/>", "\\$\\\\bullet\\$");
		result = result.replaceAll("&", "\\\\&");
		result = result.replaceAll("<", "\\$<\\$");
		result = result.replaceAll(">", "\\$>\\$");
		return result;
	}
	

	@Override
	protected void read() throws IOException {
		// It is not intended to read tex files...
	}

	private PrintStream out;

}
