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
package threads;

import gui.Gui;
import gui.StatusBar;

import java.io.IOException;

import javax.swing.JOptionPane;

import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.HTTPPost;

public class SendThread implements Runnable {

    public SendThread(Gui gui, Bogen bogen, HTTPPost post) {
        if (bogen == null || post == null || gui == null) {
            throw new IllegalArgumentException();
        }
        this.post = post;
        // wird geclont, damit sicher gestellt ist, dass sich Daten waehrend des
        // Posts nicht mehr aendern
        this.bogen = bogen.clone();
        this.gui = gui;
    }

    public SendThread(Gui gui, Bogen bogen, HTTPPost post, boolean c, boolean x, boolean t, boolean q, boolean r) {
        this(gui, bogen, post);
        this.postCover = c;
        this.postXML = x;
        this.postTex = t;
        this.postQuotes = q;
        this.registerMovie = r;
    }

    public SendThread(Gui gui, Bogen bogen, HTTPPost post, boolean c, boolean x, boolean t, boolean q, boolean r, boolean enableSplash) {
        this(gui, bogen, post, c, x, t, q, r);
        this.enableSplash = enableSplash;
    }

    @Override
    public void run() {
        ThreadRegistry.getInstance().registerThread(this, "Send");
        StatusBar status = gui.getStatusBar();
        result = "";
        float progress = 0, progressStep = 0;

        progressStep += (postCover) ? 1.0f : 0f;
        progressStep += (postXML) ? 1.0f : 0f;
        progressStep += (postTex) ? 1.0f : 0f;
        progressStep += (postQuotes) ? 1.0f : 0f;
        progressStep += (registerMovie) ? 1.0f : 0f;
        progressStep = 1.0f / progressStep;
        
        if (enableSplash) {
            gui.showSplash(1, 0);
        }

        try {          
             if (postCover) {
                if (bogen.getCoverImage() != null) {
                    status.setStatus("Sende Cover...");
                    result += post.postCover(bogen) + "\n\n";
                } else {
                    result += "Post Cover: \nKein Bild angegeben.\n\n";
                }
                progress += progressStep;
                if (enableSplash) {
                    gui.showSplash(1, progress);
                }
            }
            
            if (postXML) {
                status.setStatus("Sende XML...");
                result += post.postXML(bogen) + "\n\n";
                progress += progressStep;
                if (enableSplash) {
                    gui.showSplash(1, progress);
                }
            }

            if (postTex) {
                status.setStatus("Sende Tex...");
                result += post.postTex(bogen) + "\n\n";
                progress += progressStep;
                if (enableSplash) {
                    gui.showSplash(1, progress);
                }
            }
            

            if (registerMovie) {
                status.setStatus("Registriere Film...");
                result += post.registerMovie(bogen) + "\n\n";
                progress += progressStep;
                if (enableSplash) {
                    gui.showSplash(1, progress);
                } 
            }
            
            
            
            if (postQuotes) {
                status.setStatus("Sende Zitate...");
                result += post.postQuotes(bogen) + "\n\n";
                progress += progressStep;
            }
            
            gui.setStatus("Übertragung abgeschlossen.");
            
            if (enableSplash) {
                gui.showSplash(1, progress);
            } else {
                JOptionPane.showMessageDialog(this.gui, result, "Serverantwort", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (HTTPPost.HTTPException e) {
            gui.hideSplash(1);
            gui.setStatus("Übertragungsfehler - Siehe Bericht!");
            AppLogger.throwing("SendThread", "run", e);
            result += "\n\n\n" + "Code: " + e.getResponseCode() + " " + e.getMessage();
            JOptionPane.showMessageDialog(this.gui, "Code: " + e.getResponseCode() + " " + e.getMessage(), 
                    "Authentifizierung", JOptionPane.ERROR_MESSAGE);
        } catch(IOException ex) {
            gui.hideSplash(1);
            gui.setStatus("Übertragungsfehler - Siehe Bericht!");
            AppLogger.throwing("SendThread", "run", ex);
            result += "\n\n\n" + ex.getMessage();
            JOptionPane.showMessageDialog(this.gui, ex.getMessage(), 
                    "Authentifizierung", JOptionPane.ERROR_MESSAGE);
        }

        AppLogger.info(result);
        Globals.getInstance().setSendReport(result);
        
        ThreadRegistry.getInstance().unregisterThread(this);
    }

    public String getResult() {
        return this.result;
    }

    public String toString() {
        return "SendThread : " + post.toString();
    }
    
    // ************************************************************************
    // INTERNALS
    // ************************************************************************

    protected Bogen bogen = null;
    protected HTTPPost post = null;
    protected String result = null;
    protected Gui gui = null;

    protected boolean postCover = true;
    protected boolean postXML = true;
    protected boolean postTex = true;
    protected boolean postQuotes = true;
    protected boolean registerMovie = true;

    protected boolean enableSplash = true;
}
