package gui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ExtendedList extends List {
    /**
     * @param arg0
     * @param arg1
     */
    public ExtendedList( String arg0, int arg1 ) {
        super( arg0, arg1 );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public ExtendedList( String arg0, int arg1, String[] arg2, Image[] arg3 ) {
        super( arg0, arg1, arg2, arg3 );
        // TODO Auto-generated constructor stub
    }

    public void deleteAll() {
        while ( size() > 0 ) {
            delete( size() - 1 );
        }
    }
}