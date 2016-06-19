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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
	

	JLabel status = null;
	
	public StatusBar() {
	    this("");
	}
	
	public StatusBar(String s) {
		super();
		
		Dimension dim2, dim = new Dimension(100, 20);
		dim2 = new Dimension(Integer.MAX_VALUE, dim.height);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setMinimumSize(dim);
		this.setMaximumSize(dim2);
		status = new JLabel(s);
		status.setMinimumSize(dim);
		status.setPreferredSize(dim);
		dim2 = new Dimension(Integer.MAX_VALUE, dim.height);
		status.setMaximumSize(dim2);
		this.add(status);
	}
	
	
	public void setStatus(String status){
		if(status == null) {
			status = "";
		}
		this.status.setText(status);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
