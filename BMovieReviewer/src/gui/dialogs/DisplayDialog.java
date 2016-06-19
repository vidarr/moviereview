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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Allgemeiner Dialog mit Panel zur Darstellung und unterhalb mehreren Knoepfen
 * Eine neue Dialogklasse wird wie folgt erstellt:
 * 1. Eine Panelklasse schreiben, die von DisplayPanel abgeleitet ist (siehe dort)
 * 2. Eine neue Klasse von DisplayDialog ableiten, die im Konstruktor den Konstruktor von 
 *     Displaypanel aufruft
 * @author mibeer
 *
 * @param <T> Klasse, die zur Datenhaltung der Daten dient, die vom Panel dargestellt/ veraendert werden sollen
 */
public class DisplayDialog<T> extends JDialog {

//    public DisplayDialog(Component parent, DisplayPanel<T> display) {
//        this(parent, display, "", 0, false);
//    }
    
    public DisplayDialog(Component parent, DisplayPanel<T> display, String titel, int buttons, boolean modal) {
        if(parent == null || display == null || titel == null) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
        this.setDisplay(display);
        this.buttons = buttons;
        this.titel = titel;
        this.isModal = modal;
        initData();
        panel = new JPanel();
        this.add(panel);
        //this.setResizable(false);
        
    }
    

    public void showDialog(Component parent, T object) {
        display.setData(object);
        this.remove(panel);
        panel = new JPanel();
        panel.setLayout(new  BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(display);
        panel.add(createButtonPanel(buttons));
        this.add(panel);
        this.pack();
        this.setModal(isModal);
        this.setResizable(isResizable);
        this.setVisible(true);
    }
    
    
    /**
     *  registriert einen neuen Buttontyp
     * @param type ID des Typs. Muss eine Zweierpotenz sein!
     * @param label
     */
    public void addButton(int type, String label) {
        if(label == null) {
            throw new IllegalArgumentException();
        }
        buttonTypes.add(type);
        buttonLabels.add(label);
    }
    

    
    public DisplayPanel<T> getDisplay() {
        return display;
    }

    
    public void setDisplay(DisplayPanel<T> display) {
        if(display == null) {
            throw new IllegalArgumentException();
        }
        this.display = display;
        display.setDialog(this);
    }

    
    public int getButtons() {
        return buttons;
    }

    
    public void setButtons(int buttons) {
        this.buttons = buttons;
    }
 
    
    public static int getMaxType() {
        return BUTTON_TYPE[BUTTON_TYPE.length - 1];
    }
    
    /**
     * @param button
     * @param val
     * @return  true, wenn val angibt, dass Knopf mit der ID button gedrueckt wurde
     */
    public static boolean isPressed(int button, int val) {
        return (val & button) == button;
    }
    
    /////////////////////////////////////////////////////////
    // Hilfsklassen
    
    /**
     * Panel, welches mit DisplayDialog angezeigt werden kann.
     * Wird im Dialog ein Knopf gedrueckt, wird die Methode onButtonPressed aufgerufen
     * Der umgebende Dialog ist mit getDialog() erreichbar
     * Wichtig:  newInstance dient dazu, eine neue Panelinstanz anzulegen. Ihm wird die Elternkomponente
     * sowie eine Instanz der Datenklasse T uebergeben, deren Daten das Panel darstellen soll
     * @author mibeer
     *
     */
    public static abstract class DisplayPanel<T> extends JPanel implements ContentChangedListener<T>{
        
        /**
         * Informiert Panel, dass ein Knopf gedrueckt wurde
         * @param val Identifiziert Knopf anhand Konstanten aus JOptionPane
         */
        public abstract void onButtonPressed(int val);
     
        
        /**
         * Liefert den assoziierten DisplayDialog zurueck
         * @return
         */
        public DisplayDialog<T> getDialog() {
            return dialog;
        }
        
        public void setDialog(DisplayDialog<T> dialog) {
            if(dialog == null) {
                throw new IllegalArgumentException();
            }
            this.dialog = dialog;
        }
        
        
        //public abstract DisplayPanel<T> newInstance(Component parent, T obj);       
        public abstract void setData(T data);
        
        private DisplayDialog<T> dialog = null;


        /**
         * Wird aufgerufen, wenn Bild sich veraendert hat
         */
        public void contentChanged(T data){
            setData(data);
        }
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }

    
    public static interface ContentChangedListener<T> {
        public void contentChanged(T data);
    }
    
    
    ///////////////////////////////////////////////////////
    // Internes
    ///////////////////////////////////////////////////////
    

    protected JPanel createButtonPanel(int buttons) {
        JPanel panel = new JPanel();
        JButton button = null;
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        // Alle moeglichen Knopfarten durchgehen und falls notwendig erzeugen
        for(int index = 0; index < buttonTypes.size(); index++) {
            int type = this.buttonTypes.get(index);
            if((buttons & type) == type) {
                button = new JButton(buttonLabels.get(index));
                button.setPreferredSize(button.getMinimumSize());
                button.addActionListener(new ButtonListener<T>(this.display, type));
                panel.add(button);
            }
        }
        
        return panel;
    }
    
    
    
    
    protected void initData() {
        buttonTypes = new Vector<Integer>();
        buttonLabels = new Vector<String>();
        for(int i = 0; i < BUTTON_TYPE.length; i++) {
            buttonTypes.add(new Integer(BUTTON_TYPE[i]));
            buttonLabels.add(BUTTON_LABELS[i]);
        }
    }
    
    //////////////////////////////////////////////////
    // Konstanten
    
    //////////////////////////////////////////////////
    // Standardbuttons
    public static final int[] BUTTON_TYPE = {
        1, 2, 4, 8
    };
    
    public static final String[] BUTTON_LABELS = {
        "OK", "Abbrechen", "Ändern", "Hinzufügen"
    };
    
    public static final int OK_BUTTON = BUTTON_TYPE[0];
    public static final int CANCEL_BUTTON = BUTTON_TYPE[1];
    public static final int CHANGE_BUTTON = BUTTON_TYPE[2];
    public static final int ADD_BUTTON = BUTTON_TYPE[3];
    
  
    //////////////////////////////////////////////////////
    // Datenfelder  
   
    protected Component parent = null;
    protected Vector<Integer> buttonTypes;
    protected Vector<String> buttonLabels;
    protected String titel = "";
    protected DisplayPanel<T> display = null;
    protected JPanel panel;
    protected int buttons = 0;
    protected boolean isModal = false;
    protected boolean isResizable = false;
   
    
    ///////////////////////////////////////////////////////
    // Listeners
    protected static class ButtonListener<T> implements ActionListener {
        
        
        DisplayPanel<T> display = null;
        int type = 0;
        
        /**
         * @param display
         * @param type Knopftyp
         */
        public ButtonListener(DisplayPanel<T> display, int type) {
            if(display == null) {
                throw new IllegalArgumentException();
            }
            this.display = display;
            this.type = type;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            this.display.onButtonPressed(type);
        }
        
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
