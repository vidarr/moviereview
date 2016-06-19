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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import tools.AppLogger;

import com.inet.jortho.SpellChecker;

import data.Bogen;
import data.Globals;

public class OverviewPanel extends JPanel implements BogenListener {
    
    public final static int ANMERKUNGEN_WIDTH = 200;
    
    JPanel allg = null;
    
    List<BogenListener> bogenListeners = null;
    
    protected JTextComponent[] anmerkungen;
    
    public OverviewPanel(Bogen bogen) {
        super();
        
        bogenListeners = new LinkedList<BogenListener>();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints consts = new GridBagConstraints();
        consts.gridwidth = GridBagConstraints.REMAINDER;
        consts.gridheight = 2;
        consts.anchor = GridBagConstraints.NORTHWEST;
        consts.fill = GridBagConstraints.HORIZONTAL;

        this.setLayout(gridbag);
        
        JPanel panel = createTxtPanel(bogen);
        panel.setVisible(true);
        this.add(panel, consts);
        
        panel = createPointPanel(bogen);
        panel.setVisible(true);
        this.add(panel, consts);
    }
    

    @Override
    public void setBogen(Bogen b) { 
        if (b == null) {
            throw new IllegalArgumentException();
        }
                
        for(BogenListener bl : bogenListeners) {
            bl.setBogen(b);
        }
        
    }
    
    
    protected JPanel createTxtPanel(Bogen bogen) {
        allg = new JPanel();
        allg.setLayout(new BoxLayout(allg, BoxLayout.Y_AXIS));
        allg.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Allgemeines"));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        Dimension dim;

        JTextField txt = new JTextField();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        
        FontMetrics fm = txt.getFontMetrics(txt.getFont());
        int xWidth = fm.charWidth('x');

        dim = txt.getPreferredSize();
        JLabel label = new JLabel("Titel: ");
        dim.width = label.getPreferredSize().width;
        label.setMinimumSize(dim);
        label.setMaximumSize(dim);
        label.setVisible(true);
        panel.add(label);
        bogenListeners.add(new TxtListener(bogen, txt, Bogen.I_TITEL));
        panel.add(txt);

        label = new JLabel(" FSK: ");
        dim = new Dimension(dim);
        dim.width = label.getPreferredSize().width;
        label.setMinimumSize(dim);
        label.setMaximumSize(dim);
        label.setVisible(true);
        panel.add(label);

        JComboBox cboFSK = new JComboBox(Bogen.FSK_TYPES);
        FSKListener fsk = new FSKListener(bogen, cboFSK);
        cboFSK.addActionListener(fsk);
        bogenListeners.add(fsk);
        cboFSK.setVisible(true);
        panel.add(cboFSK);

        panel.setVisible(true);
        allg.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        label = new JLabel(" Originaltitel: ");
        dim = new Dimension(dim);
        dim.width = label.getPreferredSize().width;
        label.setMinimumSize(dim);
        label.setMaximumSize(dim);
        label.setVisible(true);
        panel.add(label);
        txt = new JTextField();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        dim = new Dimension(dim);
        dim.width = ANMERKUNGEN_WIDTH;
        txt.setMinimumSize(dim);
        txt.setVisible(true);
        panel.add(txt);
        bogenListeners.add(new TxtListener(bogen, txt, Bogen.I_ORIGINALTITEL));

        label = new JLabel(" Jahr: ");
        dim = new Dimension(dim);
        dim.width = label.getPreferredSize().width;
        label.setMinimumSize(dim);
        label.setMaximumSize(dim);
        label.setVisible(true);
        panel.add(label);
        txt = new JTextField();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        dim = new Dimension(dim);
        dim.width = 6 * xWidth;
        txt.setPreferredSize(dim);
        txt.setMinimumSize(dim);
        txt.setMaximumSize(dim);
        txt.setVisible(true);
        bogenListeners.add(new TxtListener(bogen, txt, Bogen.I_JAHR));
        panel.add(txt);
        panel.setVisible(true);
        allg.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        label = new JLabel(" Land: ");
        dim = new Dimension(dim);
        dim.width = label.getPreferredSize().width;
        label.setMaximumSize(dim);
        label.setMinimumSize(dim);
        label.setVisible(true);
        panel.add(label);
        
        txt = new JTextField();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        dim = new Dimension(dim);
        dim.width = (ANMERKUNGEN_WIDTH * 2) / 3;
        txt.setPreferredSize(dim);
        txt.setMinimumSize(dim);
        txt.setVisible(true);
        bogenListeners.add(new TxtListener(bogen, txt, Bogen.I_LAND));
        panel.add(txt);

        txt = new JTextField();
        txt.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txt.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
        dim = new Dimension(dim);
        txt.setPreferredSize(dim);
        txt.setMinimumSize(dim);
        label = new JLabel(" Genre: ");
        dim = new Dimension(dim);
        dim.width = label.getPreferredSize().width;
        label.setMaximumSize(dim);
        label.setMinimumSize(dim);
        label.setVisible(true);
        panel.add(label);
        txt.setVisible(true);
        panel.add(txt);
        bogenListeners.add(new TxtListener(bogen, txt, Bogen.I_GENRE));
        panel.setVisible(true);
        allg.add(panel);
        
        return allg;
    }

    
    protected JPanel createPointPanel(Bogen bogen) {
        JPanel punktwertungen = new JPanel();
        punktwertungen.setLayout(new BoxLayout(punktwertungen, BoxLayout.Y_AXIS));
        punktwertungen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Punktwertungen"));

        JLabel laengstesLabel = new JLabel(Bogen.KATEGORIEN[Bogen.getLaengsteKategorie()]);
        JLabel punkteLabel = new JLabel("Punkte ");
        String[] p = { Bogen.PUNKT_KEIN, Bogen.PUNKT_EIN, Bogen.PUNKT_ZWEI, Bogen.PUNKT_DREI, 
                Bogen.PUNKT_VIER, Bogen.PUNKT_FUENF };
        JComboBox punkte = new JComboBox(p);

        Dimension dimKategorie = laengstesLabel.getPreferredSize();
        Dimension dimPunkte = punkte.getPreferredSize();
        dimKategorie.height = dimPunkte.height;
        dimPunkte.width = punkteLabel.getPreferredSize().width;

        JPanel panel;
        JLabel beschriftung;
        PunktListener pl;
        JTextField txtNote;

        for (int index = 0; index < Bogen.KATEGORIEN.length; index++) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            beschriftung = new JLabel(Bogen.KATEGORIEN[index]);
            beschriftung.setPreferredSize(dimKategorie);
            beschriftung.setMinimumSize(dimKategorie);
            beschriftung.setVisible(true);
            panel.add(beschriftung);
            punkte = new JComboBox(p);
            // punktWertungen[index] = punkte;
            pl = new PunktListener(index, bogen, punkte);
            punkte.addActionListener(pl);
            bogenListeners.add(pl);
            punkte.setPreferredSize(dimPunkte);
            punkte.setMinimumSize(dimPunkte);
            punkte.setVisible(true);
            panel.add(punkte);
            panel.setVisible(true);

            punktwertungen.add(panel);
            txtNote = new JTextField();
            txtNote.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
            txtNote.addMouseListener(TextEditPopup.getPopupSpellCheckListener());
            txtNote.setVisible(true);
            bogenListeners.add(new NoteListener(bogen, txtNote, index));
            panel.add(txtNote);
            punktwertungen.add(panel);
        }
        return punktwertungen;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
    }
    
    
    protected static class FSKListener implements BogenListener {

        protected Bogen bogen;
        protected JComboBox cb;

        public FSKListener(Bogen bogen, JComboBox cb) {
            if (bogen == null || cb == null) {
                throw new IllegalArgumentException();
            }
            this.bogen = bogen;
            this.cb = cb;
        }

        public void actionPerformed(ActionEvent e) {
            String fsk = (String) cb.getSelectedItem();
            bogen.setText(Bogen.I_FSK, fsk);
        }
       
        
        public void setBogen(Bogen b) { 
            // FSK setzen
            if (b == null) {
                throw new IllegalArgumentException();
            }
            this.bogen = b;
            cb.setSelectedItem(b.getText(Bogen.I_FSK));
        }

    }
    
    
    protected static class PunktListener implements BogenListener {

        protected Bogen bogen;
        protected JComboBox cb;

        protected int index;

        public PunktListener(int index, Bogen bogen, JComboBox cb) {
            if (index < 0 || index > Bogen.KATEGORIEN.length || bogen == null || cb == null) {
                throw new IllegalArgumentException();
            }
            this.bogen = bogen;
            this.index = index;
            this.cb = cb;
        }

        public void actionPerformed(ActionEvent e) {
            String punkt = (String) cb.getSelectedItem();

            int val = Integer.parseInt(punkt);
            bogen.setPunkt(index, val);
        }

        public void setBogen(Bogen bogen) {
            if (bogen == null) {
                throw new IllegalArgumentException();
            }
            this.bogen = bogen;
            cb.setSelectedIndex(bogen.getPunkt(index));
        }
    }

    
    protected static class TxtListener implements BogenListener {

        protected Bogen bogen;
        protected JTextComponent txt;
        protected int index = 0;

        public TxtListener(Bogen bogen, JTextComponent txt, int i) {
            if (bogen == null || txt == null) {
                throw new IllegalArgumentException();
            }
            this.bogen = bogen;
            this.txt = txt;
            index = i;
        }

        public void actionPerformed(ActionEvent e) {
        }
       
        
        public void setBogen(Bogen b) { 
            // FSK setzen
            if (b == null) {
                throw new IllegalArgumentException();
            }
            txt.setDocument(b.getTextWrappers()[index]);  
            try {
                if (!Globals.getInstance().getProperty("spellcheck.mode").equals("disabled")) {
                    SpellChecker.unregister(txt);
                    SpellChecker.register(txt);
                    SpellChecker.enablePopup(txt, false);
                }
            } catch (Exception e) {
                AppLogger.throwing("SpellChecker", "register(JTextComponent)", e);
            }
        }

    }
    
    protected static class NoteListener extends TxtListener {
        public NoteListener(Bogen bogen, JTextComponent txt, int i) {
            super(bogen, txt, i);
        }
        
        public void setBogen(Bogen b) { 
            if (b == null) {
                throw new IllegalArgumentException();
            }
            txt.setDocument(b.getAnmerkungWrapper(index));
            try {
                if (!Globals.getInstance().getProperty("spellcheck.mode").equals("disabled")) {
                    SpellChecker.unregister(txt);
                    SpellChecker.register(txt);
                    SpellChecker.enablePopup(txt, false);
                }
            } catch (Exception e) {
                AppLogger.throwing("SpellChecker", "register(JTextComponent)", e);
            }
        }
    }
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
