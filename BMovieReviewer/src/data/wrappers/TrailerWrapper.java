package data.wrappers;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class TrailerWrapper extends StringWrapper {

    public TrailerWrapper(String s) {
        super(s);
        setMaxLen(255);
    }

//    public synchronized void setText(String s) {
//        if (s == null) {
//            throw new IllegalArgumentException();
//        }
//        if (maxLen > 0 && s.length() > maxLen) {
//            return;
//        }
//        // substitute stuff within s...
//        s = s.replace("&feature=related", "").replace("watch?", "").replace("v=", "v/");
//        if (!s.toLowerCase().matches(".*nocookie.*")) {
//            s = s.replace("youtube", "youtube-nocookie");
//        }
//        System.err.println("YouTube : " + s);
//        try {
//            remove(0, this.getLength());
//            insertString(0, s, null);
//        } catch (BadLocationException e) {
//            throw new RuntimeException();
//        }
//    }

    
    public synchronized void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (!str.toLowerCase().matches(".*nocookie.*")) 
            str = str.replace("youtube", "youtube-nocookie");
        super.insertString(offs, str.replace("&feature=related", "").replace("watch?", "").replace("v=", "v/"), a);
    }
    

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
