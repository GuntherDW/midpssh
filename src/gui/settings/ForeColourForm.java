/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.settings;

import app.Settings;
import app.SettingsManager;

/**
 * @author Karl von Randow
 */
public class ForeColourForm extends ColourForm {
	public ForeColourForm() {
		super( "Foreground Colour" );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Settings settings = SettingsManager.getSettings();
		initColour( settings.fgcolor );
		super.activate();
	}
	/* (non-Javadoc)
	 * @see gui.settings.ColourForm#doSave()
	 */
	protected void doSave() {
		Settings settings = SettingsManager.getSettings();
		
		int color = parseColour();
		if ( color != -1 ) {
			settings.fgcolor = color;
			SettingsManager.saveSettings( settings );
			
			doBack();
		}
	}
	/* (non-Javadoc)
	 * @see gui.settings.ColourForm#doDefault()
	 */
	protected void doDefault() {
		Settings settings = SettingsManager.getSettings();
		
		settings.fgcolor = Settings.DEFAULT_FGCOLOR;
		SettingsManager.saveSettings( settings );
			
		doBack();
	}
}
