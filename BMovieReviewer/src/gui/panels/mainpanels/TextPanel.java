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
package gui.panels.mainpanels;

import gui.TextEditPopup;
import gui.Gui.BogenListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tools.AppLogger;

import com.inet.jortho.SpellChecker;

import data.Bogen;
import data.Globals;

public class TextPanel extends JPanel implements BogenListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected JTextArea txt;
    protected int index = 0;
    protected boolean useSpellcheck = false;

    public TextPanel(Bogen bogen, int index) {
        setLayout(new BorderLayout());

        txt = new JTextArea();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setVisible(true);
        this.add(new JScrollPane(txt), BorderLayout.CENTER);
        this.index = index;
        try {
            if (!Globals.getInstance().getProperty("spellcheck.mode").equals("disabled")) {
                SpellChecker.register(txt);
                useSpellcheck = true;                
            } 
        } catch (Exception e) {
            AppLogger.throwing("TextPanel", "TextPanel", e);
        }
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        SpellChecker.enablePopup(txt, false);
    }

    @Override
    public void setBogen(Bogen b) {
        txt.setDocument(b.getTextWrappers()[index]);
        // Rechtschreibpruefung aktivieren
        if(useSpellcheck) {
            SpellChecker.unregister(txt);
            txt.setDocument(b.getTextWrappers()[index]);
            SpellChecker.register(txt);
            SpellChecker.enablePopup(txt, false);
        } else {
            txt.setDocument(b.getTextWrappers()[index]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
