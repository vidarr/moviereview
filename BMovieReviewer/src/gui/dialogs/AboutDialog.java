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
package gui.dialogs;

import gui.panels.ImageViewPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Globals;

public class AboutDialog extends DisplayDialog<BufferedImage> {
    
    
    public static final int IMG_WIDTH = 300;
    public static final int IMG_HEIGHT = 300;
    public static String INFO_TEXT = "<html><center><h2>" + Globals.APP_NAME + "  " + Globals.APP_VERSION + "</h2>" +
        Globals.COPYLEFT.replace("\n", "<br>") + "<p>" + 
        "<br>Visit: <a href=\"https://github.com/vidarr/moviereview\">https://github.com/vidarr/moviereview</a>" +
        " to stay up to date.</center></html>";    

    public  AboutDialog(Component parent, BufferedImage img) {
        this(parent, img, INFO_TEXT);
    }
    
    public AboutDialog(Component parent, BufferedImage img, String text) {
        super(parent, new AboutPanel(img, text), "Über", DisplayDialog.OK_BUTTON, true);
        setResizable(false);
    }

    
    protected static class AboutPanel extends DisplayPanel<BufferedImage> {

        public AboutPanel(BufferedImage img, String text) {
            if(text == null) {
                throw new IllegalArgumentException();
            }
            
            this.text = text;
            this.image= img;
            
            //JPanel panel = new JPanel();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            JPanel viewPanel = new ImageViewPanel(img);
            Dimension dim = new Dimension(IMG_WIDTH, IMG_HEIGHT);
            viewPanel.setMinimumSize(dim);
            viewPanel.setPreferredSize(dim);
          
            add(viewPanel);
            JLabel txt = new JLabel(this.text);
            add(txt);
            //add(panel);
        }
        
        @Override
        public void onButtonPressed(int val) {
            this.getDialog().setVisible(false);
            
        }

        @Override
        public void setData(BufferedImage data) {
            // Das Logo soll nicht im laufenden Betrieb geaendert werden...
            if(data != null) {
                throw new IllegalArgumentException();
            }
        }
           
        protected String text = null;
        protected BufferedImage image = null;
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
