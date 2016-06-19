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

import java.io.OutputStream;
import java.lang.reflect.Constructor;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import data.Globals;


/**
 * Hilfsmethoden zum Lesen und Schreiben von XML-Dateien
 * @author mibeer
 *
 */
public class XML {

	// ////////////////////////////////////////////
	// Konstruktoren

	/**
	 * Erstellt Hilfskobjekt zum Lesen aus Stream
	 */
	public XML(XMLEventReader reader) {
		if (reader == null) {
			throw new IllegalArgumentException();
		}

		this.reader = reader;
	}

	/**
	 * Erstellt Hilfsobjekt zum Schreiben in Stream
	 * 
	 * @param writer
	 */
	public XML(XMLStreamWriter writer) {
		if (writer == null) {
			throw new IllegalArgumentException();
		}

		this.writer = writer;
	}

	public XMLEventReader getReader() {
		return reader;
	}

	public XMLStreamWriter getWriter() {
		return writer;
	}

	public XMLEventFactory getEventFactory() {
		return eventFactory;
	}

	public String readText(String closingTag) throws XMLStreamException {
		String str = "", tag = "";
		XMLEvent event = null;
		if (reader == null) {
			throw new NullPointerException();
		}

		while (reader.hasNext() && !tag.equals(closingTag)) {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				throw new XMLStreamException("Textfeld " + closingTag
						+ " darf keine Untertags enthalten");
			}
			if (event.isEndElement()) {
				tag = event.asEndElement().getName().getLocalPart();
				if (!tag.equals(closingTag)) {
					throw new XMLStreamException("Textfeld " + closingTag
							+ " nicht wohlgeformt");
				}
			}
			if (event.isCharacters()) {
				String np = (event.asCharacters().getData());
				str = str + np.replaceAll("[\r\n]", " ").trim();
			}
		}

		return str;
	}

	@SuppressWarnings("unchecked")
	public static XMLStreamWriter getXMLStreamWriter(OutputStream out)
			throws XMLStreamException {
		XMLStreamWriter writer = null;
		XMLOutputFactory xof = XMLOutputFactory.newInstance();

		String encoding = Globals.getInstance().getProperty("encoding");
		try { // es wird versucht, eine formatierte Ausgabe durchzufuehren
			Class<XMLStreamWriter> indentingWriter = (Class<XMLStreamWriter>) Class
					.forName("javanet.staxutils.IndentingXMLStreamWriter");
			Constructor<XMLStreamWriter> constr = indentingWriter
					.getConstructor(XMLStreamWriter.class);
			writer = constr.newInstance(xof.createXMLStreamWriter(out, encoding));
		} catch (Exception e) {
			writer = xof.createXMLStreamWriter(out, encoding);
			AppLogger.warning("stax-utils konnte nicht geladen werden - XML-Ausgabe nicht formatiert: "
							+ e.toString());
		}
		return writer;
	}

	

	public void printOpeningTag(String tag) throws XMLStreamException {
		writer.writeStartElement(tag);
	}

	public void printClosingTag(String tag) throws XMLStreamException {
		writer.writeEndElement();
	}
	
	
	public void writeText(String tag, String value) throws XMLStreamException{
		printOpeningTag(tag);
		writer.writeCharacters(value);
		printClosingTag(tag);
	}
	
	protected XMLEventReader reader;
	protected XMLStreamWriter writer;
	protected XMLEventFactory eventFactory;
	
}
