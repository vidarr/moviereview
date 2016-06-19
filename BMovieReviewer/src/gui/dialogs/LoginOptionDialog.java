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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoginOptionDialog extends LoginDialog {

	public LoginOptionDialog(Component parent, String[] options, boolean stdChoice) {
		this(parent, options);
		this.stdChoice = stdChoice;
	}
	
	
	public LoginOptionDialog(Component parent, String[] options) {
		super();
		
		if(options == null){
			throw new IllegalArgumentException();
		}
		this.options = options;
		
		initGUI();

		this.setLayout(new GridBagLayout());
		GridBagConstraints c;
		
		JPanel panel = createLoginPanel();
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		this.add(panel, c);
		
		panel = createOptionsPanel();	
		c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		this.add(panel, c);

		panel = createButtonPanel();	
		c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 2;
		this.add(panel, c);

		this.pack();
	}

	protected JPanel createOptionsPanel() {
		JPanel panel = new JPanel();
		GridBagConstraints c;
		
		panel.setBorder(BorderFactory.createTitledBorder("Optionen"));
		panel.setLayout(new GridBagLayout());

		checkBoxes = new JCheckBox[options.length];
		JLabel txt;
		
		for(int i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i] = new JCheckBox();
			checkBoxes[i].setSelected(this.stdChoice);
			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = i;
			panel.add(checkBoxes[i], c);
			
			txt = new JLabel(options[i]);	
			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = i;
			panel.add(txt, c);
		}

		
		return panel;
	}

	public boolean isOptionChosen(int i) {
		if(i >= checkBoxes.length) {
			throw new IllegalArgumentException();
		}
		return checkBoxes[i].isSelected();
	}
	
	
	protected JCheckBox[] checkBoxes = null;
	protected String[] options = null;
	protected boolean stdChoice = true;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
