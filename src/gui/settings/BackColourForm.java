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
public class BackColourForm extends ColourForm {
	public BackColourForm() {
		super( "Background Colour" );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Settings settings = SettingsManager.getSettings();
		initColour( settings.bgcolor );
		super.activate();
	}
	/* (non-Javadoc)
	 * @see gui.settings.ColourForm#doSave()
	 */
	protected void doSave() {
		Settings settings = SettingsManager.getSettings();
		
		int color = parseColour();
		if ( color != -1 ) {
			settings.bgcolor = color;
			SettingsManager.saveSettings( settings );
			
			doBack();
		}
	}
	/* (non-Javadoc)
	 * @see gui.settings.ColourForm#doDefault()
	 */
	protected void doDefault() {
		Settings settings = SettingsManager.getSettings();
		
		settings.bgcolor = Settings.DEFAULT_BGCOLOR;
		SettingsManager.saveSettings( settings );
			
		doBack();
	}
}
