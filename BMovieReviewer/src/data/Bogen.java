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

package data;

import gui.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import data.wrappers.IntWrapper;
import data.wrappers.QualifiedStringList;
import data.wrappers.StringWrapper;
import data.wrappers.TextWrapper;
import data.wrappers.TrailerWrapper;

public class Bogen implements Cloneable {
    
    
    ///////////////////////////////////////////////////////////////////////////
    // STATISCHE INFOS
    ///////////////////////////////////////////////////////////////////////////
    
    public static final String UNKNOWN = "unbekannt";

    public static final String PUNKT_KEIN = "0";

    public static final String PUNKT_EIN = "1";

    public static final String PUNKT_ZWEI = "2";

    public static final String PUNKT_DREI = "3";

    public static final String PUNKT_VIER = "4";

    public static final String PUNKT_FUENF = "5";

    public static final int MAX_PUNKTE = 5;

    public static final int ANMERKUNGEN_MAX_LEN = 25;

    public static final String[] KATEGORIEN = { "Unterhaltungswert", "Pornofaktor", "Gewaltdarstellung", "Gewaltverherrlichung", "Niveau",
            "Sexismus", "Professionalität", "Realismus" };

    public static final String[] TEXTFELDER = { "titel", "originaltitel", "land", "jahr", "genre", "fsk", "rss", "handlung",
            "technisch", "inhalt",
            "wissenschaft", "bemerkungen",  "bild"};

    public static String getTextfeldName(int no) {
    	return TEXTFELD_NAMEN[no];
    }
    
    public static final String[] TEXTFELD_NAMEN = { "Titel", "Originaltitel", "Land", "Jahr", "Genre", "Fsk", "RSS", "Handlung",
        "Fehler-Technisch", "Fehler-Inhaltlich",
        "Fehler-Wissenschaftlich", "Bemerkungen",  "Bild"};

    // Interna, werden von formats.XMLBogen benoetigt
    public static int erstesTextfeld = 6;
    public static int laengsteKategorie = 0;
    public static int laengsterLinkTyp = 0;

    public final static int I_TITEL = 0;
    public final static int I_ORIGINALTITEL = 1;
    public final static int I_LAND = 2;
    public final static int I_JAHR = 3;
    public final static int I_GENRE = 4;
    public final static int I_FSK = 5;    
    public final static int I_MAX_DETAILS = 6;
    public final static int I_BEMERKUNGEN = 11;
    public final static int I_TECHNISCH = 8;
    public final static int I_INHALT = 9;
    public final static int I_WISSENSCHAFT = 10;
    public final static int I_BILD = 12;
    public final static int I_HANDLUNG = 7;
    public final static int I_RSS = 6;
    public final static int I_MAX_TEXT = 13;

    public final static int I_UNTERHALTUNGSWERT = 0;
    public final static int I_PORNOFAKTOR = 1;
    public final static int I_GEWALTDARSTELLUNG = 2;
    public final static int I_GEWALTVERHERRLICHUNG = 3;
    public final static int I_NIVEAU = 4;
    public final static int I_SEXISMUS = 5;
    public final static int I_PROFESSIONALITAET = 6;
    public final static int I_REALISMUS = 7;
    
    public final static String[] FSK_TYPES = { "unbekannt", "0", "6", "12", "16", "18", "Keine Freigabe", "Indiziert", "Beschlagnahmt" };

   
    public static int getLaengsteKategorie() {
        return laengsteKategorie;
    }

    public static int getLaengsterLinkTyp() {
        return laengsterLinkTyp;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    //  KONSTRUKTOR
    ///////////////////////////////////////////////////////////////////////////
    
    
    public Bogen() {
        punkte = new int[KATEGORIEN.length];
        anmerkungen = new StringWrapper[punkte.length];
        for (int i = 0; i < punkte.length; i++) {
            punkte[i] = 0;
            anmerkungen[i] = new StringWrapper("", ANMERKUNGEN_MAX_LEN);
        }

        texte = new StringWrapper[I_MAX_TEXT];

        texte[I_TITEL] = new StringWrapper(UNKNOWN);
        texte[I_ORIGINALTITEL] = new StringWrapper(UNKNOWN);
        texte[I_LAND] = new StringWrapper(UNKNOWN);
        texte[I_GENRE] = new StringWrapper(UNKNOWN);
        texte[I_JAHR] = new IntWrapper(0, 4);
        texte[I_FSK] = new StringWrapper(UNKNOWN);
        texte[I_TECHNISCH] = new TextWrapper("Dem Publikum ist nichts aufgefallen");
        texte[I_INHALT] = new TextWrapper("Dem Publikum ist nichts aufgefallen");
        texte[I_WISSENSCHAFT] = new TextWrapper("Dem Publikum ist nichts aufgefallen");
        texte[I_BILD] = new TextWrapper("Dem Publikum ist keines aufgefallen");
        texte[I_HANDLUNG] = new TextWrapper("Dem Publikum ist keine aufgefallen");
        texte[I_BEMERKUNGEN] = new TextWrapper("Dem Publikum erscheint nichts bemerkenswert");
        texte[I_RSS] = new TextWrapper("Nichts Neues");
        zitate = new QualifiedStringList("Zitate");
        links = new QualifiedStringList("Link");
        cover = new StringWrapper(StringWrapper.EMPTY_STRING);
        trailer = new TrailerWrapper(StringWrapper.EMPTY_STRING);
    }

    
    public synchronized Bogen clone() {
        Bogen cp = new Bogen();

        // Punktwertungen kopieren
        for (int i = 0; i < KATEGORIEN.length; i++) {
            cp.setPunkt(i, this.getPunkt(i));
            cp.setAnmerkungen(i, this.getAnmerkung(i));
        }

        for (int i = 0; i < I_MAX_TEXT; i++) {
            cp.setText(i, this.getText(i));
        }

        cp.setFileName(this.getFileNameWithoutEnding());
        cp.setFilePath(this.getFilePath());
        cp.getCover().setText(this.getCover().getText());
        cp.setTrailer(this.getTrailer().getText());
        BufferedImage image = getCoverImage();
        if(image != null) {
            cp.setCoverImage(image.getSubimage(0, 0, image.getWidth(), image.getHeight()));
        }
        for (QualifiedString l : this.getLinks()) {
            cp.getLinks().add(l.clone());
        }

        for (QualifiedString l : this.getZitate()) {
            cp.getZitate().add(l.clone());
        }

        return cp;
    } 
    
    
    ///////////////////////////////////////////////////////////////////////////
    //  GETTERS / SETTERS
    ///////////////////////////////////////////////////////////////////////////
    

    public String getAnmerkung(int index) throws IllegalArgumentException {
        if (index >= anmerkungen.length) {
            throw new IllegalArgumentException();
        }
        return anmerkungen[index].getText();
    }

    public void setAnmerkungen(int index, String anmerkung) throws IllegalArgumentException {
        if (index >= anmerkungen.length) {
            throw new IllegalArgumentException();
        }
        this.anmerkungen[index].setText(anmerkung);
    }

    public int getPunkt(int index) throws IllegalArgumentException {
        if (index >= punkte.length) {
            throw new IllegalArgumentException();
        }
        return punkte[index];
    }

    public void setPunkt(int index, int punkt) throws IllegalArgumentException {
        if (index >= punkte.length || punkt > MAX_PUNKTE || punkt < 0) {
            throw new IllegalArgumentException();
        }

        this.punkte[index] = punkt;
    }

    public void setText(int i, String s) {
        if (i < 0 || i > I_MAX_TEXT) {
            throw new IllegalArgumentException();
        }
        this.texte[i].setText(s);
    }

    public String getText(int i) {
        if (i < 0 || i > I_MAX_TEXT) {
            throw new IllegalArgumentException();
        }
        return this.texte[i].getText();
    }

    public StringWrapper getTextWrapper(int i) {
        if (i < 0 || i > I_MAX_TEXT) {
            throw new IllegalArgumentException();
        }
        return texte[i];
    }

    public StringWrapper[] getTextWrappers() {
        return texte;
    }

    public void setTitel(String titel) {
        if (titel == null) {
            throw new IllegalArgumentException();
        }
        this.setText(I_TITEL, titel);
    }

    public String getTitel() {
        return getText(I_TITEL);
    }

    public void setFSK(int fsk) {
        if (fsk < 0 || fsk >= Bogen.FSK_TYPES.length) {
            throw new IllegalArgumentException();
        }
        texte[I_FSK].setText(Bogen.FSK_TYPES[fsk]);
    }

    public String getFSK() {
        return texte[I_FSK].getText();
    }

    public String getGenre() {
        return getText(I_GENRE);
    }

    public void setGenre(String genre) {
        if (genre == null) {
            throw new IllegalArgumentException();
        }
        this.setText(I_GENRE, genre);
    }

    public int getJahr() {
        return ((IntWrapper) texte[I_JAHR]).getInt();
    }

    public void setJahr(int jahr) {
        ((IntWrapper) texte[I_JAHR]).setInt(jahr);
    }

    public void setJahr(String s) {
        setText(I_JAHR, s);
    }

    public String getLand() {
        return getText(I_LAND);
    }

    public void setLand(String land) {
        setText(I_LAND, land);
    }

    public String getOriginalTitel() {
        return getText(I_ORIGINALTITEL);
    }

    public void setOriginalTitel(String originalTitel) {
        setText(I_ORIGINALTITEL, originalTitel);
    }

    public StringWrapper getTitelWrapper() {
        return texte[I_TITEL];
    }

    public StringWrapper getOriginalTitelWrapper() {
        return texte[I_ORIGINALTITEL];
    }

    public StringWrapper getLandWrapper() {
        return texte[I_LAND];
    }

    public StringWrapper getGenreWrapper() {
        return texte[I_GENRE];
    }

    public IntWrapper getJahrWrapper() {
        return (IntWrapper) texte[I_JAHR];
    }

    public StringWrapper getTechnischWrapper() {
        return texte[I_TECHNISCH];
    }

    public StringWrapper getInhaltWrapper() {
        return texte[I_INHALT];
    }

    public StringWrapper getWissenschaftWrapper() {
        return texte[I_WISSENSCHAFT];
    }

    public StringWrapper getBildWrapper() {
        return texte[I_BILD];
    }

    public StringWrapper getHandlungWrapper() {
        return texte[I_HANDLUNG];
    }

    public StringWrapper getBemerkungenWrapper() {
        return texte[I_BEMERKUNGEN];
    }

    public IntWrapper getFSKWrapper() {
        return (IntWrapper) texte[I_FSK];
    }

    public StringWrapper getAnmerkungWrapper(int i) {
        if (i < 0 || i >= KATEGORIEN.length) {
            throw new IllegalArgumentException();
        }
        return anmerkungen[i];
    }

    public QualifiedStringList getZitate() {
        return zitate;
    }

    public QualifiedStringList getLinks() {
        return links;
    }

    public StringWrapper getCover() {
        return cover;
    }
    
    public BufferedImage getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(BufferedImage image) {
        if(image == null) {
            throw new IllegalArgumentException();
        }
        this.coverImage = image;
    }
    
    public TrailerWrapper getTrailer() {
        return trailer;
    }
    
    public void setTrailer(String trailer) {
        if(trailer == null) {
            throw new IllegalArgumentException();
        }
        this.trailer.setText(trailer);
    }

    /**
     * Setzt den Dateinamen neu, ersetzt Leerzeichen durch Unterstriche und
     * entfernt Endung
     * 
     * @param name
     */
    public synchronized void setFileName(String name) {
        fileName = name.trim();
        fileName = Utils.getWithoutFileNameExtension(name).replaceAll("\\W", "_");
    }

    /**
     * Liefert den zu benutzenden Dateinamen OHNE Endung. Ist entweder der Name,
     * der beim letzten "Speichern unter" angegeben wurde, oder der Filmtitel
     * 
     * @return den zu nutzenden Dateinamen
     */
    public synchronized String getFileNameWithoutEnding() {
        return (fileName == null) ? getText(Bogen.I_TITEL).toLowerCase().replaceAll("\\W", "_") : fileName;
    }

    /**
     * Setzt den Dateipfad neu, mit abschlieszendem Separator
     * 
     * @param name
     */
    public synchronized void setFilePath(String name) {
        int pathEnd = name.lastIndexOf(File.separatorChar);
        if (pathEnd > 0) {
            filePath = name.substring(0, pathEnd);
        }
    }
    
    /**
     * Liefert den aktuellen Pfad Ist entweder der Pfad zur Datei, der beim
     * letzten "Speichern unter" angegeben wurde, oder das aktuelle Verzeichnis
     * 
     * @return den zu nutzenden Dateinamen
     */
    public String getFilePath() {
        return (filePath == null) ? Globals.getInstance().getProperty("basedirectory") : filePath;
    }

    public String toString() {
        String str = getText(I_TITEL) + "    " + getText(I_LAND) + "   " + getText(I_ORIGINALTITEL) + "   " + getText(I_JAHR) + "   "
                + getText(I_FSK) + "   " + getText(I_GENRE) + "\n";

        for (int index = 0; index < punkte.length; index++) {
            if (punkte[index] > 0) {
                if (this.anmerkungen[index] != null) {
                    str = str + "[" + anmerkungen[index] + "]  ";
                }
                str = str + punkte[index] + "\n";
            }
        }
        str = str + getText(I_TECHNISCH) + "\n" + getText(I_WISSENSCHAFT) + "\n" + getText(I_INHALT) + "\n" + getText(I_BILD) + "\n"
                + getText(I_HANDLUNG) + "\n" + getText(I_BEMERKUNGEN) + "\n";

        str = str + "Zitate:\n";
        Iterator<QualifiedString> it = zitate.iterator();
        while (it.hasNext()) {
            str = str + it.next() + "\n";
        }

        return str;
    } 
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    ///////////////////////////////////////////////////////////////////////////
    
    
    protected int[] punkte;

    protected StringWrapper[] anmerkungen;

    protected StringWrapper[] texte;

    protected QualifiedStringList links, zitate;

    protected StringWrapper cover;

    protected BufferedImage coverImage;
    
    protected TrailerWrapper trailer;
    
    /**
     * Gibt den aktuellen Suchpfad an
     */
    protected String filePath = null;
    
    /**
     * Gibt an, unter welchem Namen die Datei gespeichert werden soll (ohne
     * Endung) Falls null, wird der Titel des Filmes genommen
     */
    private String fileName = null;
    
    
    ///////////////////////////////////////////////////////////////////////////
    // STATISCHER KONSTRUKTOR
    ///////////////////////////////////////////////////////////////////////////
    
    
    static {

        for (int index = 0; index < KATEGORIEN.length; index++) {
            if (KATEGORIEN[index].length() > KATEGORIEN[laengsteKategorie].length()) {
                laengsteKategorie = index;
            }
        }

        for (int index = 0; index < Link.TYPES.length; index++) {
            if (Link.TYPES[index].length() > Link.TYPES[laengsterLinkTyp].length()) {
                laengsterLinkTyp = index;
            }
        }
    }

}
