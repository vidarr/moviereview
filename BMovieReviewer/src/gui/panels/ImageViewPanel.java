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
package gui.panels;

import gui.dialogs.DisplayDialog.DisplayPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

/**
 * Panel zum Anzeigen eines BufferedImage
 * Das Bild wird wenn moeglich zentriert
 * Sollte als ComponentListener an die uebergeordnete Komponente angehaengt werden
 * Dann passt sich das Panel automatisch der Groesze der uebergeordneten Komponente an
 * @author mibeer
 *
 */
public class ImageViewPanel extends DisplayPanel<BufferedImage> implements ComponentListener{

    public ImageViewPanel(BufferedImage image) {
        //this.image = image;
        this.setImage(image);
        //position = new Point(0, 0);
    }
    
     
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if(image != null) {
            Dimension dim = new Dimension(image.getWidth(), image.getHeight());
            this.setMinimumSize(dim);
            this.setPreferredSize(dim);
            
            // Bild in der Mitte des Panels platzieren
            recalcImagePosition();
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(image, position.x, position.y, this);
        }
    }

    
    protected void recalcImagePosition() {
        // Falls noch kein Bild gesetzt, nichts tun
        if(image == null) {
            return;
        }
        int x, y;
        x = (this.getWidth() - image.getWidth());
        x = (x > 0) ? x/2 : 0;
        y = (this.getHeight() - image.getHeight());
        y = (y > 0) ? y/2 : 0;
        position = new Point(x, y);
    }
    
    protected BufferedImage image = null;
    protected Point position = null;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    @Override
    public void onButtonPressed(int val) {        
    }

    @Override
    public void setData(BufferedImage data) {
        this.setImage(data);
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
            // Dieses Panel resizen
            Dimension dim = arg0.getComponent().getSize();
            // Falls nicht zu klein, Neue Groesze einstellen
            if(dim.height >= this.getMinimumSize().height || dim.width >= this.getMinimumSize().width) {
                this.setPreferredSize(dim);
                this.setSize(dim);
             // und die Bildposition neu berechnen            
                recalcImagePosition();
            }        
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        
    }
}
