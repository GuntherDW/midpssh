/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import gui.EditableMenu;

import java.util.Vector;

import app.Main;
import app.session.Session;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MacrosMenu extends EditableMenu {
	private static NewMacroForm newMacroForm = new NewMacroForm();
	
	private static EditMacroForm editMacroForm = new EditMacroForm();
	
	private MacroSet macroSet;
	
	private int macroSetIndex;
	
	public MacrosMenu( MacroSet macroSet, int macroSetIndex ) {
		super( "Macros: " + macroSet.getName() );
		this.macroSet = macroSet;
		this.macroSetIndex = macroSetIndex;
	}
	
	/* (non-Javadoc)
	 * @see gui.EditableMenu#addItems()
	 */
	protected void addItems() {
		deleteAll();

		Vector macros = macroSet.getMacros();
		if ( macros != null ) {
			for ( int i = 0; i < macros.size(); i++ ) {
				Macro macro = (Macro) macros.elementAt( i );
				String name = macro.getName();
				if ( name == null || name.length() == 0 ) {
					name = macro.getValue();
				}
				append( name, null );
			}
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doDelete(int)
	 */
	protected void doDelete( int i ) {
		macroSet.deleteMacro( i );
		addItems();
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		Session session = Main.currentSession();
		if ( session != null ) {
			Macro macro = macroSet.getMacro( i );
			if ( macro != null ) {
				session.typeString( macro.getValue() );
				session.activate();
			}
		}
		else {
			doEdit( i );
		}
	}
	protected void doEdit( int i ) {
		editMacroForm.setMacroIndices( macroSetIndex, i );
		editMacroForm.activate( this );
	}

	protected void doNew() {
		newMacroForm.activate( this );
	}
}
