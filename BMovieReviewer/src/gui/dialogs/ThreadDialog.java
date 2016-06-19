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
package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import threads.ThreadRegistry;
import tools.UpdateListener;
import data.wrappers.AnnotatedStringList;

public class ThreadDialog extends DisplayDialog<ThreadRegistry> {
    
    
	public ThreadDialog(Component parent){
		super(parent, new ThreadPanel(parent), "Laufende Threads", OK_BUTTON, false);
		if(parent == null) {
			throw new IllegalArgumentException();
		}
	}
	
	
	public static class ThreadPanel extends DisplayPanel<ThreadRegistry> {

        public ThreadPanel(Component parent) {
	        super();           
	        if(parent == null) {
	            throw new IllegalArgumentException();
	        }
            this.parent = parent;
            this.setLayout(new BorderLayout());

            table = new JTable();
            ThreadList tl = new ThreadList(table);
            //tl.addTableModelListener(table);
            table.setModel(tl);
            Dimension dim = new Dimension(500, 400);
            table.setMinimumSize(dim);
            table.setVisible(true);
            JScrollPane scrlPane = new JScrollPane(table);
            scrlPane.setVisible(true);
            scrlPane.setMinimumSize(dim);
            scrlPane.setPreferredSize(dim);
            this.add(scrlPane, BorderLayout.CENTER);	        
	    }


        public void setData(ThreadRegistry reg) {
            
        }
        
        @Override
        public void onButtonPressed(int val) {
            getDialog().setVisible(false);            
        }       
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        protected JTable table = null;
        protected Component parent = null;
	}
	
	
	public static class ThreadList extends AnnotatedStringList
	implements UpdateListener {
	    
	    JTable table = null;
	    
	    public ThreadList(JTable table) {
	        super(new HashMap<String, String>());
	        if(table == null) {
	            throw new IllegalArgumentException();
	        }
	        Set<Runnable> threads = ThreadRegistry.getInstance().getThreads();
	        Map<String, String> map = new HashMap<String, String>();
	        for(Runnable r : threads) {
	            map.put(r.toString(), ThreadRegistry.getInstance().getName(r));
	        }
	        map.put("Main", "Main");
	        setMap(map);
	        this.table = table;
	        ThreadRegistry.getInstance().addListener(this);
	    }
	    
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
	        if(columnIndex > 1) {
	            throw new IllegalArgumentException();
	        }
	        return false;
	    }

        @Override
        public void added(Event e) {
            Runnable r = (Runnable) e.arg;
            put(r.toString(), ThreadRegistry.getInstance().getName(r));
            fireTableStructureChanged();
        }

        @Override
        public void removed(Event e) {
            Runnable r = (Runnable) e.arg;
            remove(r.toString()); 
            fireTableStructureChanged();
        }
       
        
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        static {
            PROPERTY_NAME = "Thread";
            VALUE_NAME = "Typ";
        }

	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
