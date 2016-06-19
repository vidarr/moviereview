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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import tools.AppLogger;
import tools.CmdArgsParser;
import tools.Utils;
import data.Bogen;
import data.Globals;
import data.formats.BogenFormat;
import data.formats.HardDisc;
import data.formats.HardDisc.ImageException;
import data.formats.PDF;
import data.formats.Tex;
import data.formats.XML;
import data.formats.XMLConfig;

public class BMovieApp {

	Bogen bogen;

	public BMovieApp(Bogen bogen) {
		Utils.setBaseDir();
		// Falls gesetzt, Appletflag loeschen
		Globals.setAppType(Globals.STAND_ALONE | Globals.MIT_SENDEN
				| Globals.MIT_PDF);
		AppLogger.config(Globals.getInstance().toString());
		this.bogen = bogen;
		Gui g = new Gui(Globals.APP_NAME, this.bogen);
		g.setVisible(true);

	}

	protected static void printBogenAsTex(Bogen bogen, OutputStream out) {
		Tex tex = new Tex(bogen);
		try {
			tex.write(out);
		} catch (IOException e) {
			AppLogger.warning(e.getMessage());
		}
	}

	protected static void convertBogenToPdf(Bogen bogen) {
		String texFileName = bogen.getFilePath();
		texFileName += File.separator;
		texFileName += bogen.getFileNameWithoutEnding();
		texFileName += ".";
		texFileName += Tex.FORMAT_EXTENSION;
		PDF.write(bogen, texFileName);
	}

	public static void main(String[] argv) {

		Bogen bogen = null;

		CmdArgsParser args = new CmdArgsParser(argv);
		if (args.isMalformed()) {
			System.out.println(Globals.COPYLEFT);
			System.out.println("\n\nMögliche Kommandozeilenparameter sind:\n");
			for (String s : CmdArgsParser.POSSIBLE_LONG) {
				System.out.print(s + "   ");
			}
			System.out.println("\nSowie deren Kurzformen: ");
			for (String s : CmdArgsParser.POSSIBLE_SHORT) {
				System.out.print(s + "   ");
			}
			System.out
					.println("\n\n Zur genaueren Erläuterung siehe Datei 'Readme'.");
			return;
		}

		// Logger konfigurieren
		Logger log = AppLogger.getLogger();
		Handler handler = new StreamHandler(System.out, new SimpleFormatter());
		handler.setLevel(Level.ALL);
		log.addHandler(handler);
		log.setLevel(Level.ALL);

		try {
			// Falls Config ausgegeben werden soll, das tun und ohne weitere
			// Ausgabe
			// beenden
			if (args.isSet(CmdArgsParser.STAT_GENERATE_CONFIG)) {
				data.formats.XMLConfig.printConfig(System.out);
				return;
			}

			// DTD ausgeben?
			if (args.isSet(CmdArgsParser.STAT_PRINT_DTD)) {
				data.formats.XML.writeDTD(System.out);
				return;
			}

			// XLS ausgeben?
			if (args.isSet(CmdArgsParser.STAT_PRINT_XLS)) {
				data.formats.XML.writeXLS(System.out);
				return;
			}
		} catch (IOException ex) {
			AppLogger.warning(ex.getMessage());
			return;
		}
		// Konfigdatei setzen & laden
		Globals.getInstance().setProperty("configfile", args.getArgs()[1]);
		XMLConfig.loadConfig();
		if (!Utils.isTexProvided()) {
			Globals.getInstance().setProperty("texcommand", "disabled");
		}

		// Codierung setzen
		try {
			handler.setEncoding(Globals.getInstance().getProperty("encoding"));
		} catch (UnsupportedEncodingException e) {
			AppLogger.severe("System unterstuetzt Codierung "
					+ Globals.getInstance().getProperty("encoding") + " nicht");
		}

		// Bogen erzeugen
		if (args.isSet(CmdArgsParser.STAT_READ_IN)) {
			// Bogen wird aus bestehender Datei erzeugt...
			try {
				bogen = new Bogen();
				File in = new File(args.getArgs()[0]);
				InputStream inStream = new FileInputStream(in);
				BogenFormat serializer = new HardDisc(bogen);
				try {
					serializer.read(inStream);
				} finally {
					inStream.close();
				}
			} catch (ImageException e) {
				AppLogger.warning("Konnte Bilddatei " + bogen.getCover()
						+ " nicht laden");
			} catch (IOException e) {
				AppLogger.throwing("Main", "main", e);
				return;
			}
		} else {
			if (args.isSet(CmdArgsParser.STAT_IMPORT)) {
				// importieren?
				if (args.isSet(CmdArgsParser.STAT_PDF)) {
					// aus pdf erzeugen
					File in = new File(args.getArgs()[0]);
					if (!in.exists() || !in.isFile()) {
						AppLogger.severe("Datei " + in.toString()
								+ "existiert nicht");
					}
					bogen = PDF.read(in);
					bogen.setFilePath(in.getAbsolutePath());
					bogen.setFileName(in.getName());
				}
			} else {
				bogen = new Bogen();
			}
		}

		if (args.isSet(CmdArgsParser.STAT_CONVERT)) {
			BMovieApp.printBogenAsTex(bogen, System.out);
			return;
		}

		if (args.isSet(CmdArgsParser.STAT_CONVERT_TO_NEW)) {
			Globals.getInstance().setProperty("xmlformat",
					String.valueOf(XML.FORMAT_NEU));
			XML xml = new XML(bogen);
			try {
				xml.write(System.out);
			} catch (IOException e) {
				AppLogger.warning(e.toString());
			}
			return;
		}

		if (args.isSet(CmdArgsParser.STAT_EXPORT)) {
			if (args.isSet(CmdArgsParser.STAT_TEX)) {
				BMovieApp.printBogenAsTex(bogen, System.out);
				return;
			} else if (args.isSet(CmdArgsParser.STAT_PDF)) {
				BMovieApp.convertBogenToPdf(bogen);
			}
		}

		if (args.isSet(CmdArgsParser.STAT_GUI)) {
			System.out.println(Globals.COPYLEFT);
			new BMovieApp(bogen);
			return;
		}

	}

}
