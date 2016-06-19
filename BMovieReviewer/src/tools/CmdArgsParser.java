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
package tools;

import data.Globals;

public class CmdArgsParser {

	public CmdArgsParser(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException();
		}

		init();

		int pos = 0, inc = 0;
		String current = null;
		while (pos < args.length) {
			current = args[pos];
			if (current.length() > 0) {
				if (current.charAt(0) == '-') {
					// ist eine Option
					if (current.length() > 1) {
						if (current.charAt(1) == '-') {
							// Langform
							inc = parseLongForm(args, pos);
							if (inc == 0) {
								// nicht wohlgeformt => Abbruch
								malFormed = true;
								return;
							}
							pos += inc;
						} else {
							// Kurzform
							inc = parseShortForm(args, pos);
							if (inc == 0) {
								// nicht wohlgeformt => Abbruch
								malFormed = true;
								return;
							}
							pos += inc;
						}
					} else {
						// nicht wohlgeformt => Abbruch
						malFormed = true;
						return;
					}
				} else {
					// keine Option spezifiziert => Muss zu ladende XML Datei
					// sein
					file[0] = current;
					pos++;
				}
			} else {
				pos++;
			}
		}
		
		if(status == 0) status = STAT_GUI;
	}

	protected void init() {
		file = new String[2];
		file[1] = Globals.getInstance().getProperty("configfile");
	}

	/**
	 * Parst ein Argument Zurueckgegeben wird die Zahl der "verbrauchten"
	 * Argumente Dabei indiziert 0 einen Fehler
	 * 
	 * @param args
	 *            Argumentarray
	 * @param pos
	 *            Postion des zu parsenden Arguments
	 * @return
	 */
	protected int parseLongForm(String[] args, int pos) {
		int inc = 0;
		int i = 0;
		// remove leading -
		String current = args[pos].substring(2, args[pos].length());
		for (i = 0; i < POSSIBLE_LONG.length; i++) {
			if (current.equals(POSSIBLE_LONG[i])) {
				status |= STATI[i];
				if (OPT_ARGS[i] > -1) {
					if (args.length > pos + 1) {
						// es wird ein Argument erwartet und es folgt noch ein
						// Argument
						file[OPT_ARGS[i]] = args[pos + 1];
					} else {
						return 0;
					}
				}
				inc = INCREMENT[i];
			}
		}
		if (i > POSSIBLE_LONG.length) {
			inc = 0;
		}
		return inc;

	}

	/**
	 * Parst ein Kurzargument Zurueckgegeben wird die Zahl der "verbrauchten"
	 * Argumente Dabei indiziert 0 einen Fehler
	 * 
	 * @param args
	 *            Argumentarray
	 * @param pos
	 *            Postion des zu parsenden Arguments
	 * @return
	 */
	protected int parseShortForm(String[] args, int pos) {
		int inc = 0;
		int i = 0;
		// remove leading -
		String current = args[pos].substring(1, args[pos].length());
		for (i = 0; i < POSSIBLE_SHORT.length; i++) {
			if (current.equals(POSSIBLE_SHORT[i])) {
				status |= STATI[i];
				if (OPT_ARGS[i] > -1) {
					if (args.length > pos + 1) {
						// es wird ein Argument erwartet und es folgt noch ein
						// Argument
						file[OPT_ARGS[i]] = args[pos + 1];
					} else {
						return 0;
					}
				}
				inc = INCREMENT[i];
			}
		}
		if (i > POSSIBLE_SHORT.length) {
			inc = 0;
		}
		return inc;
	}

	public String[] getArgs() {
		return file;
	}

	public int getStatus() {
		return status;
	}

	public boolean isMalformed() {
		return malFormed;
	}

	public boolean isSet(int opt) {
		if ((getStatus() & opt) == opt) {
			return true;
		}
		return false;
	}

	/**
	 * Moegliche angegebene Dateien 0 zu behandelnde Datei 1 config
	 */
	protected String[] file;

	/**
	 * wohlgeformte Parameterzeile ?
	 */
	protected boolean malFormed = false;

	// //////////////////////////////////////////////////////////
	// Alle moeglichen Parameter
	public final static String[] POSSIBLE_LONG = { "generate-config-file",
			"config-file", "print-bmovie-dtd", "import-pdf", "export-pdf",
			"export-tex", "convert-pdf", "file" , "print-bmovie-xls", "convert-to-new"};

	public final static String[] POSSIBLE_SHORT = { "gcf", "cfg", "dtd", "ip",
			"ep", "et", "cp", "f", "xls", "c2n"};

	protected final int[] INCREMENT = { 1, 2, 1, 2, 2, 2, 2, 2, 1, 2};

	protected final int[] STATI = { 
	        STAT_GENERATE_CONFIG, //gcf
	        STAT_GUI | STAT_LOAD_CFG, // cfg
			STAT_PRINT_DTD, // dtd
			STAT_IMPORT | STAT_PDF | STAT_GUI,  //ip
			STAT_EXPORT | STAT_PDF | STAT_READ_IN, // ep
			STAT_EXPORT | STAT_XML | STAT_TEX | STAT_READ_IN,  // et
			STAT_CONVERT | STAT_PDF | STAT_READ_IN, // cp
			STAT_GUI | STAT_IMPORT | STAT_XML | STAT_READ_IN, // f
			STAT_PRINT_XLS,  // xls
			STAT_CONVERT_TO_NEW | STAT_XML | STAT_READ_IN  // c2n
			};

	protected final int[] OPT_ARGS = { -1, 1, -1, 0, 0, 0, 0, 0, -1, 0};

	protected int status = 0;

	// Moegliche Zustaende    
	/**
    * Eine Datei muss eingelesen werden
    */
   public static final int STAT_READ_IN = 2048;
   
	/**
	 * Gui starten?
	 */
	public static final int STAT_GUI = 1;
	/**
	 * Importieren?
	 */
	public static final int STAT_IMPORT = 2;
	/**
	 * Exportieren?
	 */
	public static final int STAT_EXPORT = 4;
	/**
	 * Konvertieren?
	 */
	public static final int STAT_CONVERT = 8;
	
	/**
	 * Tex?
	 */
	public static final int STAT_TEX = 16;
	/**
	 * XML?
	 */
	public static final int STAT_XML = 32;
	/**
	 * PDF
	 */
	public static final int STAT_PDF = 64;

	/**
	 * Generate config file
	 */
	public static final int STAT_GENERATE_CONFIG = 128;
	/**
	 * Print dtd
	 */
	public static final int STAT_PRINT_DTD = 256;
	
	/**
     * Print XLS
     */
    public static final int STAT_PRINT_XLS = 512;
    
    /**
     * Uebergebenen Bogen ins neue Format konvertieren
     */
    public static final int STAT_CONVERT_TO_NEW = 1024;
    
    /**
     * Konfig laden
     */
    public  static final int STAT_LOAD_CFG = 2048;  
}
