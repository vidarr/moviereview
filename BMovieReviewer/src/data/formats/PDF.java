/*
 * BMovieReviewer Copyright (C) 2009 Stefan Knipl, Michael J. Beer
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Vector;

import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.QualifiedString;
import data.Zitat;

public class PDF {

	public static boolean enableTex = true;

	public static String tech_errors = "";
	public static String story_errors = "";
	public static String science_errors = "";
	public static String image = "";
	public static String comments;
	public static String story = "";
	public static Vector<String> quotes = new Vector<String>();
	public static Vector<String> files_to_convert = new Vector<String>();
	public static boolean windows = false; // do we run on windows?

	public static boolean old_eval_file = true;
	public static BufferedReader pdftext;

	public static Bogen read(File pdfIn) {
		Bogen b = null;

		// Falls Tex nicht unterstuetzt wird, nichts tun
		if (!enableTex)
			return null;

		try {
			BufferedReader pdfinfoOut = new BufferedReader(
					new InputStreamReader(Runtime.getRuntime()
							.exec("pdfinfo " + pdfIn.toString())
							.getInputStream()));
			String creator = pdfinfoOut.readLine();
			if (creator.contains("TeX")) {
				old_eval_file = false;
			}
			pdfinfoOut.close();
		} catch (IOException e) {
			System.err.println("Fehler beim Lesen der PDFDatei "
					+ pdfIn.toString());
			return null;
		}
		// use pdfinfo to check for new eval file format
		try {
			if (old_eval_file) {
				b = evaluateOldFileFormat(pdfIn);
			} else {
				b = evaluateNewFileFormat(pdfIn);
			}
		} catch (IOException e) {
			System.err.println("Fehler beim Lesen der PDFDatei "
					+ pdfIn.toString());
			return null;
		} catch (Exception e) {
			System.err.println("Fehler beim Lesen der PDFDatei "
					+ pdfIn.toString());
			e.printStackTrace();
			return null;
		}

		new File(pdfIn.toString().replace(".pdf", ".txt")).delete();
		return b;
	}

	public static Bogen evaluateNewFileFormat(File pdfIn) throws IOException {
		Bogen bogen = new Bogen();
		// use ps2txt
		// System.out.println("The option to convert the new evaluation file type - which is used by "
		// + files_to_convert.get(z) + " - has not yet been implemented.");
		if (windows) {
			System.out.println("Converting this file - " + pdfIn.toString()
					+ " - is not yet supported on windows.");
			return null;
		}

		int punkt;
		String nextLine = "";
		boolean no_new_line = false; // if we read too much while parsing we
		// don't need to read a new line in the
		// while statement
		pdftext = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
				.exec("ps2txt " + pdfIn.toString()).getInputStream()));

		while (no_new_line || (nextLine = pdftext.readLine()) != null) {

			no_new_line = false;
			if (nextLine != null && nextLine != "") // we read a non-empty line,
													// now we check
			// out if we need any infos out of it
			{
				if (nextLine.contains("Name: ")) {
					nextLine = nextLine.substring(
							nextLine.indexOf("Name: ") + 6,
							nextLine.indexOf("Land: ")).trim();
					nextLine = nextLine.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
					bogen.setText(Bogen.I_TITEL, nextLine);
				} // umlauts are represented as "a, "o, "u, etc. - replace
					// them. False positives can not be avoided :-(
				if (nextLine.contains("Land: ")) {
					nextLine = nextLine.substring(
							nextLine.lastIndexOf("Land: ") + 6,
							nextLine.indexOf("Originaltitel: ")).trim();
					nextLine = nextLine.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
					bogen.setText(Bogen.I_LAND, nextLine);
				}
				if (nextLine.contains("Originaltitel: ")) {
					nextLine = nextLine.substring(
							nextLine.indexOf("Originaltitel: ") + 15,
							nextLine.lastIndexOf("Jahr: ")).trim();
					nextLine = nextLine.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
					bogen.setText(Bogen.I_ORIGINALTITEL, nextLine);
				}
				if (nextLine.contains("Jahr: ")) {
					nextLine = nextLine.substring(
							nextLine.indexOf("Jahr: ") + 6,
							nextLine.indexOf("FSK: ")).trim();
					bogen.setText(Bogen.I_JAHR, nextLine);
				}
				if (nextLine.contains("FSK: ")) {
					nextLine = nextLine.substring(
							nextLine.lastIndexOf("FSK: ") + 5,
							nextLine.indexOf("Genre: ")).trim();
					bogen.setText(Bogen.I_FSK, nextLine);
				}
				if (nextLine.contains("Genre: ")) {
					nextLine = nextLine.substring(
							nextLine.indexOf("Genre: ") + 6).trim();
					nextLine = nextLine.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
					bogen.setText(Bogen.I_GENRE, nextLine);
				}
				if (nextLine.contains("Unterhaltungswert:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Unterhaltungswert:"),
							nextLine.indexOf("Pornofaktor:")));
					bogen.setPunkt(Bogen.I_UNTERHALTUNGSWERT, punkt);
				}
				if (nextLine.contains("Pornofaktor:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Pornofaktor:"),
							nextLine.indexOf("Gewaltdarstellung:")));
					bogen.setPunkt(Bogen.I_PORNOFAKTOR, punkt);
				}
				if (nextLine.contains("Gewaltdarstellung")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Gewaltdarstellung:"),
							nextLine.indexOf("Gewaltverherrlichung:")));
					bogen.setPunkt(Bogen.I_GEWALTDARSTELLUNG, punkt);
				}
				if (nextLine.contains("Gewaltverherrlichung:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Gewaltverherrlichung:"),
							nextLine.indexOf("Niveau")));
					bogen.setPunkt(Bogen.I_GEWALTVERHERRLICHUNG, punkt);
				}
				if (nextLine.contains("Niveau:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Niveau:"),
							nextLine.indexOf("Sexismus:")));
					bogen.setPunkt(Bogen.I_NIVEAU, punkt);
				}
				if (nextLine.contains("Sexismus:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Sexismus:"),
							nextLine.indexOf("Professionalit\"at")));
					bogen.setPunkt(Bogen.I_SEXISMUS, punkt);
				}
				if (nextLine.contains("Professionalit\"at:")) {
					punkt = getValue(nextLine.substring(
							nextLine.indexOf("Professionalit\"at:"),
							nextLine.indexOf("Realismus:")));
					bogen.setPunkt(Bogen.I_PROFESSIONALITAET, punkt);
				}
				if (nextLine.contains("Realismus:")) {
					punkt = getValue(nextLine.substring(nextLine
							.indexOf("Realismus:")));
					bogen.setPunkt(Bogen.I_REALISMUS, punkt);
				} else if (nextLine
						.contains("Auff\"allige Fehler (technisch): ")) {
					tech_errors = nextLine
							.substring(
									nextLine.indexOf("Auff\"allige Fehler (technisch): ") + 32)
							.trim();
					if (!nextLine
							.contains("Auff\"allige Fehler (inhaltlich-logisch): ")) {
						while ((nextLine = pdftext.readLine()) != null) {
							if (nextLine
									.contains("Auff\"allige Fehler (inhaltlich-logisch): ")) // this
							// is
							// the
							// beginning
							// of
							// the
							// next
							// item,
							// break
							{
								no_new_line = true;
								tech_errors = tech_errors
										.concat(" "
												+ nextLine.substring(
														0,
														nextLine.indexOf("Auff\"allige Fehler (inhaltlich-logisch): ")));
								tech_errors = tech_errors
										.replaceAll("\"A", "Ä")
										.replaceAll("\"O", "Ö")
										.replaceAll("\"U", "Ü")
										.replaceAll("\"a", "ä")
										.replaceAll("\"o", "ö")
										.replaceAll("\"u", "ü");
								break;
							} else {
								nextLine = nextLine.trim();
								tech_errors = tech_errors
										.concat(" " + nextLine);
							}
						}
					} else {
						tech_errors = nextLine
								.substring(
										nextLine.indexOf("Auff\"allige Fehler (technisch): ") + 32,
										nextLine.indexOf("Auff\"allige Fehler (inhaltlich-logisch): "))
								.trim();
						story_errors = nextLine
								.substring(
										nextLine.indexOf("Auff\"allige Fehler (inhaltlich-logisch): ") + 41)
								.trim();
						while ((nextLine = pdftext.readLine()) != null) {
							if (nextLine
									.contains("Auff\"allige Fehler (\"wissenschaftlich\", z.B.: historisch, physikalisch, usw.): ")) // this
							// is
							// the
							// beginning
							// of
							// the
							// next
							// item,
							// break
							{
								no_new_line = true;
								break;
							}
							nextLine = nextLine.trim();
							story_errors = story_errors.concat(" " + nextLine);
						}
						story_errors = story_errors.replaceAll("\"A", "Ä")
								.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
								.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
								.replaceAll("\"u", "ü");
					}
					tech_errors = tech_errors.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
				} else if (nextLine
						.contains("Auff\"allige Fehler (inhaltlich-logisch): ")) {
					story_errors = nextLine
							.substring(
									nextLine.indexOf("Auff\"allige Fehler (inhaltlich-logisch): ") + 41)
							.trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine
								.contains("Auff\"allige Fehler (\"wissenschaftlich\", z.B.: historisch, physikalisch, usw.): ")) 
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						story_errors = story_errors.concat(" " + nextLine);
					}
					story_errors = story_errors.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
				} else if (nextLine
						.contains("Auff\"allige Fehler (\"wissenschaftlich\", z.B.: historisch, physikalisch, usw.): ")) {
					science_errors = nextLine
							.substring(
									nextLine.indexOf("Auff\"allige Fehler (\"wissenschaftlich\", z.B.: historisch, physikalisch, usw.): ") + 79)
							.trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine
								.contains("Was f\"ur ein Bild vermittelt der Film? "))
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						science_errors = science_errors.concat(" " + nextLine);
					}
					science_errors = science_errors.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
				} else if (nextLine
						.contains("Was f\"ur ein Bild vermittelt der Film?")) {
					image = nextLine
							.substring(
									nextLine.indexOf("Was für ein Bild vermittelt der Film?") + 40)
							.trim();
					if (!nextLine.contains("Handlung: ")) {
						while ((nextLine = pdftext.readLine()) != null) {
							if (nextLine.contains("Bemerkungen: "))
							{
								no_new_line = true;
								break;
							}
							if (nextLine.contains("Handlung: ")) {
								image = image
										.concat(" "
												+ nextLine.substring(
														0,
														nextLine.indexOf("Handlung: ")));
								story = nextLine.substring(
										nextLine.indexOf("Handlung: ") + 10)
										.trim();
								while ((nextLine = pdftext.readLine()) != null) {
									if (nextLine.contains("Bemerkungen: ")) 
									{
										no_new_line = true;
										break;
									}
									nextLine = nextLine.trim();
									story = story.concat(" " + nextLine);
								}
								story = story.replaceAll("\"A", "Ä")
										.replaceAll("\"O", "Ö")
										.replaceAll("\"U", "Ü")
										.replaceAll("\"a", "ä")
										.replaceAll("\"o", "ö")
										.replaceAll("\"u", "ü");
								break;
							} else {
								nextLine = nextLine.trim();
								image = image.concat(" " + nextLine);
							}
						}
					} else
						image = nextLine
								.substring(
										nextLine.indexOf("Was für ein Bild vermittelt der Film?") + 40,
										nextLine.indexOf("Handlung: ")).trim();
					{
						story = nextLine.substring(
								nextLine.indexOf("Handlung: ") + 10).trim();
						while ((nextLine = pdftext.readLine()) != null) {
							if (nextLine.contains("Bemerkungen: "))
							{
								no_new_line = true;
								break;
							}
							nextLine = nextLine.trim();
							story = story.concat(" " + nextLine);
						}
						story = story.replaceAll("\"A", "Ä")
								.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
								.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
								.replaceAll("\"u", "ü");
					}
					image = image.replaceAll("\"A", "Ä").replaceAll("\"O", "Ö")
							.replaceAll("\"U", "Ü").replaceAll("\"a", "ä")
							.replaceAll("\"o", "ö").replaceAll("\"u", "ü");
				} else if (nextLine.contains("Bemerkungen: ")) {
					comments = nextLine.substring(
							nextLine.indexOf("Bemerkungen: ") + 13).trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine.contains("Zitate: "))
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						comments = comments.concat(" " + nextLine);
					}
					comments = comments.replaceAll("\"A", "Ä")
							.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
							.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
							.replaceAll("\"u", "ü");
				} else if (nextLine.contains("Zitate: ")) {
					String tmp = nextLine;
					while ((nextLine = pdftext.readLine()) != null) {
						// quotes.add(nextLine.trim()); // not the right
						// quote handling! just a quick & dirty solution
						// that should be suitable for at least 50%
						tmp = tmp + nextLine.trim();
					}
					tmp = tmp.substring(tmp.indexOf("Zitate: ") + 8).trim()
							.replace("", "");
					while (tmp.contains(";") == true)
					{
						bogen.getZitate().add(
								new QualifiedString(0, tmp.substring(0,
										tmp.indexOf(";") + 1), Zitat.TYPES));
						tmp = tmp.substring(tmp.indexOf(";") + 1);
					}
					if (tmp != "") {
						bogen.getZitate().add(
								new QualifiedString(0, tmp, Zitat.TYPES));
					}
					for (int i = 0; i < quotes.size(); i++) {
						quotes.set(i, quotes.get(i).replaceAll("\"A", "Ä")
								.replaceAll("\"O", "Ö").replaceAll("\"U", "Ü")
								.replaceAll("\"a", "ä").replaceAll("\"o", "ö")
								.replaceAll("\"u", "ü"));
					}
				}
			} // endif
		} // endwhile
		bogen.setText(Bogen.I_TECHNISCH, tech_errors);
		bogen.setText(Bogen.I_INHALT, story_errors);
		bogen.setText(Bogen.I_WISSENSCHAFT, science_errors);
		bogen.setText(Bogen.I_BILD, image);
		bogen.setText(Bogen.I_HANDLUNG, story);
		bogen.setText(Bogen.I_BEMERKUNGEN, (comments == null) ? "" : comments);
		pdftext.close();
		return bogen;
	}

	public static Bogen evaluateOldFileFormat(File pdfIn)
			throws FileNotFoundException, IOException, Exception {
		Bogen bogen = new Bogen();

		try {
			Process p;
			if (windows) {
				System.out.println("Executing \"pdftotext -enc UTF-8 -layout "
						+ pdfIn.toString() + "\"...");
				p = Runtime.getRuntime().exec(
						"pdftotext.exe -enc UTF-8 -layout " + pdfIn.toString());
				p.waitFor();
			} else {
				System.out.println("Executing \"pdftotext -layout "
						+ pdfIn.toString() + "\"...");
				p = Runtime.getRuntime().exec(
						"pdftotext -layout " + pdfIn.toString());
				p.waitFor(); // we have to wait for the process to finish or
				// else the file opened in the next try block
				// may not yet be created
			}
			// call pdftotext w/ the given input file to get a text file w/ the
			// pdf contents
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.out
					.print("You need pdftotext to run this program. For most linux distributions your package manager should be able to install it.\n"
							+ "For Windows you can find it here: ftp://ftp.foolabs.com/pub/xpdf/xpdf-3.02pl4-win32.zip");
			throw e;
			// TODO throw a seperate "pdftotext not found" message - how to
			// effectively test that? Looking through the whole $PATH does not
			// seem to be
			// the most effective way
		}
		// System.out.println("Opening " +
		// files_to_convert.get(z).replace(".pdf", ".txt") + "...");
		pdftext = new BufferedReader(new FileReader(pdfIn.toString().replace(
				".pdf", ".txt")));// (pdftotext_output));

		String nextLine = "";

		boolean no_new_line = false; // if we read too much while parsing we
		// don't need to read a new line in the
		// while statement
		while (no_new_line || (nextLine = pdftext.readLine()) != null) {
			no_new_line = false;
			if (nextLine != "") // we read a non-empty line, now we check
			// out if we need any infos out of it
			{
				if (nextLine.contains("Name: ") || nextLine.contains("Name:")) // different
				// encoding? : and :
				// are not equal -
				// same problems
				// below
				{
					bogen.setText(
							Bogen.I_TITEL,
							nextLine.substring(nextLine.indexOf("Name") + 6,
									nextLine.lastIndexOf("Land")).trim());
				}
				if (nextLine.contains("Land: ") || nextLine.contains("Land:")) // fill
																				// in
																				// country
				{
					bogen.setText(
							Bogen.I_LAND,
							nextLine.substring(nextLine.lastIndexOf("Land") + 6)
									.trim());
				}
				if (nextLine.contains("Originaltitel: ")
						|| nextLine.contains("Originaltitel:")) // fill in
				// original_name
				{
					// System.out.println(nextLine);
					bogen.setText(
							Bogen.I_ORIGINALTITEL,
							nextLine.substring(
									nextLine.indexOf("Originaltitel") + 15,
									nextLine.lastIndexOf("Jahr")).trim());
				}
				if (nextLine.contains("Jahr: ") || nextLine.contains("Jahr:")) // fill
																				// in
																				// year
				{
					bogen.setText(
							Bogen.I_JAHR,
							nextLine.substring(nextLine.indexOf("Jahr") + 6,
									nextLine.indexOf("FSK")).trim());
				}
				if (nextLine.contains("FSK: ") || nextLine.contains("FSK:")) {
					bogen.setText(Bogen.I_FSK,
							nextLine.substring(nextLine.lastIndexOf("FSK") + 5)
									.trim());
				}
				if (nextLine.contains("Genre: ") || nextLine.contains("Genre:")) {
					int tmp = nextLine.indexOf("hoch");
					if (tmp == -1) // as far as I know only the case for
					// Ijon Tichy (for the old files!)...
					{
						tmp = nextLine.indexOf("Jahr");
						if (tmp == -1) // files is created by bmoviereviewer
						// program
						{
							bogen.setText(
									Bogen.I_GENRE,
									nextLine.substring(
											nextLine.indexOf("Genre") + 7)
											.trim());
						} else {
							bogen.setText(
									Bogen.I_GENRE,
									nextLine.substring(
											nextLine.indexOf("Genre") + 7, tmp)
											.trim());
						}
					} else {
						bogen.setText(
								Bogen.I_GENRE,
								nextLine.substring(
										nextLine.indexOf("Genre") + 7,
										nextLine.indexOf("hoch")).trim());
					}
				}
				if (nextLine.contains("Unterhaltungswert:")) {
					bogen.setPunkt(Bogen.I_UNTERHALTUNGSWERT,
							getValue(nextLine));
				}
				if (nextLine.contains("Pornofaktor:")) {
					bogen.setPunkt(Bogen.I_PORNOFAKTOR, getValue(nextLine));
				}
				if (nextLine.contains("Gewaltdarstellung")) {
					bogen.setPunkt(Bogen.I_GEWALTDARSTELLUNG,
							getValue(nextLine));
				}
				if (nextLine.contains("Gewaltverherrlichung:")) {
					bogen.setPunkt(Bogen.I_GEWALTVERHERRLICHUNG,
							getValue(nextLine));
				}
				if (nextLine.contains("Niveau (B­Movie berücksichtigt):")
						|| (nextLine.contains("Niveau (B") && nextLine
								.contains("Movie berücksichtigt)"))) {
					bogen.setPunkt(Bogen.I_NIVEAU, getValue(nextLine));
				}
				if (nextLine.contains("Sexismus:")) {
					bogen.setPunkt(Bogen.I_SEXISMUS, getValue(nextLine));
				}
				if (nextLine.contains("Professionalität:")) {
					bogen.setPunkt(Bogen.I_PROFESSIONALITAET,
							getValue(nextLine));
				}
				if (nextLine.contains("Realismus:")) {
					bogen.setPunkt(Bogen.I_REALISMUS, getValue(nextLine));
				} else if (nextLine.contains("Auffällige Fehler (technisch): ")
						|| nextLine.contains("Auffällige Fehler (technisch):")) {
					tech_errors = nextLine
							.substring(
									nextLine.indexOf("Auffällige Fehler (technisch): ") + 31)
							.trim();
					while ((nextLine = pdftext.readLine()) != null)
					{
						if (nextLine
								.contains("Auffällige Fehler (inhaltlich-logisch): ")
								|| nextLine
										.contains("Auffällige Fehler (inhaltlich­logisch):"))
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						tech_errors = tech_errors.concat(" " + nextLine);
					}
					tech_errors = tech_errors.replace("", "");
					tech_errors = tech_errors.replaceAll("\\u00A0", " ");
				} else if (nextLine
						.contains("Auffällige Fehler (inhaltlich-logisch): ")
						|| nextLine
								.contains("Auffällige Fehler (inhaltlich­logisch):")) {
					story_errors = nextLine
							.substring(
									nextLine.indexOf("Auffällige Fehler (inhaltlich") + 40)
							.trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine
								.contains(", z.B.: historisch, physikalisch, usw.)")
								|| nextLine
										.contains("uffällige Fehler („wissenschaftlich“, z.B.: historisch, physikalisch, usw.):"))
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						story_errors = story_errors.concat(" " + nextLine);
					}
					story_errors = story_errors.replace("", "");
					story_errors = story_errors.replaceAll("\\u00A0", " ");
				} else if (nextLine
						.contains("Auffällige Fehler („wissenschaftlich“, z.B.: historisch, physikalisch, usw.):")
						|| (nextLine.contains("Auffällige Fehler (") && nextLine
								.contains(", z.B.: historisch, physikalisch, usw.): "))) {
					int tmp = nextLine
							.indexOf("Auffällige Fehler („wissenschaftlich“, z.B.: historisch, physikalisch, usw.):") + 77;
					if (tmp == 76) {
						tmp = nextLine
								.indexOf(", z.B.: historisch, physikalisch, usw.)") + 41;
					}
					science_errors = nextLine.substring(tmp).trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine
								.contains("Was für ein Bild vermittelt der Film")
								|| nextLine
										.contains("Was für ein Bild vermittelt der Film?")
								|| nextLine.contains("Story: ")
								|| nextLine.contains("Bemerkungen:")) {
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						science_errors = science_errors.concat(" " + nextLine);
					}
					science_errors = science_errors.replace("", "");
					science_errors = science_errors.replaceAll("\\u00A0", " ");
				} else if (nextLine
						.contains("Was für ein Bild vermittelt der Film")
						|| nextLine
								.contains("Was für ein Bild vermittelt der Film?")) {
					int tmp = nextLine
							.indexOf("Was für ein Bild vermittelt der Film?") + 37;
					if (tmp == 36)
						tmp = nextLine
								.indexOf("Was für ein Bild vermittelt der Film?") + 37;
					image = nextLine.substring(tmp).trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine.contains("Bemerkungen: ")
								|| nextLine.contains("Bemerkungen:")
								|| nextLine.contains("Handlung: ")
								|| nextLine.contains("Story: ")) { 
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						image = image.concat(" " + nextLine);
					}
					image = image.replace("", "");
					image = image.replaceAll("\\u00A0", " ");
				} else if (nextLine.contains("Handlung: ")
						|| (nextLine.contains("Story: ") && !nextLine
								.contains("Bemerkungen: "))) { 
					int tmp = nextLine.indexOf("Handlung: ") + 10; // sorted
					// out
					// manually
					// :-(
					if (tmp == 9) {
						tmp = nextLine.indexOf("Story: ") + 7;
					} // story summary may start with "Story:" and not with
						// "Handlung:"
					story = nextLine.substring(tmp).trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine.contains("Bemerkungen: ")
								|| nextLine
										.contains("Was für ein Bild vermittelt der Film?")) {
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						story = story.concat(" " + nextLine);
					}
					story = story.replace("", "");
					story = story.replaceAll("\\u00A0", " ");
				} else if (nextLine.contains("Bemerkungen: ")
						|| nextLine.contains("Bemerkungen:")) {
					comments = nextLine.substring(
							nextLine.indexOf("Bemerkungen: ") + 13).trim();
					while ((nextLine = pdftext.readLine()) != null) {
						if (nextLine.contains("Zitate: "))
						{
							no_new_line = true;
							break;
						}
						nextLine = nextLine.trim();
						comments = comments.concat(" " + nextLine);
					}
					comments = comments.replace("", "");
					comments = comments.replaceAll("\\u00A0", " ");
					// TODO check if it's possible to sort out quotes
					// => does not seem that way, quotation marks are widely
					// used not only for quotes, I cant think of any way to
					// sort out quotes that
					// would not be pretty error prone
				} else if (nextLine.contains("Zitate: ")) {
					String tmp = nextLine;
					while ((nextLine = pdftext.readLine()) != null) {
						tmp = tmp + nextLine.trim();
					}
					tmp = tmp.substring(tmp.indexOf("Zitate: ") + 8).trim()
							.replaceAll("", "").replaceAll("\\u00A0", " ");
					while (tmp.contains(";") == true) // semicolon usually
					// seperates two
					// quotes
					{
						bogen.getZitate().add(
								new QualifiedString(0, tmp.substring(0,
										tmp.indexOf(";") + 1), Zitat.TYPES));
						tmp = tmp.substring(tmp.indexOf(";") + 1);
					}
					if (tmp != "") {
						bogen.getZitate().add(
								new QualifiedString(0, tmp, Zitat.TYPES));
					}
				}

			} // endif
		} // endwhile
		bogen.setText(Bogen.I_TECHNISCH, tech_errors);
		bogen.setText(Bogen.I_INHALT, story_errors);
		bogen.setText(Bogen.I_WISSENSCHAFT, science_errors);
		bogen.setText(Bogen.I_BILD, image);
		bogen.setText(Bogen.I_HANDLUNG, story);
		bogen.setText(Bogen.I_BEMERKUNGEN, (comments == null) ? "" : comments);
		pdftext.close();

		// delete temporary file created by pdftotext
		return bogen;
	}

	public static int getValue(String string) {
		if (old_eval_file) {
			int value = 1;
			int tmp = string.indexOf("●");
			if (tmp == -1) {
				System.err.println("Value not set!");
				return 0;
			}
			String tmp_string = string.substring(tmp + 1); // the substring
			// after the ●
			while (tmp_string.indexOf("○") != -1) // count how many points there
			// are *after* ●
			{
				value++;
				tmp_string = tmp_string.substring(tmp_string.indexOf("○") + 1);
			}
			return value;
		} else {
			int value = 1;
			int tmp = string.indexOf("*");
			if (tmp == -1) {
				System.err.println("Value not set!");
				return 0;
			}
			String tmp_string = string.substring(tmp + 1); // the substring
			// after the ●
			while (tmp_string.indexOf("ffi") != -1) // count how many points
			// there are *after* ●
			{
				value++;
				tmp_string = tmp_string
						.substring(tmp_string.indexOf("ffi") + 3);
			}
			return value;
		}
	}

	public static void write(Bogen bogen, String texFile) {
		PrintStream out = null;
		AppLogger.info(texFile + " wird konvertiert");
		if (bogen == null || texFile == null) {
			throw new IllegalArgumentException();
		}

		try {
			out = new PrintStream(new FileOutputStream(texFile));
		} catch (FileNotFoundException e) {
			AppLogger.severe("Kann Datei " + texFile + " nicht oeffnen");
			return;
		}

		Tex texFormatter = new Tex(bogen);
		try {
			texFormatter.write(out);
		} catch (IOException e) {
			AppLogger.warning(e.getMessage());
		} finally {
			out.close();
		}

		// und mit PDFLatex behandeln
		try {
			AppLogger.info("Starte "
					+ Globals.getInstance().getProperty("texcommand") + " "
					+ texFile);

			Process p = Runtime.getRuntime().exec(
					Globals.getInstance().getProperty("texcommand") + " "
							+ texFile);
			p.waitFor();
			AppLogger.info("PDF geschrieben");
		} catch (Exception e) {
			AppLogger
					.severe("Fehler beim Ausfuehren von PDFLatex! (PDFLatex nicht vorhanden? bmovie.sty nicht im Suchpfad?)");
			return;
		}
	}
}
