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
package gui;

import gui.dialogs.AboutDialog;
import gui.dialogs.DisplayDialog;
import gui.dialogs.LoginDialog;
import gui.dialogs.LoginOptionDialog;
import gui.dialogs.PreferencesDialog;
import gui.dialogs.ThreadDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import threads.ExportPDFThread;
import threads.ImportPDFThread;
import threads.SendThread;
import threads.ThreadRegistry;
import tools.AppLogger;
import data.Bogen;
import data.Globals;
import data.HTTPPost;
import data.formats.BogenFormat;
import data.formats.BogenSerializerFactory;
import data.formats.HardDisc;
import data.formats.Tex;
import data.formats.Zip;

public class HauptMenue extends JMenuBar {

	private static final long serialVersionUID = 1L;

	protected Gui gui;

	protected static class LadenListener implements ActionListener {

		protected Gui gui;

		public LadenListener(Gui gui) {
			if (gui == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
		}

		/**
		 * oeffnet einen FileChooser-Dialog und liefert gewaehlte Datei zurueck
		 * 
		 * @param filter
		 * @return
		 */

		protected File getFile(FileNameExtensionFilter filter, int kind) {
			return Utils.getFile(this.gui, gui.getBogen().getFilePath(),
					filter, kind);
		}

		protected File getFile(Vector<FileNameExtensionFilter> filters, int kind) {
			return Utils.getFile(this.gui, gui.getBogen().getFilePath(),
					filters, kind);
		}

		public void actionPerformed(ActionEvent e) {
			File file;

			file = getFile(
					BogenSerializerFactory.getFileNameExtensionFilters(),
					JFileChooser.OPEN_DIALOG);
			if (file == null) {
				return;
			}

			String msg;
			Bogen bogen = new Bogen();
			try {
				BogenSerializerFactory.getSerializerForFile(file, bogen);
				BogenFormat serializer = BogenSerializerFactory
						.getSerializerForFile(file, bogen);
				serializer.read(file);
				msg = file.toString() + " erfolgreich geladen";
				AppLogger.info(msg);
				this.gui.setStatus(msg);
				AppLogger.info(msg);
				gui.setBogen(bogen); // um die Listener zu benachrichtigen
			} catch (HardDisc.ImageException ex) {
				AppLogger.throwing("LadenListener", "actionPerformed", ex);
				gui.setBogen(bogen); // um die Listener zu benachrichtigen
				msg = ex.getMessage();
				JOptionPane.showMessageDialog(this.gui, msg);
			} catch (IOException ex) {
				AppLogger.throwing("LadenListener", "actionPerformed", ex);
				JOptionPane.showMessageDialog(this.gui, ex.getMessage());
				msg = ex.getMessage();
			}
			this.gui.setStatus(msg);
		}
	}

	protected static class ExportTexListener implements ActionListener {

		public ExportTexListener(HauptMenue menue) {
			if (menue == null) {
				throw new IllegalArgumentException();
			}
			this.menue = menue;
		}

		public void actionPerformed(ActionEvent e) {
			FileOutputStream fileOut = null;
			String fileNameWithoutSuffix = menue.gui.getBogen().getFilePath()
					+ File.separator
					+ menue.gui.getBogen().getFileNameWithoutEnding();
			try {
				tools.Utils.checkForSTY(fileNameWithoutSuffix + ".sty");
				fileOut = new FileOutputStream(fileNameWithoutSuffix + ".tex",
						false);
				Bogen bogen = this.menue.gui.getBogen();
				Tex texFormatter = new Tex(bogen);
				try {
					texFormatter.write(fileOut);
					menue.gui.setStatus(fileNameWithoutSuffix + ".tex"
							+ " gespeichert.");
				} finally {
					fileOut.close();
				}
			} catch (Exception ex) {
				AppLogger.throwing("ExportTexListener", "actionPerformed", ex);
				menue.gui.setStatus(fileNameWithoutSuffix + ".tex"
						+ " konnte nicht geschrieben werden.");
			}
		}

		protected HauptMenue menue;
	}

	protected static class ExportBogenListener extends LadenListener {

		protected boolean choice = false;

		public ExportBogenListener(Gui gui) {
			super(gui);
		}

		public ExportBogenListener(Gui gui, boolean choice) {
			this(gui);
			this.choice = choice;
		}

		public void actionPerformed(ActionEvent e) {
			File file;
			Bogen bogen = this.gui.getBogen();
			BogenFormat serializer = new Zip(bogen);
			String filePath = "";
			if (choice) {
				FileNameExtensionFilter filter = serializer
						.getFileNameExtensionFilter();

				file = getFile(filter, JFileChooser.SAVE_DIALOG);
				if (file != null) {
					file = Utils.ensureFileExtension(file, filter);
					bogen.setFileName(file.getName());
					bogen.setFilePath(file.getPath());
					filePath = file.getAbsolutePath();
				} else {
					return;
				}
			} else {
				filePath = bogen.getFilePath() + File.separator
						+ bogen.getFileNameWithoutEnding() + "."
						+ serializer.getFormatExtension();
				file = new File(filePath);
			}
			try {
				OutputStream out = new FileOutputStream(file);
				try {
					serializer.write(out);
				} finally {
					out.close();
				}
				this.gui.setStatus(gui.getBogen().getFileNameWithoutEnding()
						+ " erfolgreich gespeichern");
			} catch (IOException ex) {
				AppLogger
						.throwing("ExportBogenListener", "actionPerformed", ex);
				this.gui.setStatus("Fehler beim Speichern von " + filePath);
				JOptionPane.showMessageDialog(gui, "Fehler beim Speichern von "
						+ filePath, "Ein/Ausgabefehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected static class ImportPDFListener extends LadenListener {

		public ImportPDFListener(Gui gui) {
			super(gui);
		}

		public void actionPerformed(ActionEvent e) {
			File file;

			file = getFile(new FileNameExtensionFilter("PDF-Dateien", "pdf",
					"PDF"), JFileChooser.OPEN_DIALOG);
			if (file == null) {
				return;
			}

			Runnable importThread = new ImportPDFThread(file, gui);
			new Thread(importThread).start();

		}
	}

	protected static class ExportPDFListener extends LadenListener {

		public ExportPDFListener(Gui gui) {
			super(gui);
		}

		public void actionPerformed(ActionEvent e) {

			AppLogger.info("Schreibe nach "
					+ gui.getBogen().getFileNameWithoutEnding() + ".pdf");
			gui.setStatus("Daten werden exportiert...");
			Runnable export = new ExportPDFThread(gui, gui.getBogen()
					.getFilePath(), gui.getBogen().getFileNameWithoutEnding());
			(new Thread(export)).start();
		}
	}

	public static class BeendenListener implements ActionListener {

		protected Gui gui;

		public BeendenListener(Gui gui) {
			if (gui == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			// Gui das Signal zum Terminieren senden...
			gui.terminate();
		}

	}

	public static class SendenListener extends LadenListener {
		public SendenListener(Gui gui) {
			super(gui);
		}

		public SendenListener(Gui gui, boolean withOptions) {
			this(gui);
			this.withOptions = withOptions;
		}

		public void actionPerformed(ActionEvent e) {

			boolean c = true, x = true, t = true, q = true, r = true;

			dlgLogin = getLoginDialog();
			if (dlgLogin.showLoginDialog() == JFileChooser.APPROVE_OPTION) {
				String name = dlgLogin.getLogin();
				String password = dlgLogin.getPassword();
				if (name == null || password == null) {
					AppLogger
							.warning("Nutzername und Passwort zur Anmeldung benoetigt");
					return;
				}
				c = dlgLogin.isOptionChosen(0);
				x = dlgLogin.isOptionChosen(1);
				t = dlgLogin.isOptionChosen(2);
				q = dlgLogin.isOptionChosen(3);
				r = dlgLogin.isOptionChosen(4);
				HTTPPost post = new HTTPPost(this.getUrl(), name, password);
				SendThread send;
				send = new SendThread(gui, gui.bogen, post, c, x, t, q, r);

				new Thread(send).start();
			}
		}

		protected boolean withOptions = false;
		protected LoginDialog dlgLogin;

		public final static String[] options = { "Sende Titelbild",
				"Sende XML-Datei", "Sende Tex-Datei", "Registriere Zitate",
				"Registriere Film" };

		protected String getUrl() {
			Globals globs = Globals.getInstance();
			String dir = globs.getProperty("server.dir");
			dir = (dir.equals("")) ? dir : '/' + dir;
			return globs.getProperty("server.protocoll") + "://"
					+ globs.getProperty("server.name") + dir;
		}

		protected LoginDialog getLoginDialog() {
			LoginDialog dlgLogin;
			if (withOptions) {
				dlgLogin = new LoginOptionDialog(gui, options);
			} else {
				dlgLogin = new LoginDialog(gui);
			}
			return dlgLogin;
		}
	}

	public static class ThreadListener implements ActionListener {

		public ThreadListener(Gui gui) {
			if (gui == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			ThreadDialog dialog = new ThreadDialog(gui);
			dialog.showDialog(this.gui, ThreadRegistry.getInstance());
		}

		Gui gui = null;
	}

	public static class GlobalsListener implements ActionListener {

		public GlobalsListener(Gui gui, DisplayDialog<Globals> dialog) {
			if (gui == null || dialog == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.showDialog(gui, Globals.getInstance());
		}

		Gui gui = null;
		DisplayDialog<Globals> dialog = null;
	}

	public static class PreviewListener implements ActionListener {

		public PreviewListener(Gui gui, DisplayDialog<BufferedImage> dialog) {
			if (gui == null || dialog == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.showDialog(gui, gui.getBogen().getCoverImage());
		}

		Gui gui = null;
		DisplayDialog<BufferedImage> dialog = null;
	}

	public static class AboutListener extends PreviewListener {

		public AboutListener(Gui gui) {
			super(gui, new AboutDialog(gui, Globals.logo));
		}

		public void actionPerformed(ActionEvent e) {
			dialog.showDialog(gui, null);
		}
	}

	public static class SendenBerichtListener implements ActionListener {

		protected Gui gui = null;

		public SendenBerichtListener(Gui gui) {
			if (gui == null) {
				throw new IllegalArgumentException();
			}
			this.gui = gui;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(this.gui, Globals.getInstance()
					.getSendReport(), "Serverantwort",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	protected JMenuItem erzeugeEintrag(String bez, ActionListener aL, int mn) {
		// Eintrag anlegen
		JMenuItem mi = new JMenuItem(bez);
		if (mn != 0)
			mi.setMnemonic(mn);
		else
			mi = new JMenuItem(bez);

		// actionlistener setzen
		mi.addActionListener(aL);

		return mi;
	}

	/**
	 * der einzige Konstruktor, wird benutzt, um das ganze menu herzustellen
	 */
	public HauptMenue(Gui gui) {
		super();
		int noEntries = 0;
		Globals globs = Globals.getInstance();

		JMenuItem mi;

		if (gui == null) {
			throw new IllegalArgumentException();
		}
		this.gui = gui;

		JMenu daten = new JMenu("Daten");
		daten.setMnemonic(KeyEvent.VK_D);
		// Bei einem Applet waere Laden/Speichern wegen den Sicherheitspolicies
		// nur
		// umstaendlich zu handhaben, daher bei Applet/Servlet die betreffenden
		// Menuepunkte gar nicht
		// erst erzeugen
		if ((Globals.getAppType() & Globals.APPLET) != Globals.APPLET) {
			mi = erzeugeEintrag("Laden", new LadenListener(gui), KeyEvent.VK_L);
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
					InputEvent.CTRL_MASK));
			daten.add(mi);

			mi = erzeugeEintrag("Speichern", new ExportBogenListener(this.gui),
					KeyEvent.VK_S);
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					InputEvent.CTRL_MASK));
			daten.add(mi);

			mi = erzeugeEintrag("Speichern unter", new ExportBogenListener(
					this.gui, true), KeyEvent.VK_U);
			daten.add(mi);

			noEntries = 3;

			if (!globs.getProperty("texcommand").equals("disabled")
					&& (Globals.getAppType() & Globals.MIT_PDF) == Globals.MIT_PDF) { // Mit
				// Import
				// von
				// PDF
				mi = erzeugeEintrag("Importiere von PDF",
						new ImportPDFListener(gui), KeyEvent.VK_P);
				daten.add(mi);
				mi = erzeugeEintrag("Exportiere nach PDF",
						new ExportPDFListener(gui), KeyEvent.VK_D);
				daten.add(mi);
				daten.insertSeparator(3);
				daten.insertSeparator(6);
				noEntries += 4;
			} else {
				Globals.initialStatus = "Texkommando : "
						+ globs.getProperty("texcommand")
						+ " nicht gefunden - PDF-Unterstützung deaktiviert";
			}
			mi = erzeugeEintrag("Exportiere nach LaTex", new ExportTexListener(
					this), KeyEvent.VK_L);
			daten.add(mi);
			noEntries++;
		}

		if ((Globals.getAppType() & Globals.MIT_SENDEN) == Globals.MIT_SENDEN) {
			daten.insertSeparator(++noEntries);
			mi = erzeugeEintrag("Sende an Server",
					new SendenListener(gui, true), KeyEvent.VK_T);
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
					InputEvent.CTRL_MASK));
			daten.add(mi);
			mi = erzeugeEintrag("Sende Alle Daten an Server",
					new SendenListener(gui, false), KeyEvent.VK_T);
			daten.add(mi);
			noEntries++;
		}
		daten.insertSeparator(++noEntries);
		mi = erzeugeEintrag("Beenden", new BeendenListener(gui), KeyEvent.VK_B);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		daten.add(mi);

		add(daten);

		JMenu bearbeiten = TextEditPopup.getTextEditMenu();
		add(bearbeiten);

		JMenu ansicht = new JMenu("Ansicht");
		ansicht.setMnemonic(KeyEvent.VK_A);
		ansicht.add(erzeugeEintrag("Bericht", new SendenBerichtListener(gui),
				KeyEvent.VK_B));
		ansicht.add(erzeugeEintrag("Einstellungen", new GlobalsListener(gui,
				new PreferencesDialog(this.gui)), KeyEvent.VK_T));
		ansicht.add(erzeugeEintrag("Threads", new ThreadListener(gui),
				KeyEvent.VK_T));
		add(ansicht);

		JMenuItem beenden = new JMenuItem("Über");
		beenden.setMnemonic(KeyEvent.VK_B);
		beenden.addActionListener(new AboutListener(gui));
		add(beenden);

	}
}
