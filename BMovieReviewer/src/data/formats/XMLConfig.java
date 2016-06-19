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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.SortedSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import tools.AppLogger;
import tools.XML;
import data.Globals;

public class XMLConfig {

    public static final String BMOVIECREATORCONFIG = "bmoviecreatorconfig";

    // /////////////////////////////////////////////////////////
    // public Methoden
    // /////////////////////////////////////////////////////////

    /**
     * gibt template-Konfigurationsdatei aus
     * 
     * @param out
     *            Stream in den die Datei geschrieben werden soll
     */
    public static void printConfig(PrintStream out) {

        XMLConfig xmlConfig;

        try {
            xmlConfig = new XMLConfig(XML.getXMLStreamWriter(out));
            xmlConfig.printConfig();
        } catch (XMLStreamException e) {
            System.err.println("Beim Vorbereiten zum Schreiben des XML-Dokuments trat Fehler auf!");
            return;
        }
        out.flush();
    }

    public static void saveConfig(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException();
        }

        PrintStream ps = new PrintStream(new FileOutputStream(file));
        printConfig(ps);
        ps.close();
    }

    public static void loadFromStream(InputStreamReader in) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = null;
        XMLConfig xmlConfig = null;
        try {
            eventReader = inputFactory.createXMLEventReader(in);
            xmlConfig = new XMLConfig(eventReader);
            xmlConfig.read();
            eventReader.close();

        } catch (XMLStreamException e) {
            AppLogger.warning("XMLConfig::readXML: Fehler mit XMLStream: " + e.toString());
        }

    }

    public static void loadConfig() {
		Globals globs = Globals.getInstance();
		String name = globs.getProperty("configfile");
		File cfg = new File(name);
		if (cfg.exists() && cfg.isFile()) { // Configdatei wurde gefunden
			try {
				InputStream configInputStream = new FileInputStream(cfg);
				try {
					InputStreamReader configInputReader = new InputStreamReader(
							configInputStream, globs.getProperty("encoding"));
					try {
						loadFromStream(configInputReader);
					} finally {
						configInputReader.close();
					}
				} finally {
					configInputStream.close();
				}
			} catch (Exception e) {
				AppLogger.warning("Could not load external configuration file "
						+ name);
			}
		}
	}

    // //////////////////////////////////////////////////////
    // INTERNALS
    // //////////////////////////////////////////////////////

    protected XMLConfig(XMLEventReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }

        this.reader = reader;
        this.xml = new XML(reader);
    }

    protected XMLConfig(XMLStreamWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }

        this.writer = writer;
        this.xml = new XML(writer);
    }

    protected void read() throws XMLStreamException {
        XMLEvent event = null;

        while (reader.hasNext()) {
            event = reader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(BMOVIECREATORCONFIG)) {
                readConfig();
                if (reader.hasNext()) {
                    event = reader.nextEvent();
                    if (event.isEndDocument()) {
                        return;
                    }
                }
            }
        }

    }

    
    // /////////////////////////////////////////
    // Methoden zum Lesen
    
    
    protected void readConfig() throws XMLStreamException {
        String tag;

        XMLEvent event = null;

        while (reader.hasNext()) {
            event = reader.nextEvent();
            if (event.isStartElement()) {
                tag = event.asStartElement().getName().getLocalPart();
                readTag(tag);
            } else if (event.isEndElement()) {
                tag = event.asEndElement().getName().getLocalPart();
                if (tag.equals(BMOVIECREATORCONFIG)) {
                    return;
                }
            }
        }
    }

    
    protected void readTag(String tag) throws XMLStreamException {
        XMLEvent event = null;
        String nested = null;
        String value = null;

        while (reader.hasNext()) {
            event = reader.nextEvent();
            if (event.isStartElement()) {
                // eingebetteter Tag
                try {
                    nested = event.asStartElement().getName().getLocalPart();
                    readTag(tag + "." + nested);
                } catch (RuntimeException e) {
                    // Kein gueltiger Schluessel
                    System.err.println(e);
                }

            } else if (event.isEndElement()) {
                tag = event.asEndElement().getName().getLocalPart();
                if (tag.equals(tag)) {
                    return;
                }
            } else if (event.isCharacters()) {
                value = event.asCharacters().getData();
                if (!value.matches("\\s*"))
                    if (!Globals.getInstance().setProperty(tag, value)) {
                        AppLogger.warning("readTag(): " + tag + " kein gueltiger tag :: " + value);
                    }
            }
        }
    }

    // /////////////////////////////////////////
    // Methoden zum Schreiben

    protected void printConfig() throws XMLStreamException {
        SortedSet<String> keys = Globals.getInstance().sortedKeys();
        writer.writeStartDocument(Globals.getInstance().getProperty("encoding"), "1.0");
        writer.writeStartElement(BMOVIECREATORCONFIG);

        for (String key : keys) {
            writeKey(key, key);
        }

        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }

    
    protected void writeKey(String key, String fullTag) throws XMLStreamException {
        if (key.contains(".")) {
            if (key.length() <= key.indexOf(".")) {
                AppLogger.severe(key + " nicht wohlgeformt");
                return;
            }
            String head = key.substring(0, key.indexOf("."));
            String remainder = key.substring(key.indexOf(".") + 1);
            writer.writeStartElement(head);
            writeKey(remainder, fullTag);
            writer.writeEndElement();
        } else {
            xml.writeText(key, Globals.getInstance().getProperty(fullTag));
        }
    }


	XMLEventReader reader = null;
    XMLStreamWriter writer = null;

    XML xml = null;
}
