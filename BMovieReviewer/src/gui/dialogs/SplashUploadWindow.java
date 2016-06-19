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

package gui.dialogs;

import gui.panels.ImageViewPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import tools.AppLogger;
import data.Globals;
import data.ResourceManager;

public class SplashUploadWindow extends JWindow {

	protected SplashPanel display = null;
	protected JProgressBar progress = null;
	protected boolean fadeOut = true;

	public static final String MSG_SENDE = "Sende";

	/**
	 * Time in ms till fade out of splash screen is accomplished
	 */
	public static final String PROP_FADING_TIME = "splash.fadingtime";

	/**
	 * Zahl der Splashbilder
	 */
	public static final int NO_SPLASHES = 6;

	public SplashUploadWindow(Component parent) {
		super();
		this.getImages();
		this.parent = parent;
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.display = new SplashPanel(this);
		panel.add(display);
		progress = new JProgressBar(0, 100);
		progress.setString(MSG_SENDE + "...");
		progress.setStringPainted(true); // Damit die Hoehe stimmt...
		panel.add(progress);
		this.add(panel);
		this.pack();

		// Splash in der Schirmmitte platzieren
		GraphicsConfiguration graphicsConf = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		Rectangle rec = graphicsConf.getBounds();

		this.setLocation((rec.width - this.getWidth()) / 2,
				(rec.height - this.getHeight()) / 2);
		fadeOut = true;

	}

	/**
	 * Zeigt zu Stage x passendes Bild an
	 */
	public void showStage(int i) {
		if (splashes == null) {
			return; // sagt nur aus, dass die Bilder nicht geladen wurden ...
		}
		if (i >= splashes.length) {
			throw new IllegalArgumentException();
		}
		display.setData(splashes[i]);
		progress.setValue(i * 100 / (splashes.length - 1));
		setAlwaysOnTop(true);
		toFront();
		this.setVisible(true);
		repaint();

		if (i == splashes.length - 1) {
			display.activateMouseListener();
			progress.setStringPainted(true);
			progress.setString("Zum Schließen auf Kopf klicken");
			waitForClose();
		}
	}

	/**
     * 
     */
	public void showStage(float f) {
		if (f < 0f || f > 1f) {
			throw new IllegalArgumentException();
		}
		if(splashes == null) return;
		this.showStage((int) ((splashes.length - 1) * f));
	}

	public void hideSplash() {
		this.setVisible(false);
	}

	protected BufferedImage[] splashes = null;
	protected Component parent = null;

	/**
	 * Laedt die Bilder
	 */
	protected void getImages() {
		Globals globs = Globals.getInstance();
		ResourceManager resourceManager = ResourceManager.getInstance();
		// Wenn nicht aktiviert, werden Bilder nicht benoetigt
		if (!globs.getProperty("splash.uploads").equals("enabled")) {
			return;
		}

		splashes = new BufferedImage[NO_SPLASHES];
		for (int i = 0; i < splashes.length; i++) {
			URL url;
			try {
				url = resourceManager.getResource("splash.upload_" + (i + 1));
				AppLogger.config("Hole Splash-Bild " + i + 1 + " von URL "
						+ url);
				splashes[i] = ImageIO.read(url);
			} catch (Exception e) {
				AppLogger.warning("Konnte Bild " + "splash.upload_" + (i + 1)
						+ "nicht laden - Uploadsplashes werden nicht gezeigt!");
				globs.setProperty("splash.uploads", "disabled");
				splashes = null;
				return;
			}
		}
		// fertig
	}

	public void waitForClose() {
		if (fadeOut) {
			long fadingTime = Globals.getInstance().getPropertyAsInt(
					PROP_FADING_TIME);
			// Transparenz wird erst mit Java 7 unterstuetzt ...
			try {
				Thread.sleep(fadingTime);
			} catch (InterruptedException e) {
				AppLogger.throwing("SplashUploadWindow", "waitForClose", e);
			}
		} else {
			// Warten, bis Fenster geschlossen wird ...
			while (true) {
				Thread.yield();
			}
		}
		this.setVisible(false);
	}

	public void setVisible(boolean state) {
		super.setVisible(state);
		// String zuruecksetzen
		if (!state) {
			this.progress.setString(MSG_SENDE + "...");
			this.progress.setStringPainted(true);
		}
	}

	/**
	 * Das Panel zur Anzeige der Splashes
	 */
	public static class SplashPanel extends ImageViewPanel {

		protected MouseKlickedListener mouseListener = null;

		public SplashPanel(Component parent) {
			super(null);
			if (parent == null) {
				throw new IllegalArgumentException();
			}
			mouseListener = new MouseKlickedListener(this);
			this.addMouseListener(mouseListener);
			Dimension dim = new Dimension(300, 300);
			this.setMinimumSize(dim);
			this.setMaximumSize(dim);
			this.setPreferredSize(dim);
			this.parent = parent;
		}

		public void activateMouseListener() {
			this.mouseListener.setActive(true);
		}

		protected Component parent;

		public Component getMyParent() {
			return parent;
		}

		protected static class MouseKlickedListener implements MouseListener {

			protected SplashPanel panel = null;
			protected boolean active = false;

			public MouseKlickedListener(SplashPanel panel) {
				if (panel == null) {
					throw new IllegalArgumentException();
				}
				this.panel = panel;
			}

			public void setActive(boolean a) {
				this.active = a;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (active) {
					panel.getMyParent().setVisible(false);
					this.active = false;
					Thread.currentThread().interrupt();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

		}

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
