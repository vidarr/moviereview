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
package data;


import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/** 
 * Haelt alle globalen Einstellungen
 * @author mibeer
 *
 */
public class Globals{
	
	private static Globals instance = null;
	
	
	public static final int APPLET = 1;
	public static final int STAND_ALONE = 4;
	public static final int MIT_SENDEN = 8;
	public static final int MIT_PDF = 16;
	
	public static final String PROPERTY_INTERNAL_RESOURCE = "internalResource";
	
	/**
	 * Schluessel aller Eigenschaften
	 */
	public static final String[] keys = {
		"configfile",
		"server.protocoll",
		"server.name",
		"server.dir", 
		"server.postbmovie",
		"server.postxml",
		"server.posttex",
		"server.postjpeg",
		"server.postquotations",
		"mimexml",
		"mimetex",
		"jpeg.mime",
		"jpeg.id",
		"encoding",
		"coverwidth",
		"server.passwordstring",
		"server.userstring",
		"server.securitylevel",
		"server.certificate",
		"basedirectory",
		"datadirectory",
		"jpeg.quality",
		"texcommand",
		"spellcheck.mode",
		"spellcheck.dictionaries",   // pfad zur Dictionarybase
		"logourl",          // url zum Logo
		"splash.uploads",
		"splash.upload_1",
		"splash.upload_2",
		"splash.upload_3",
		"splash.upload_4",
		"splash.upload_5",
		"splash.upload_6",
		"splash.fadingtime", // Dauer des Splash-fadeouts in Millisekunden
		"iotimeout",
		"xmlformat",
		"xml.bmoviesty"
	};
	
	/**
	 * Standardwerte der einzelnen Eigenschaften
	 */
	public static final String[] stdValues = {
		"config.xml",
		"https",
		"www.bmovieprojekt.tk",
		"",
		"movie_post.php",
		"uploaded.php",
		"uploaded.php",
		"uploaded.php",
		"quote_post.php",
		"text/xml",
		"text/tex",
		"image/jpeg",
		"jpeg",
		"utf-8",
		"300",
		"authinfo",
		"login",
		"fuckit",   // server.securitylevel: fuckit / normal / paranoid
		"standard",
		".",
		"dat",
		"0.85",
		"pdflatex",
		"disabled",
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		"enabled",  // splash.upload
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		PROPERTY_INTERNAL_RESOURCE,
		"5000",
		"30000",
		"0",
		PROPERTY_INTERNAL_RESOURCE
	};
	
	/**
	 * Art des Programms
	 */
	protected static int appType = STAND_ALONE | MIT_PDF | MIT_SENDEN;
	
	/**
	 * Name der Anwendung
	 */
	public static final String APP_NAME = "BMovieReviewer"; 
	
	public static final String APP_VERSION = "0.1.0";
	
	/**
	 * Wert, auf den die Statusbar beim Eryeugen gesetzt wird
	 */
	public static String initialStatus = Globals.APP_NAME + " gestartet.";
	
	
	public static final String COPYLEFT = "BMovieReviewer Copyright (C) 2009, 2010  Michael J. Beer, Stefan Knipl\n"
        + "This program comes with ABSOLUTELY NO WARRANTY; \n" + "This is free software, and you are welcome to redistribute it\n"
        + "under certain conditions; \n" + "See file 'COPYING' for more details.\n\n" +
        "This programm uses JOrtho for spellchecking. \nVisit http://jortho.sourceforge.net/ for further reading.\n\n" +
        "This programm uses Stax-Utils for formatted XML - output.\n" +
        "Visit https://stax-utils.dev.java.net/ for further reading.\n\n" +
        "This program uses the Apache HttpComponents for posting to the server\n" +
        "Have a look at http://hc.apache.org/  for further reading.\n\n";
        //"This program uses \'MultiPartFormOutputStream.java\' for generating http multipart-posts.\n" +
        //"Visit http://forums.sun.com/thread.jspa?threadID=451245&forumID=31 for further reading.\n\n";
	
	
	public static BufferedImage logo = null;
	
	/////////////////////////////////////////////////////////////
	//  Zugriff auf die Eigenschaften
	
	/** 
	 * Setzt Eigenschaft 
	 * @param key Eigenschaft
	 * @param value neuer Wert
	 * @return true falls Eigenschaft existiert 
	 */
	public boolean setProperty(String key, String value) {	
		boolean found = settings.containsKey(key);
		if(found) {
			settings.put(key, value);
		}
		return found;
	}
	
	
	/**
	 * Holt wert einer Eigenschaft
	 * @param key Eigenschaft
	 * @return den Wert der Eigenschaft
	 */
	public String getProperty(String key) {
		if(key == null) {
			throw new IllegalArgumentException();
		}
		return settings.get(key);
	}
	
	
	public static int getAppType() {
		return appType;
	}

	public static void setAppType(int i) {
	    appType = i;
	}
	
	/** 
	 * Liefert die aktuelle Globalsinstanz
	 * @return
	 */
	public static Globals getInstance() {
		if(instance == null) {
			return (instance = new Globals());
		} 
		return instance;
	}
	
	/**
	 * liefert 2d - Stringarray aller keys und deren Werte 
	 * Groesze nx2 mit n Zahl der Keys
	 * @return
	 */
	public String[][] getMatrix() {
	    Globals globs = getInstance();
	    Set<String> keys = settings.keySet();
	    
	    String[][] m = new String[keys.size()][];
	    int index = 0;
	    for(String key : keys) {
	     m[index] = new String[2];
	     m[index][0] = key;
	     m[index++][1] = globs.getProperty(key);   
	    }	    
	    return m;
	}
	
	
	public Map<String, String> getMap() {
	    return settings;
	}
	
	/**
	 *  Gibt eine geordnete Liste der Eigenschaftsnamen zurueck
	 * @return
	 */
	public SortedSet<String> sortedKeys() {
	    SortedSet<String> keys = new TreeSet<String>();
	    for(String s : getMap().keySet()) {
	        keys.add(s);
	    }
	    
	    return keys;
	}
	
	protected void addProperty(String key, String value) {
		settings.put(key, value);
	}
	
	
	public String toString() {
		String desc = "Globals\n";
		for(int i = 0; i < keys.length; i++) {
			desc += keys[i] + " : " + getProperty(keys[i]) + "\n";
		}
		return desc;
	}
	
	public int getPropertyAsInt(String prop) throws NumberFormatException{
	    if(prop == null) throw new IllegalArgumentException();
	    return Integer.parseInt(getProperty(prop));
	}

    public String getSendReport() {
        return sendReport;
    }


    public void setSendReport(String sendReport) {
        if(sendReport == null) {
             throw new IllegalArgumentException();
        }
        this.sendReport = sendReport;
    }
    
    
	//////////////////////////////////////////////////////////////////////////
	// INTERNALS
    //////////////////////////////////////////////////////////////////////////
	
	
	protected Map<String, String> settings; 

	protected String sendReport = new String("Noch Nichts gesandt");
	
	private Globals() {
		settings = new HashMap<String, String>();
		if(keys.length != stdValues.length) {
			throw new RuntimeException();
		}
		for(int i = 0; i < keys.length; i++) {
			addProperty(keys[i], stdValues[i]);
		}
	}
}
