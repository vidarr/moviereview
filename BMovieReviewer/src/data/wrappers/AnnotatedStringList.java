package data.wrappers;

import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import data.Globals;

public class AnnotatedStringList extends AbstractTableModel{

    protected static String PROPERTY_NAME = "Eigenschaft";
    protected static String VALUE_NAME = "Wert";
    
    
    protected Vector<String> keys = null;
    protected Map<String, String> content;
    
    public AnnotatedStringList() {
        this(Globals.getInstance().getMap());
    }
    
    public AnnotatedStringList(Map<String, String> content) {
        if(content == null) {
            throw new IllegalArgumentException();
        }
        
        this.content = content;
        updateKeys();
    }
    
    
    
//    @Override
//    public void addTableModelListener(TableModelListener l) {
//        listeners.add(l);
//        
//    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex > 1) {
            throw new IllegalArgumentException();
        }
        return (columnIndex == 0) ? PROPERTY_NAME : VALUE_NAME;
    }

    @Override
    public int getRowCount() {
        return keys.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex > 1 || rowIndex >= keys.size()) {
            throw new IllegalArgumentException("getValueAt(): " + rowIndex + " " + columnIndex  + " max " + keys.size());
        }
        
        String str = this.keys.elementAt(rowIndex);
        return (columnIndex == 0) ? str : content.get(str);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex > 1) {
            throw new IllegalArgumentException();
        }
        return (columnIndex == 0) ? false : true;
    }

//    @Override
//    public void removeTableModelListener(TableModelListener l) {
//        listeners.remove(l);        
//    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex >= keys.size() || columnIndex > 1) {
             throw new IllegalArgumentException();
        }
        if(columnIndex == 0) {
            return;
        }
        String str = this.keys.elementAt(rowIndex);
        content.put(str, (String)aValue);
        // Listener benachrichtigen
        contentChanged(rowIndex, columnIndex);
    }
    
    
    protected void contentChanged(int rowIndex, int columnIndex) {
        fireTableCellUpdated(rowIndex, columnIndex);
//        for(TableModelListener l : listeners) {
//            l.tableChanged(new TableModelEvent(this, rowIndex, columnIndex));
//        }
    }
          
    
    public void setMap(Map<String, String> map) {
        if(map == null) {
            throw new IllegalArgumentException();
        }
        content = map;
        updateKeys();
    }
    
    public Map<String, String> getMap() {
        return content;
    }
    
    protected void updateKeys() {
        this.keys = new Vector<String>();
        for(String str : new TreeSet<String>(content.keySet())) {
            this.keys.add(str);
        }
        
        fireTableStructureChanged();
    }
    
    
    public void put(String key, String value) {
        content.put(key, value);
        updateKeys();
    }
    
    public void remove(String key) {
        content.remove(key);
        updateKeys();
    }
    

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
