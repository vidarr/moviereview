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

import gui.Gui.BogenListener;
import gui.ManagedQualifiedStringTable;
import gui.TextEditPopup;
import gui.TextEditPopup.AnySelectionChangedListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import data.Bogen;
import data.wrappers.QualifiedStringList;
import data.wrappers.TrailerWrapper;

public class LinkPanel extends JPanel implements BogenListener {
    
    public static final String TRAILER_URL_TOOLTIP = "Füge hier den Youtube Link auf den Trailer ein";
    
    
    ManagedQualifiedStringTable linkTable = null;
    TrailerChooser trailerChooser = null;
    
    
    public LinkPanel(QualifiedStringList list, int textHeight, String[] types) {
        super();
        
        linkTable = new ManagedQualifiedStringTable(list, textHeight, types); 
        trailerChooser = new TrailerChooser();
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(linkTable);
        this.add(trailerChooser);
        
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void setBogen(Bogen b) {
        this.linkTable.setModel(b.getLinks()); 
        this.trailerChooser.setModel(b.getTrailer());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    protected class TrailerChooser extends JPanel {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        
        protected JTextField trailerEditor;
        
        public  TrailerChooser() {
        super();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Youtube-Trailer"));

        add(new JLabel("URL: "));
        trailerEditor= new JTextField();
        trailerEditor.addCaretListener(AnySelectionChangedListener.getInstance());
        trailerEditor.addMouseListener(TextEditPopup.getPopupListener());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        JButton b = new JButton("Übernehmen");
        Dimension dim = b.getPreferredSize();
        b.setMinimumSize(dim);
        b.setMaximumSize(dim);
        //b.addActionListener(new LoadImageListener(trailer));
        buttons.add(b);
        trailerEditor.setMaximumSize(dim = new Dimension(100 * dim.width, 2 * dim.height));
        trailerEditor.setMinimumSize(dim);
        //trailer.addKeyListener(new LoadImageListener(trailer));
        trailerEditor.setToolTipText(TRAILER_URL_TOOLTIP);
        add(trailerEditor);
        add(buttons);
    }
        
        public void setModel(PlainDocument doc) {
            if(doc == null) {
                throw new IllegalArgumentException();
            }
            trailerEditor.setDocument(doc);
        }
        
    }
    
    
    public class TrailerListener implements KeyListener, ActionListener {

        protected JTextComponent trailer;
        protected TrailerWrapper doc;
        
        public TrailerListener (JTextComponent trailer, Bogen bogen) {
            if(trailer == null || bogen == null) {
                throw new IllegalArgumentException();
            }
            this.trailer = trailer;
        }
        
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            //doc.setText(this.trailer.getText());
            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            //doc.setText(this.trailer.getText());
            
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub
            
        }
        
    }

}
