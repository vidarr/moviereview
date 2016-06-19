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

import gui.Utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import data.Globals;
import data.formats.XMLConfig;
import data.wrappers.AnnotatedStringList;

public class PreferencesDialog extends DisplayDialog<Globals> {

    public PreferencesDialog(Component parent) {
        super(parent, new PreferencesPanel(parent, Globals.getInstance()), "Einstellungen",
                DisplayDialog.OK_BUTTON, true);

        super.addButton(SAVE_BUTTON, "Speichern");
        super.addButton(EXPORT_BUTTON, "Exportieren");
        super.setButtons(OK_BUTTON | SAVE_BUTTON | EXPORT_BUTTON);
        // this.setResizable(false);
    }

    protected static class PreferencesPanel extends DisplayPanel<Globals> {

        public PreferencesPanel(Component parent, Globals globs) {
            super();

            if(globs == null || parent == null) {
                throw new IllegalArgumentException();
            }
            this.parent = parent;
            this.globs = globs;

            this.setLayout(new BorderLayout());

            table = new JTable();
            table.setModel(new AnnotatedStringList());
            Dimension dim = new Dimension(500, 400);
            //table.setMinimumSize(dim);
            table.setVisible(true);
            JScrollPane scrlPane = new JScrollPane(table);
            scrlPane.setVisible(true);
            scrlPane.setMinimumSize(dim);
            scrlPane.setPreferredSize(dim);
            this.add(scrlPane, BorderLayout.CENTER);
        }

        @Override
        public void onButtonPressed(int val) {
            if (isPressed(DisplayDialog.OK_BUTTON, val)) {
                getDialog().setVisible(false);
            }

            File file = null;

            if (isPressed(PreferencesDialog.EXPORT_BUTTON, val)) {
                file = Utils.getFile(parent, ".", null);
            } else {
                file = new File(Globals.getInstance().getProperty("configfile"));
            }
            if(file == null) {
                return;
            }
            String status = file.getAbsolutePath();
            int msgType = JOptionPane.INFORMATION_MESSAGE;
            if (isPressed(PreferencesDialog.SAVE_BUTTON, val) | isPressed(PreferencesDialog.EXPORT_BUTTON, val)) {
                try {
                    XMLConfig.saveConfig(file);
                    status += " gespeichert";
                } catch (IOException e) {
                    status += " konnte nicht geschrieben werden";
                    msgType = JOptionPane.ERROR_MESSAGE;
                }
                JOptionPane.showMessageDialog(this.parent, status, "Speichern der Konfiguration", msgType);
            }
        }


        /**
     * 
     */
        private static final long serialVersionUID = 1L;

        protected JTable table = null;
        protected Component parent = null;
        protected Globals globs = null;

     
        @Override
        public void setData(Globals globs) {
        }
    }

    protected static int SAVE_BUTTON = DisplayDialog.getMaxType() * 2;
    protected static int EXPORT_BUTTON = SAVE_BUTTON * 2;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
