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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.Link;
import data.QualifiedString;
import data.Zitat;
import data.wrappers.QualifiedStringList;

public class XML extends BogenFormat {

	public static int FORMAT_NEU = 1;
	public static int FORMAT_ALT = 0;

	public static final String VERSION = "1.0.0";

	public static final String ZITAT = "zitat";
	public static final String ZITAT_TYP = "wertung";

	public static final String ZITATE = "zitate";

	public static final String LINK = "link";

	public static final String LINKS = "links";

	public static final String LINKS_TYP = "typ";

	public static final String VIDEOS = "videos";
	public static final String VIDEO = "video";
	public static final String VIDEO_TYP = "typ";

	public static final String PUNKTWERTUNGEN = "punktwertungen";

	public static final String DETAILS = "details";

	public static final String TEXTFELDER = "textfelder";

	public static final String BMOVIE = "bmovie";

	public static final String PUNKT_VAL = "val";

	public static final String PUNKT_ANMERKUNG = "note";

	public static final String COVER = "cover";

	public static final String XLS_LINE = "xml-stylesheet type=\"text/xsl\" href=\"bmr.xsl\" ";

	// ///////////////////////////////////////////////////////////////
	// public Methoden

	public static int getXmlFormat() {
		return Globals.getInstance().getPropertyAsInt("xmlformat");
	}

	public static void writeDTD(OutputStream out) throws IOException {
		PrintStream printStream = new PrintStream(out);
		try {
			DTD dtd = new DTD(printStream);
			dtd.dtdToStream();
		} finally {
			printStream.close();
		}
	}

	public static void writeXLS(OutputStream out) throws IOException {
		PrintStream printStream = new PrintStream(out);
		try {
			XLS xls = new XML.XLS(printStream);
			xls.xlsToStream();
		} finally {
			printStream.close();
		}
	}

	public XML(Bogen bogen) {
		super(bogen);
		this.format = XML.getXmlFormat();
	}

	@Override
	public String getFormatExtension() {
		return "xml";
	}

	@Override
	public FileNameExtensionFilter getFileNameExtensionFilter() {
		return new FileNameExtensionFilter("XML-Dateien", getFormatExtension());
	}

	// *************************************************************************
	// INTERNALS
	// *************************************************************************

	// ///////////////////////////////////////////////////////////
	// Schreiben von XML

	@Override
	protected void write() {
		try {
			this.writer = tools.XML.getXMLStreamWriter(out);
			this.xmlHelper = new tools.XML(writer);
			try {
				this.printBMovie();
			} finally {
				this.writer.close();
			}
		} catch (XMLStreamException e) {
			AppLogger
					.warning("Beim Vorbereiten zum Schreiben des XML-Dokuments trat Fehler auf!");
			return;
		}
	}

	protected void printPunktWertung(int i) throws XMLStreamException {
		if (i >= Bogen.KATEGORIEN.length) {
			throw new IllegalArgumentException();
		}
		writer.writeStartElement(Bogen.KATEGORIEN[i]);
		writer.writeAttribute(PUNKT_VAL, Integer.toString(bogen.getPunkt(i)));
		writer.writeAttribute(PUNKT_ANMERKUNG, bogen.getAnmerkung(i));
		writer.writeEndElement();
	}

	protected void printTextFeld(int index) throws XMLStreamException {
		xmlHelper.writeText(Bogen.TEXTFELDER[index],
				textToXHTML(bogen.getText(index)));
	}

	protected void printZitat(QualifiedString zitat) throws XMLStreamException {
		writer.writeStartElement(ZITAT);
		writer.writeAttribute(ZITAT_TYP, Zitat.TYPES[zitat.getTyp()]);
		writer.writeCharacters(zitat.getText().replaceAll("\"", "\\\""));
		writer.writeEndElement();
	}

	protected void printLink(QualifiedString link) throws XMLStreamException {
		writer.writeStartElement(LINK);
		writer.writeAttribute(LINKS_TYP, Link.TYPES[link.getTyp()]);
		writer.writeCharacters(link.getText());
		writer.writeEndElement();
	}

	protected void printPunktWertungen() throws XMLStreamException {
		xmlHelper.printOpeningTag(PUNKTWERTUNGEN);
		for (int index = 0; index < Bogen.KATEGORIEN.length; index++) {
			printPunktWertung(index);
		}
		xmlHelper.printClosingTag(PUNKTWERTUNGEN);
	}

	protected void printDetails() throws XMLStreamException {
		xmlHelper.printOpeningTag(DETAILS);
		for (int index = 1; index < Bogen.I_MAX_DETAILS; index++) {
			printTextFeld(index);
		}
		xmlHelper.printClosingTag(DETAILS);
	}

	protected void printTextfelder() throws XMLStreamException {
		xmlHelper.printOpeningTag(TEXTFELDER);
		for (int index = this.textStartIndex; index < Bogen.I_MAX_TEXT; index++) {
			printTextFeld(index);
		}
		xmlHelper.printClosingTag(TEXTFELDER);
	}

	protected void printVideos() throws XMLStreamException {
		String trailer = bogen.getTrailer().getText();
		if (trailer.length() == 0)
			return;
		xmlHelper.printOpeningTag(VIDEOS);
		writer.writeStartElement(VIDEO);
		writer.writeAttribute(VIDEO_TYP, "Youtube 2");
		writer.writeCharacters(trailer);
		writer.writeEndElement();
		xmlHelper.printClosingTag(VIDEOS);
	}

	protected void printZitate() throws XMLStreamException {
		xmlHelper.printOpeningTag(ZITATE);
		Iterator<QualifiedString> it = (Iterator<QualifiedString>) bogen
				.getZitate().iterator();
		while (it.hasNext()) {
			QualifiedString zitat = it.next();
			printZitat(zitat);
		}
		xmlHelper.printClosingTag(ZITATE);
	}

	protected void printLinks() throws XMLStreamException {
		xmlHelper.printOpeningTag(LINKS);
		Iterator<QualifiedString> it = (Iterator<QualifiedString>) bogen
				.getLinks().iterator();
		while (it.hasNext()) {
			printLink(it.next());
		}
		xmlHelper.printClosingTag(LINKS);
	}

	protected void printCover() throws XMLStreamException {
		xmlHelper.printOpeningTag(COVER);
		writer.writeCharacters(bogen.getCover().getText());
		xmlHelper.printClosingTag(COVER);
	}

	protected void printBMovie() throws XMLStreamException {
		writer.writeStartDocument(
				Globals.getInstance().getProperty("encoding"), "1.0");
		if (format > 0) {
			writer.writeProcessingInstruction(XLS_LINE);
		}
		writer.writeStartElement(BMOVIE);

		if (format > 0) {
			this.textStartIndex = (format > 0) ? Bogen.I_MAX_DETAILS : 0;
			printTextFeld(Bogen.I_TITEL);
		}
		printCover();
		printLinks();
		printPunktWertungen();
		if (format > 0) {
			printDetails();
		}
		printTextfelder();
		printZitate();
		printVideos();
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	// XML Lesen...

	@SuppressWarnings("unchecked")
	protected void setzePunktWertung(XMLEvent ev, int index) {
		Iterator<Attribute> attributes = (Iterator<Attribute>) ev
				.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equals(PUNKT_VAL)) {
				bogen.setPunkt(index, Integer.parseInt(attribute.getValue()));
			} else if (attribute.getName().toString().equals(PUNKT_ANMERKUNG)) {
				bogen.setAnmerkungen(index, attribute.getValue());
			}
		}

	}

	protected void readPunktwertungen() throws XMLStreamException {
		String tag = "";
		XMLEvent event = null;
		int index = -1;

		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();
				for (int i = 0; i < Bogen.KATEGORIEN.length; i++) {
					if (tag.equals(Bogen.KATEGORIEN[i])) {
						index = i;
					}
				}
				if (index > -1) {
					setzePunktWertung(event, index);
				} else {
					throw new XMLStreamException("   Zeile " + tag
							+ " nicht wohlgeformt");
				}
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(PUNKTWERTUNGEN)) {
					return;
				} else {
					if (index > -1 && tag.equals(Bogen.KATEGORIEN[index])) {
						// nichts zu tun, tag wohlgeformt
						index = -1;
					} else {
						throw new XMLStreamException("    " + tag
								+ " unbekannt");
					}
				}
			}
		}
	}

	protected void readTextfelder() throws XMLStreamException {
		String tag = "", str;
		XMLEvent event = null;
		int index = -1;
		int startIndex = (this.format == FORMAT_NEU) ? Bogen.I_MAX_DETAILS : 0;
		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();
				for (int i = startIndex; i < Bogen.TEXTFELDER.length; i++) {
					if (tag.equals(Bogen.TEXTFELDER[i])) {
						index = i;
					}
				}
				if (index > -1) {
					str = xmlHelper.readText(Bogen.TEXTFELDER[index]);
					bogen.setText(index, str);
				} else {
					throw new XMLStreamException("   Zeile " + tag
							+ " nicht wohlgeformt");
				}
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(TEXTFELDER)) {
					return;
				} else if (index > -1 && tag.equals(Bogen.TEXTFELDER[index])) {
					// nichts zu tun, tag wohlgeformt
					index = -1;
				} else {
					throw new XMLStreamException("   " + tag + " unbekannt");
				}
			}
		}
	}

	protected void readDetails() throws XMLStreamException {
		String tag = "", str;
		XMLEvent event = null;
		int index = -1;

		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();
				for (int i = 1; i < Bogen.I_MAX_DETAILS; i++) {
					if (tag.equals(Bogen.TEXTFELDER[i])) {
						index = i;
					}
				}
				if (index > -1) {
					str = xmlHelper.readText(Bogen.TEXTFELDER[index]);
					bogen.setText(index, str);
				} else {
					throw new XMLStreamException("   Zeile " + tag
							+ " nicht wohlgeformt");
				}
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(DETAILS)) {
					return;
				} else if (index > -1 && tag.equals(Bogen.TEXTFELDER[index])) {
					// nichts zu tun, tag wohlgeformt
					index = -1;
				} else {
					throw new XMLStreamException("   " + tag + " unbekannt");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void readQualifiedStrings(String tagName, String typName,
			String[] types, QualifiedStringList list, String endTagName)
			throws XMLStreamException {
		String tag = "", str;
		XMLEvent event = null;
		String typ = "";
		int index = 0;

		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();
				if (tag.equals(tagName)) {
					Iterator<Attribute> attributes = (Iterator<Attribute>) event
							.asStartElement().getAttributes();
					typ = types[0];
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equals(typName)) {
							typ = attribute.getValue();
						}
					}
					str = xmlHelper.readText(tagName);
					for (index = 0; index < types.length; index++) {
						if (types[index].equals(typ)) {
							list.add(new QualifiedString(index, str, types));
						}
					}
				} else {
					throw new XMLStreamException("   Zeile " + tag
							+ " nicht wohlgeformt");
				}
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(endTagName)) {
					return;
				} else {
					throw new XMLStreamException("   " + tag + " unbekannt");
				}
			}
		}
	}

	protected void readZitate() throws XMLStreamException {
		readQualifiedStrings(ZITAT, ZITAT_TYP, Zitat.TYPES, bogen.getZitate(),
				ZITATE);
	}

	protected void readLinks() throws XMLStreamException {
		readQualifiedStrings(LINK, LINKS_TYP, Link.TYPES, bogen.getLinks(),
				LINKS);
	}

	protected void readVideos() throws XMLStreamException {
		String tag;
		XMLEvent event;
		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();
				if (!tag.equals(VIDEO)) {
					throw new XMLStreamException("   " + tag + " ist unbekannt");
				}
				String str = xmlHelper.readText(tag);
				bogen.setTrailer(str);
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(VIDEOS)) {
					return;
				} else {
					throw new XMLStreamException("   " + tag + " unbekannt");
				}
			}
		}
	}

	protected void readBMovie() throws XMLStreamException {
		String tag;
		XMLEvent event = null;

		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				tag = event.asStartElement().getName().getLocalPart();

				if (tag.equals(Bogen.TEXTFELDER[Bogen.I_TITEL])) {
					bogen.setText(Bogen.I_TITEL,
							xmlHelper.readText(Bogen.TEXTFELDER[Bogen.I_TITEL]));
				}
				if (tag.equals(ZITATE)) {
					readZitate();
				}
				if (tag.equals(PUNKTWERTUNGEN)) {
					readPunktwertungen();
				}
				if (tag.equals(TEXTFELDER)) {
					readTextfelder();
				}
				if (tag.equals(DETAILS)) {
					readDetails();
				}
				if (tag.equals(LINKS)) {
					readLinks();
				}
				if (tag.equals(COVER)) {
					bogen.getCover().setText(xmlHelper.readText(COVER));
					// Und das Cover einlesen

				}
				if (tag.equals(VIDEOS)) {
					readVideos();
				}
			} else if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (tag.equals(BMOVIE)) {
					return;
				}
			}
		}
	}

	protected void read() throws IOException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			this.reader = inputFactory.createXMLEventReader(in, Globals
					.getInstance().getProperty("encoding"));
			this.xmlHelper = new tools.XML(this.reader);
			try {
				this.readFromXmlEventReader();
			} finally {
				reader.close();
				reader = null;
			}
		} catch (XMLStreamException e) {
			AppLogger.warning("Fehler mit XMLStream: " + e.toString());
			throw new IOException(e.getMessage());
		}
	}

	protected void readFromXmlEventReader() throws IOException {
		XMLEvent event = null;

		try {
			while (reader.hasNext()) {
				event = reader.nextEvent();
				if (event.isProcessingInstruction()) {
					this.format = FORMAT_NEU; // das identifiziert das neue
												// Format
				}
				if (event.isStartElement()
						&& event.asStartElement().getName().getLocalPart()
								.equals(BMOVIE)) {
					readBMovie();
					if (reader.hasNext()) {
						event = reader.nextEvent();
						if (event.isEndDocument()) {
						}
					}
				}
			}
		} catch (XMLStreamException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	// //////////////////////////////////////////////////////
	// Drucken der DTD

	protected static class DTD {

		public DTD(PrintStream printStream) {
			this.printStream = printStream;
		}

		protected void printEntity(String entity, String cont) {
			if (entity == null) {
				throw new IllegalArgumentException();
			}
			printStream.print("<!ENTITY " + entity);
			if (cont != null) {
				printStream.print(" (" + cont + ")");
			}
			printStream.println(" >");
		}

		protected void printAtt(String tag) {
			printStream.println("<!ATTLIST " + tag);
		}

		protected void printAttribute(String name, String type, String def) {
			printStream.println("   " + name + " " + type + " " + def);
		}

		protected void printAllgDTD() {
			for (int i = 0; i < Bogen.erstesTextfeld; i++) {
				printEntity(Bogen.TEXTFELDER[i], "#PCDATA");
			}
		}

		protected void printPunktwertungenDTD() {
			String kateg = "";
			if (Bogen.KATEGORIEN.length > 0) {
				kateg = Bogen.KATEGORIEN[0];
				for (int i = 1; i < Bogen.KATEGORIEN.length; i++) {
					kateg += "," + Bogen.KATEGORIEN[i];
				}
			}

			printEntity(PUNKTWERTUNGEN, kateg);

			for (int i = 0; i < Bogen.KATEGORIEN.length; i++) {
				printEntity(Bogen.KATEGORIEN[i], "EMPTY");
				printAtt(Bogen.KATEGORIEN[i]);
				printAttribute(PUNKT_VAL, "CDATA", "\"0\"");
				printAttribute(PUNKT_ANMERKUNG, "CDATA", "\"\"");
				printStream.println(">");
				printStream.println();
			}

			printStream.println();
		}

		protected void printTextfelderDTD() {
			String felder = "";
			if (Bogen.TEXTFELDER.length > Bogen.erstesTextfeld) {
				felder = Bogen.TEXTFELDER[Bogen.erstesTextfeld];
				for (int i = Bogen.erstesTextfeld + 1; i < Bogen.TEXTFELDER.length; i++) {
					felder += "," + Bogen.TEXTFELDER[i];
				}
			}

			printEntity(PUNKTWERTUNGEN, felder);
			for (int i = Bogen.erstesTextfeld; i < Bogen.TEXTFELDER.length; i++) {
				printEntity(Bogen.TEXTFELDER[i], "#PCDATA");
			}

		}

		protected void printZitateDTD() {
			printEntity(ZITATE, ZITAT + '*');
			printEntity(ZITAT, "#PCDATA");
		}

		protected void printLinksDTD() {
			printEntity(LINKS, LINK + '*');
			printEntity(LINK, "#PCDATA");
			printAtt(LINK);
			printAttribute(LINKS_TYP, "CDATA", "#REQUIRED");
			printStream.println(" >");
		}

		protected void printCoverDTD() {
			printEntity(COVER, "#PCDATA");
		}

		protected void dtdToStream() {
			String allg = "";
			for (int i = 0; i < Bogen.erstesTextfeld; i++) {
				allg += Bogen.TEXTFELDER[i] + ",";
			}
			printStream.println("<!-- BMovie  version " + VERSION + " DTD -->");

			printEntity("bmovie", allg + PUNKTWERTUNGEN + "," + TEXTFELDER
					+ "," + ZITATE + "," + LINKS + "," + COVER);
			printAllgDTD();
			printPunktwertungenDTD();
			printTextfelderDTD();
			printZitateDTD();
			printLinksDTD();
			printCoverDTD();
		}

		PrintStream printStream;
	}

	// ///////////////////////////////////////////////////////////////////
	// Ausgabe der xls

	protected static class XLS {

		public XLS(PrintStream printStream) {
			this.printStream = printStream;
		}

		protected String textfieldToXLS(String tag, int i) {
			return "<xsl:template match=\"" + tag + "/" + Bogen.TEXTFELDER[i]
					+ "\">\n" + "<p>\n" + "<b>" + Bogen.TEXTFELD_NAMEN[i]
					+ ": </b><xsl:value-of select=\".\"/>\n" + "</p>\n"
					+ "</xsl:template>";
		}

		protected String detailToXLS(String tag, int i) {
			return "<xsl:template match=\"" + tag + "/" + Bogen.TEXTFELDER[i]
					+ "\">\n" + "<b>" + Bogen.TEXTFELD_NAMEN[i]
					+ ": </b><xsl:value-of select=\".\"/>\n"
					+ "</xsl:template>";
		}

		protected void xlsToStream() {
			printStream
					.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
							+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
							+ "<xsl:template match=\"/\">\n"
							+ "<html><head></head><body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FF0000\" vlink=\"#AA0000\" alink=\"#000000\" style=\"font-family:Arial; font-size:13px;\">\n"
							+ "<xsl:apply-templates />\n" + "</body></html>\n"
							+ "</xsl:template>\n" + "\n" + "\n"
							+ "<xsl:template match=\""
							+ Bogen.TEXTFELDER[Bogen.I_TITEL] + "\">\n"
							+ "<center>\n"
							+ "<h1><xsl:value-of select=\".\" /></h1>\n"
							+ "</center>\n" + "</xsl:template>\n");

			printStream
					.println("<xsl:template match=\""
							+ COVER
							+ "\">\n"
							+ "<center>\n"
							+ "<img><xsl:attribute name=\"src\"><xsl:value-of select=\".\" /></xsl:attribute>\n"
							+ "<xsl:attribute name=\"alt\"><xsl:value-of select=\".\" /></xsl:attribute>\n"
							+ "<xsl:value-of select=\".\" /></img>\n"
							+ "</center>\n\n" + "</xsl:template>\n");

			printStream.println("<xsl:template match=\"" + LINKS + "\">\n"
					+ "<center><table><tr>\n" + "<xsl:apply-templates/>\n"
					+ "</tr></table></center>\n" + "</xsl:template>\n");

			printStream
					.println("<xsl:template match=\""
							+ LINK
							+ "\">\n"
							+ "<td><a><xsl:attribute name=\"href\"><xsl:value-of select=\".\" /></xsl:attribute>\n"
							+ "<xsl:value-of select=\"@typ\" /></a></td>\n"
							+ "</xsl:template>\n");

			printStream.println("<xsl:template match=\"" + ZITATE + "\">\n"
					+ "<p>\n" + "<b>Zitate: </b>\n" + "<ul>\n"
					+ "<xsl:apply-templates/>\n" + "</ul>\n" + "</p>\n"
					+ "</xsl:template>");

			printStream.println("<xsl:template match=\"" + ZITAT + "\">\n"
					+ "<li> <xsl:value-of select=\".\"/></li>\n"
					+ "</xsl:template>");

			printStream.println("<xsl:template match=\"" + TEXTFELDER + "/"
					+ Bogen.TEXTFELDER[Bogen.I_RSS] + "\">\n" + "<p>\n"
					+ "<center>\n" + "<b><xsl:value-of select=\".\"/></b>\n"
					+ "</center>\n" + "</p>\n" + "</xsl:template>");

			printStream.println("<xsl:template match=\"" + DETAILS + "\">\n"
					+ "<center>\n" + "<xsl:apply-templates/>\n" + "</center>\n"
					+ "</xsl:template>\n");

			printStream.println(textfieldToXLS(TEXTFELDER, Bogen.I_HANDLUNG));
			printStream.println(textfieldToXLS(TEXTFELDER, Bogen.I_TECHNISCH));
			printStream
					.println(textfieldToXLS(TEXTFELDER, Bogen.I_WISSENSCHAFT));
			printStream.println(textfieldToXLS(TEXTFELDER, Bogen.I_INHALT));
			printStream.println(textfieldToXLS(TEXTFELDER, Bogen.I_BILD));
			printStream
					.println(textfieldToXLS(TEXTFELDER, Bogen.I_BEMERKUNGEN));

			printStream.println(detailToXLS(DETAILS, Bogen.I_ORIGINALTITEL));
			printStream.println(detailToXLS(DETAILS, Bogen.I_JAHR));
			printStream.println(detailToXLS(DETAILS, Bogen.I_FSK));
			printStream.println(detailToXLS(DETAILS, Bogen.I_LAND));
			printStream.println(detailToXLS(DETAILS, Bogen.I_GENRE));

			printStream.println("</xsl:stylesheet>");
		}

		protected PrintStream printStream;
	}

	public static String textToXHTML(String text) {
		String xhtml = text;
		return xhtml;
	}

	protected XMLEventReader reader;

	protected tools.XML xmlHelper = null;
	protected XMLStreamWriter writer = null;

	protected XMLEventFactory eventFactory;
	protected PrintStream printStream;

	protected int textStartIndex = 0;

	protected int format = FORMAT_ALT;
}
