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
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import data.Bogen;
import data.QualifiedString;
import data.wrappers.QualifiedStringList;

public class ManagedQualifiedStringTable extends JPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected String[] types = null;
    protected JTable qStrList = null;
    // protected Bogen bogen = null;
    protected int textHeight = 0;
    protected JComboBox cbCellEditor;
    protected JTextField txtCellEditor;

    public ManagedQualifiedStringTable(QualifiedStringList list, int textHeight, String[] types) {
        super();
        if (list == null || textHeight < 0 || types == null) {
            throw new IllegalArgumentException();
        }
        this.types = types;
        this.textHeight = textHeight;
        createFrame();
        setModel(list);
    }

    protected void createFrame() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Links"));
        qStrList = new JTable();

        qStrList.setRowHeight((int)(textHeight * 1.5));
        cbCellEditor = new JComboBox();
        txtCellEditor = new JTextField();
        txtCellEditor.addCaretListener(TextEditPopup.AnySelectionChangedListener.getInstance());
        txtCellEditor.addFocusListener(new InputTextListener(qStrList));
        txtCellEditor.addMouseListener(TextEditPopup.getPopupListener());
        for (int i = 0; i < this.types.length; i++) {
            cbCellEditor.addItem(this.types[i]);
        }
        this.add(new JScrollPane(qStrList));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JButton b = new JButton("Hinzufügen");
        b.addActionListener(new AddQualifiedStringListener(qStrList, this.types));
        Dimension dim = b.getPreferredSize();
        b.setMinimumSize(dim);
        b.setMaximumSize(dim);
        p.add(b);

        b = new JButton("Löschen");
        b.addActionListener(new DeleteQualifiedStringListener(qStrList));
        dim = b.getPreferredSize();
        b.setMinimumSize(dim);
        b.setMaximumSize(dim);
        p.add(b);

        this.add(p);

    }

    protected void refreshTable() {
        TableColumn colTyp = qStrList.getColumnModel().getColumn(0);
        colTyp.setCellEditor(new DefaultCellEditor(cbCellEditor));
        FontMetrics fm = qStrList.getFontMetrics(qStrList.getFont());
        int xWidth = fm.charWidth('x');
        colTyp.setPreferredWidth(Bogen.getLaengsterLinkTyp() * xWidth * 3);
        colTyp.setMaxWidth(colTyp.getPreferredWidth());
        colTyp = qStrList.getColumnModel().getColumn(1);
        colTyp.setCellEditor(new DefaultCellEditor(txtCellEditor));
    }

    public void setModel(QualifiedStringList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        qStrList.setModel(list);
        refreshTable();
    }

    protected static class AddQualifiedStringListener implements ActionListener {

        JTable list;
        String[] types = null;

        public AddQualifiedStringListener(JTable list, String[] types) {
            if (list == null || types == null) {
                throw new IllegalArgumentException();
            }
            this.list = list;
            this.types = types;
        }

        public void actionPerformed(ActionEvent e) {
            QualifiedStringList qStrList = (QualifiedStringList) list.getModel();

            qStrList.add(new QualifiedString(0, "", this.types));
            List<ListDataListener> listeners = qStrList.getListeners();

            Iterator<ListDataListener> it = listeners.iterator();
            ListDataEvent ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, qStrList.getSize() - 1, qStrList.getSize() - 1);
            while (it.hasNext()) {
                it.next().contentsChanged(ev);
            }

        }
    }

    protected static class DeleteQualifiedStringListener implements ActionListener {

        JTable list;

        public DeleteQualifiedStringListener(JTable list) {
            if (list == null) {
                throw new IllegalArgumentException();
            }
            this.list = list;
        }

        public void actionPerformed(ActionEvent e) {
            QualifiedStringList qStrList = (QualifiedStringList) list.getModel();
            int sel = list.getSelectedRow();
            if (sel < 0 || sel > list.getModel().getRowCount() - 1) { // nichts
                // selektiert?
                return;
            }
            qStrList.remove(sel);
            List<TableModelListener> listeners = qStrList.getTableListeners();

            Iterator<TableModelListener> it = listeners.iterator();
            TableModelEvent ev = new TableModelEvent(qStrList, sel, qStrList.size()); // ,
            // linkList.size()
            // -
            // 1);
            while (it.hasNext()) {
                it.next().tableChanged(ev);
            }

        }
    }
    
    protected static class InputTextListener extends FocusAdapter {
        
        protected JTable table;
        
        public InputTextListener(JTable table) {
            if(table == null) {
                throw new IllegalArgumentException();
            }
            this.table = table;
        }
        
        public void focusLost(FocusEvent e) {
            if (table.isEditing()) {
                Component c = table.getEditorComponent();
                if(c != null && c instanceof JTextField){
//                    qStrList.getModel().setValueAt(((JTextField)c).getText(), qStrList.getEditingRow(), 
//                    qStrList.getEditingColumn());
                table.getCellEditor().stopCellEditing();
                }
            }
        }
    }
}
