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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {

	protected LoginDialog(){
		super();
	}
	
	public LoginDialog(Component parent) {
		super();
		
		initGUI();

		GridBagConstraints c; 
		this.setLayout(new GridBagLayout());
		
		JPanel login = createLoginPanel();
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		this.add(login, c);
		
		JPanel buttons = createButtonPanel();
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;		
		this.add(buttons, c);
		
		this.pack();		
	}
	
	
	protected void initGUI() {
		
		login = new JTextField();
		password = new JPasswordField();
		
		this.setName("Login");
		this.setTitle("Serverlogin");
	}
	
	
	protected JPanel createLoginPanel(){
	   JPanel panel = new JPanel();
	   
	   panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		JLabel txt = new JLabel("Name");
		txt.setMinimumSize(getPreferredSize());
		panel.add(txt, c);
		
		c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		txt = new JLabel("Passwort");
		txt.setMinimumSize(getPreferredSize());
		panel.add(txt, c);
		
		Dimension dimTxtAreas;
		
		c = new GridBagConstraints();
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 0;
		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		login.setColumns(20);
		dimTxtAreas = login.getPreferredSize();
		login.setMinimumSize(dimTxtAreas);
		login.addKeyListener(new KeyFocusNextListener(password));
		login.setVisible(true);
		panel.add(login, c);
	
		c = new GridBagConstraints();
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 0;
		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		password.setPreferredSize(dimTxtAreas);
		password.setMinimumSize(getPreferredSize());
		password.addKeyListener(new EnterOKListener(this));
		panel.add(password, c);
	   
	   return panel;
	}
	
	
	protected JPanel createButtonPanel() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		JButton btnCancel = new JButton("Abbrechen");

		btnCancel.setMinimumSize(btnCancel.getPreferredSize());	
		btnCancel.setMaximumSize(btnCancel.getMinimumSize());
		btnCancel.addActionListener(new CloseButtonListener(this, JFileChooser.CANCEL_OPTION));		
		
		JButton btn = new JButton("OK");
		btn.setMinimumSize(btnCancel.getMinimumSize());
		btn.setPreferredSize(btn.getMinimumSize());
		btn.setMaximumSize(btn.getMinimumSize());
		btn.addActionListener(new CloseButtonListener(this, JFileChooser.APPROVE_OPTION));
		buttons.add(btn);
		buttons.add(btnCancel);
		
		return buttons;
	}

	
	/////////////////////////////////////////////////////
	// Ende Bau der GUI
	
	public int showLoginDialog() {
		this.setModal(true);
		this.setResizable(false);
		this.setVisible(true);
		return returnVal;
	}
	
	public String getLogin() {
		return login.getText();
	}
	
	public String getPassword() {
		return String.valueOf(password.getPassword());
	}

	public void setReturnValue(int returnVal) {
		this.returnVal = returnVal;
	}
	
	public boolean isOptionChosen(int i) {
		return true;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	protected JTextField login;
	protected JPasswordField password;
	protected int returnVal = JFileChooser.CANCEL_OPTION;
	
	
	protected static class CloseButtonListener implements ActionListener {
		
		int returnValue = 0;
		LoginDialog dialog = null;

		public CloseButtonListener(LoginDialog dialog, int returnValue) {
			if(dialog == null) 
				throw new IllegalArgumentException();
			this.returnValue = returnValue;
			this.dialog = dialog;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.dialog.setReturnValue(this.returnValue);
			this.dialog.setVisible(false);			
		}		
	}
	
	
	protected static class EnterOKListener implements KeyListener {

		protected LoginDialog dialog = null;
		
		public EnterOKListener(LoginDialog dialog) {
			if(dialog == null) {
				throw new IllegalArgumentException();
			}
			this.dialog = dialog;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				this.dialog.setReturnValue(JFileChooser.APPROVE_OPTION);
				this.dialog.setVisible(false);
			}			
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				this.dialog.setReturnValue(JFileChooser.APPROVE_OPTION);
				this.dialog.setVisible(false);
			}
		}
		
	}	
}
