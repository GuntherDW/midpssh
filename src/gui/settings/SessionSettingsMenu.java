/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.settings;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SessionSettingsMenu extends SettingsMenu {
	
	public SessionSettingsMenu() {
		super( "Session Settings" );
		
		//append( "Screen Size", null );
	}
	/* (non-Javadoc)
	 * @see gui.settings.SettingsMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		switch ( i ) {
			case SETTINGS_OPTIONS:
				doScreenSize();
				break;
			default:
				super.doSelect( i );
				break;
		}
	}
	
	private void doScreenSize() {
		ScreenSizeForm screenSizeForm = new ScreenSizeForm();
		screenSizeForm.activate( this );
	}
}
