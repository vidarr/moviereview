package gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultEditorKit;

import com.inet.jortho.SpellChecker;

public class TextEditPopup extends JMenu{
	
	protected TextEditPopup() {
		super("Bearbeiten");

    	JMenuItem item = new JMenuItem(new DefaultEditorKit.CopyAction());
        item.setText("Kopieren");
        item.setMnemonic(KeyEvent.VK_C);
        this.add(item);
        item.setEnabled(false);

        TextEditPopup.AnySelectionChangedListener.getInstance().addMenuItem(item);
        
        item = new JMenuItem(new DefaultEditorKit.CutAction());
        item.setText("Ausschneiden");
        item.setMnemonic(KeyEvent.VK_X);
        this.add(item);
        item.setEnabled(false);
        
        TextEditPopup.AnySelectionChangedListener.getInstance().addMenuItem(item);

        item = new JMenuItem(new DefaultEditorKit.PasteAction());
        item.setText("Einfügen");
        item.setMnemonic(KeyEvent.VK_V);
        item.setEnabled(false);
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new ClipboardPasteChanged(item));
        this.add(item);
	}
   

	public static class MouseClickedListener implements MouseListener {

		protected JPopupMenu menu = null;
		
		public MouseClickedListener(JPopupMenu menu) {
			if(menu == null) {
				throw new IllegalArgumentException();
			}
			this.menu = menu;
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			showPopup(arg0);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			showPopup(arg0);
		}
		
		protected void showPopup(MouseEvent e) {
			if(e.isPopupTrigger()) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	
	public static class PopupListener extends MouseClickedListener {
		
		public PopupListener () {
			super(TextEditPopup.getTextPopup());
		}
	}
	

	public static class PopupSpellCheckListener extends MouseClickedListener {
		
		public PopupSpellCheckListener () {
			super(TextEditPopup.getTextSpellCheckPopup());
		}
	}

	
    public static class AnySelectionChangedListener implements CaretListener {

    	protected List<JMenuItem> menuItems;	
    	
    	protected static AnySelectionChangedListener listener = null;
    	
    	public static AnySelectionChangedListener getInstance() {
    		if(listener == null) {
    			listener = new AnySelectionChangedListener();
    		}
    		return listener;
    	}
    	
    	protected AnySelectionChangedListener() {
    		menuItems = new LinkedList<JMenuItem>();
    	}
    	
    	public void addMenuItem(JMenuItem item) {
    		if(item == null) {
    			throw new IllegalArgumentException();
    		}
    		menuItems.add(item);
    	}
    	
    	public void removeMenuItem(JMenuItem item) {
    		if(item == null) {
    			throw new IllegalArgumentException();
    		}
    		menuItems.remove(item);
    	}
    	
		@Override
		public void caretUpdate(CaretEvent e) {
			boolean b = false;
			if(e.getMark() != e.getDot()) {
				b = true;
			}
			for(JMenuItem item : menuItems) {
				item.setEnabled(b);
			}				
		}	
    }
    
    
    public static JMenu getTextEditMenu() {
    	JMenu textEditMenu = new TextEditPopup();
    	return textEditMenu;
     }
    
    
    public static JPopupMenu getTextPopup() {
    	if(textPopup == null) {
    		textPopup = new JPopupMenu();
    		textPopup.add(getTextEditMenu());
    	}
    	return textPopup;
    }


    public static JPopupMenu getTextSpellCheckPopup() {
    	if(textSpellCheckPopup == null) {
    		textSpellCheckPopup = new JPopupMenu();
    		textSpellCheckPopup.add(getTextEditMenu());
    		textSpellCheckPopup.add(SpellChecker.createCheckerMenu());
    	}
    	return textSpellCheckPopup;
    }

    
    public static MouseListener getPopupListener() {
    	if(mouseListener == null) {
    		mouseListener = new TextEditPopup.PopupListener();
    	}
    	return mouseListener;
    }
    
    public static MouseListener getPopupSpellCheckListener() {
    	if(mouseSpellCheckListener == null) {
    		mouseSpellCheckListener = new TextEditPopup.PopupSpellCheckListener();
    	}
    	return mouseSpellCheckListener;
    }

    
    protected static MouseListener mouseListener = null;
    protected static MouseListener mouseSpellCheckListener = null;
    protected static JPopupMenu textPopup = null;
    protected static JPopupMenu textSpellCheckPopup = null;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    
    protected static class ClipboardPasteChanged implements FlavorListener {
    
    	protected JMenuItem pasteEntry;
    	
    	public ClipboardPasteChanged(JMenuItem pasteEntry) {
    		if(pasteEntry == null) {
    			throw new IllegalArgumentException();
    		}
    		this.pasteEntry = pasteEntry;
    	}	

		@Override
		public void flavorsChanged(FlavorEvent arg0) {
			Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
			if(content != null &&
				content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				pasteEntry.setEnabled(true);
			} else {
				pasteEntry.setEnabled(false);
			}
		}
    }
    
}
