/*
 * BMovieReviewer Copyright (C) 2009, 2012 Michael J. Beer
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
package gui;

import gui.dialogs.SplashUploadWindow;
import gui.panels.mainpanels.CoverPanel;
import gui.panels.mainpanels.LinkPanel;
import gui.panels.mainpanels.OverviewPanel;
import gui.panels.mainpanels.QuotePanel;
import gui.panels.mainpanels.TextPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import tools.AppLogger;

import com.inet.jortho.SpellChecker;

import data.Bogen;
import data.Globals;
import data.Link;
import data.ResourceManager;
import data.Zitat;

public class Gui extends JPanel {

	public Gui() {
		this(Globals.APP_NAME, new Bogen());
	}

	public Gui(String titel, Bogen bogen) {
		if (bogen == null || titel == null) {
			throw new IllegalArgumentException();
		}
		this.bogen = bogen;
		this.titel = titel;
		initialize();
		this.erzeugeFrame(titel);
	}

	public Bogen getBogen() {
		return bogen;
	}

	public synchronized void setStatus(String status) {
		statusBar.setStatus(status);
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public void showSplash(int type, int stage) {
		if (type != 1) {
			throw new IllegalArgumentException();
		}
		this.splashUploadDialog.showStage(stage);
	}

	public void showSplash(int type, float progress) {
		if (type != 1) {
			throw new IllegalArgumentException();
		}
		this.splashUploadDialog.showStage(progress);
	}

	public void hideSplash(int type) {
		if (type != 1) {
			throw new IllegalArgumentException();
		}
		this.splashUploadDialog.hideSplash();
	}

	public synchronized void setBogen(Bogen bogen) {
		if (bogen == null) {
			throw new IllegalArgumentException();
		}

		this.bogen = bogen;

		Iterator<BogenListener> it = bogenListeners.iterator();
		while (it.hasNext()) {
			it.next().setBogen(bogen);
		}
	}

	public static interface BogenListener extends ActionListener {
		public void setBogen(Bogen b);
	}

	// ////////////////////////////////////////////////////////////////////////
	// INTERNALS
	// ////////////////////////////////////////////////////////////////////////

	protected void terminate() {
		System.out.println(Globals.APP_NAME + " beendet");
		if ((Globals.getAppType() & Globals.APPLET) == Globals.APPLET) {
			mainFrame.setVisible(false);
		} else {
			System.exit(0);
		}
	}

	protected void erzeugeFrame(String titel) {

		if (mainFrame != null)
			mainFrame.dispose();

		mainFrame = new JFrame(titel);
		mainFrame.setLayout(new BorderLayout());

		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				terminate();
			}
		});

		JTabbedPane tabbedMainPanel = createTabbedMainPanel();
		tabbedMainPanel.setVisible(true);
		mainFrame.add(tabbedMainPanel, BorderLayout.CENTER);

		erzeugeMenue();

		statusBar = new StatusBar(Globals.initialStatus);
		mainFrame.add(statusBar, BorderLayout.SOUTH);

		setBogen(bogen);

		setCorrectSize();
		mainFrame.pack();
		mainFrame.setResizable(true);

		mainFrame.setVisible(true);
	}

	protected void erzeugeMenue() {
		HauptMenue menue = new HauptMenue(this);
		mainFrame.setJMenuBar(menue);

	}

	protected JTabbedPane createTabbedMainPanel() {
		JTabbedPane haupt = new JTabbedPane();

		OverviewPanel overviewPanel = new OverviewPanel(bogen);
		overviewPanel.setVisible(true);
		bogenListeners.add(overviewPanel);
		haupt.add(overviewPanel, "Allgemein");

		addTextPanelToParentPane(haupt, Bogen.I_TECHNISCH);
		addTextPanelToParentPane(haupt, Bogen.I_INHALT);
		addTextPanelToParentPane(haupt, Bogen.I_WISSENSCHAFT);
		addTextPanelToParentPane(haupt, Bogen.I_BILD);
		addTextPanelToParentPane(haupt, Bogen.I_HANDLUNG);
		addTextPanelToParentPane(haupt, Bogen.I_BEMERKUNGEN);
		addTextPanelToParentPane(haupt, Bogen.I_RSS);

		LinkPanel mngtPanel = new LinkPanel(bogen.getLinks(), textHeight,
				Link.TYPES);
		mngtPanel.setVisible(true);
		bogenListeners.add(mngtPanel);
		haupt.add(mngtPanel, "Links");

		QuotePanel quotePanel = new QuotePanel(bogen.getZitate(), textHeight,
				Zitat.TYPES);
		quotePanel.setVisible(true);
		bogenListeners.add(quotePanel);
		haupt.add(quotePanel, "Bewertete Zitate");

		CoverPanel coverPanel = new CoverPanel(this, haupt.getWidth());
		coverPanel.setVisible(true);
		bogenListeners.add(coverPanel);
		haupt.add(coverPanel, "Titelbild");

		return haupt;
	}

	protected void addTextPanelToParentPane(JTabbedPane parentPane,
			int textFeldIndex) {
		TextPanel panel = new TextPanel(this.bogen, textFeldIndex);
		String title = Bogen.getTextfeldName(textFeldIndex);
		panel.setVisible(true);
		bogenListeners.add(panel);
		parentPane.add(panel, title);
	}

	protected void setCorrectSize() {
		Rectangle screenBounds = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension size = new Dimension(
				(screenBounds.width < 800) ? screenBounds.width : 800,
				(screenBounds.height < 800) ? screenBounds.height : 800);
		mainFrame.setPreferredSize(size);
	}

	protected void initialize() {
		bogenListeners = new LinkedList<BogenListener>();
		Globals globs = Globals.getInstance();
		ResourceManager resourceManager = ResourceManager.getInstance();

		// Rechtschreibpruefung
		if (!globs.getProperty("spellcheck.mode").equals("disabled")) {
			try {
				URL url = resourceManager.getResource("spellcheck.dictionaries");
				SpellChecker.registerDictionaries(url, null);
			} catch (MalformedURLException e) {
				AppLogger
						.warning("Konnte Konfiguration für Rechtschreibkorrektur nicht finden");
				globs.setProperty("spellcheck.mode", "disabled");
			}
		}

		// Logo
		try {
			URL logoURL = resourceManager.getResource("logourl");
			Globals.logo = ImageIO.read(logoURL);
		} catch (Exception e) {
			AppLogger.throwing("Gui", "initialize()", e);
			Globals.logo = null;
		}

		// Splashdialog vorbereiten...
		splashUploadDialog = new SplashUploadWindow(this);
	}

	// ////////////////////////////////////////////////
	// Internal Listener Classes

	protected static class PunktListener implements BogenListener {

		protected Bogen bogen;
		protected JComboBox cb;

		protected int index;

		public PunktListener(int index, Bogen bogen, JComboBox cb) {
			if (index < 0 || index > Bogen.KATEGORIEN.length || bogen == null
					|| cb == null) {
				throw new IllegalArgumentException();
			}
			this.bogen = bogen;
			this.index = index;
			this.cb = cb;
		}

		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
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

		public void setBogen(Bogen b) {
			if (b == null) {
				throw new IllegalArgumentException();
			}
			this.bogen = b;
			cb.setSelectedItem(bogen.getText(Bogen.I_FSK));
		}

		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String fsk = (String) cb.getSelectedItem();
			bogen.setText(Bogen.I_FSK, fsk);
		}
	}

	private static final long serialVersionUID = 1L;

	protected String titel = "";

	protected JFrame mainFrame;

	protected Bogen bogen;

	protected List<BogenListener> bogenListeners;

	protected SplashUploadWindow splashUploadDialog;

	protected StatusBar statusBar = null;

	protected int textHeight = 20;

	protected final String COVER_URL_TOOLTIP = "Gib hier den Link zu einer Datei an oder wähle eine Datei mit dem \'Durchsuchen\'-Knopf! Vorsicht: Nur JPG erlaubt!";

}
