/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import gui.EditableMenu;

import java.util.Vector;

import app.session.MacroSetManager;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MacroSetsMenu extends EditableMenu {
	
	private static NewMacroSetForm newMacroSetForm = new NewMacroSetForm();
	
	private static EditMacroSetForm editMacroSetForm = new EditMacroSetForm();
	
	public MacroSetsMenu() {
		super( "Macro Sets" );
	}
	
	/* (non-Javadoc)
	 * @see gui.EditableMenu#addItems()
	 */
	protected void addItems() {
		deleteAll();

		Vector macroSets = MacroSetManager.getMacroSets();
		if ( macroSets != null ) {
			for ( int i = 0; i < macroSets.size(); i++ ) {
				MacroSet macroSet = (MacroSet) macroSets.elementAt( i );
				append( macroSet.getName(), null );
			}
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doDelete(int)
	 */
	protected void doDelete( int i ) {
		MacroSetManager.deleteMacroSet( i );
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		MacroSet macroSet = MacroSetManager.getMacroSet( i );
		MacrosMenu macrosMenu = new MacrosMenu( macroSet, i );
		macrosMenu.activate( this );
	}
	protected void doEdit( int i ) {
		editMacroSetForm.setMacroSetIndex( i );
		editMacroSetForm.activate( this );
	}

	protected void doNew() {
		newMacroSetForm.activate( this );
	}
}
