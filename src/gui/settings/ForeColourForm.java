/* This file is part of "MidpSSH".
 * Copyright (c) 2004 Karl von Randow.
 * 
 * MidpSSH is based upon Telnet Floyd and FloydSSH by Radek Polak.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 *
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
