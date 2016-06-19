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

import gui.Gui;
import gui.Gui.BogenListener;
import gui.TextEditPopup;
import gui.TextEditPopup.AnySelectionChangedListener;
import gui.panels.ImageViewPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.formats.BogenFormat;
import data.formats.JPEG;

public class CoverPanel extends JPanel implements BogenListener,
		ComponentListener {

	protected JTextField cover;

	protected final String COVER_URL_TOOLTIP = "Gib hier den Link zu einer Datei an oder wähle eine Datei mit dem \'Durchsuchen\'-Knopf! Vorsicht: Nur JPG erlaubt!";

	protected Gui gui = null;

	// Die Teilkomponenten
	protected ImageViewPanel view = null;
	protected JPanel choose = null;

	/**
	 * Enthaelt das ViewPanel Das Viewpanel wird an die Groesze dieses
	 * Containers angepasst Die Groeszenanpassung ist der einzige Zweck, warum
	 * das Viewpanel nicht direkt an das Coverpanel gehaengt wird - Das
	 * ViewPanel wuerde sich sonst auf die gesamte Groesze des CoverPanels
	 * ausdehnen und das CoverChoosePanel ueberlagern
	 */
	protected Container viewContainer = null;

	public Gui getGui() {
		return gui;
	}

	public String getOldCoverFileName() {
		return oldCoverFileName;
	}

	public CoverPanel(Gui gui, int hauptWidth) {
		super();
		if (gui == null) {
			throw new IllegalArgumentException();
		}
		this.gui = gui;
		this.setLayout(new BorderLayout());
		choose = createCoverChoosePanel(this, hauptWidth);

		this.view = new ImageViewPanel(null);
		this.viewContainer = new Container();

		this.addComponentListener(this);
		this.viewContainer.addComponentListener(this.view);

		this.viewContainer.add(view);
		this.add(viewContainer, BorderLayout.CENTER);
		this.addComponentListener(view);
		this.add(choose, BorderLayout.SOUTH);
	}

	public void setBogen(Bogen bogen) {
		if (bogen == null) {
			throw new IllegalArgumentException();
		}
		oldCoverFileName = bogen.getCover().getText();
		cover.setDocument(bogen.getCover());
		this.view.setData(bogen.getCoverImage());
	}

	protected JPanel createCoverChoosePanel(CoverPanel parent, int hauptWidth) {
		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK), "Bilddatei"));

		panel.add(new JLabel("URL: "));
		cover = new JTextField();
		cover.addCaretListener(AnySelectionChangedListener.getInstance());
		cover.addMouseListener(TextEditPopup.getPopupListener());
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		JButton b = new JButton("Durchsuchen");
		Dimension dim = b.getPreferredSize();
		b.setMinimumSize(dim);
		b.setMaximumSize(dim);
		b.addActionListener(new DurchsuchenListener(cover, parent,
				new FileNameExtensionFilter("JPeg-Dateien", "jpg", "JPG")));
		if ((Globals.getAppType() & Globals.APPLET) == Globals.APPLET) {
			b.setEnabled(false);
		}
		buttons.add(b);
		b = new JButton("Bild Laden");
		b.setMinimumSize(dim);
		b.setMaximumSize(dim);
		b.addActionListener(new LoadImageListener(cover, parent));
		buttons.add(b);
		cover.setMaximumSize(new Dimension(100 * dim.width, dim.height));
		cover.setMinimumSize(dim);
		cover.addKeyListener(new LoadImageListener(cover, this));
		cover.setToolTipText(COVER_URL_TOOLTIP);
		panel.add(cover);
		panel.add(buttons);

		return panel;
	}

	protected String oldCoverFileName = "";

	protected static class LoadImageListener implements KeyListener,
			ActionListener {

		JTextComponent txt = null;
		CoverPanel parent = null;

		public LoadImageListener(JTextComponent txt, CoverPanel parent) {
			if (txt == null || parent == null) {
				throw new IllegalArgumentException();
			}
			this.txt = txt;
			this.parent = parent;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			String msg = null;
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				System.out.println("Going to load " + txt.getText());
				if (!fetchImage(txt.getText())) {
					// alten Text wiederherstellen
					txt.setText(parent.getOldCoverFileName());
					JOptionPane.showMessageDialog(parent.getGui(),
							"Konnte Bilddatei nicht laden!",
							"Ein&Ausgabefeher", JOptionPane.ERROR_MESSAGE);
					msg = "Konnte Bilddatei nicht laden.";
				} else {
					msg = "Bilddatei "
							+ parent.getGui().getBogen().getCover().getText()
							+ " geladen.";
				}
				AppLogger.info(msg);
				parent.getGui().setStatus(msg);
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

		protected boolean fetchImage(String urlStr) {
			try {
				URL url = new URL(urlStr);
				fetchImage(url);
			} catch (Exception e) {
				AppLogger
						.throwing("LoadImageListener", "fetchImage(String)", e);
				return false;
			}
			return true;
		}

		protected boolean fetchImage(URL url) {
			if (url == null) {
				throw new IllegalArgumentException();
			}
			int scaledWidth = 300;
			Bogen bogen = this.parent.getGui().getBogen();
			try {
				scaledWidth = Globals.getInstance().getPropertyAsInt(
						"coverwidth");
			} catch (NumberFormatException e) {
				AppLogger.throwing("LoadImageListener", "fetchImage", e);
				scaledWidth = 300;
			}
			try {
				InputStream in = url.openStream();
				BogenFormat imageSerializer = new JPEG(bogen, scaledWidth);
				try {
					imageSerializer.read(in);
				} finally {
					in.close();
				}
			} catch (Exception e) {
				AppLogger.throwing("LoadImageListener", "fetchImage", e);
				return false;
			}
			String name = bogen.getFileNameWithoutEnding() + ".jpg";
			if (name.equals(Bogen.UNKNOWN)) {
				name = url.getFile();
				int pos = name.lastIndexOf('/');
				name = (pos >= 0 && pos + 1 < name.length()) ? name
						.substring(name.lastIndexOf('/') + 1) : name;
			}
			bogen.getCover().setText(name);
			parent.setBogen(bogen);
			this.parent.getGui().setStatus(url + " erfolgreich geladen");
			return true;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!fetchImage(txt.getText())) {
				JOptionPane.showMessageDialog(parent.getGui(),
						"Konnte Bilddatei nicht laden!", "Ein&Ausgabefeher",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	protected static class DurchsuchenListener extends LoadImageListener {

		JTextField text;
		CoverPanel parent;
		FileNameExtensionFilter filter = null;

		public DurchsuchenListener(JTextField text, CoverPanel parent) {
			this(text, parent, null);
		}

		public DurchsuchenListener(JTextField text, CoverPanel parent,
				FileNameExtensionFilter filter) {
			super(text, parent);
			this.text = text;
			this.parent = parent;
			this.filter = filter;
		}

		public void actionPerformed(ActionEvent e) {

			JFileChooser chooser = new JFileChooser(parent.getGui().getBogen()
					.getFilePath());
			File cover = null;

			if (this.filter != null) {
				chooser.setFileFilter(filter);
			}
			int returnVal = chooser.showOpenDialog(parent.getGui());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					cover = new File(chooser.getSelectedFile()
							.getAbsolutePath());
					if (cover != null) {
						if (!fetchImage(cover.toURI().toURL())) {
							throw new IOException();
						}
					}
				} catch (IOException ex) {
					AppLogger
							.throwing(this.toString(), "actionPerformed()", ex);
					parent.getGui()
							.setStatus(
									"Konnte Titelbild nicht in Arbeitsverzeichnis übertragen - Abgebrochen.");
					JOptionPane.showMessageDialog(parent.getGui(),
							ex.getMessage(), "Fehler bei Titelbildwahl",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public void imageChanged(BufferedImage image) {
		this.view.contentChanged(image);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		Dimension dim = arg0.getComponent().getSize();
		if (choose.getHeight() > 0) {
			dim.height -= choose.getHeight();
		}
		// Falls nicht zu klein, Neue Groesze einstellen
		if (dim.height >= this.getMinimumSize().height
				|| dim.width >= this.getMinimumSize().width) {
			this.viewContainer.setPreferredSize(dim);
			this.viewContainer.setSize(dim);
			// und die Bildposition neu berechnen
			// recalcImagePosition();
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}
}
