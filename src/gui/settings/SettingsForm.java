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

import gui.EditableForm;
import gui.MessageForm;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import ssh.v2.DHKeyExchange;
import app.Main;
import app.Settings;

/**
 * @author Karl von Randow
 */
public class SettingsForm extends EditableForm {
    
    public static final int MODE_NETWORK = 1;
    
    public static final int MODE_INTERFACE = 2;
    
    public static final int MODE_FONTS = 3;
    
//#ifdef ssh2   
    public static final int MODE_SSH = 4;
//#endif
    
    private int mode;
    
	protected TextField tfType = new TextField( "Type", "", 20, TextField.ANY );
	
	protected TextField tfCols = new TextField( "Cols", "", 3, TextField.NUMERIC );
	
	protected TextField tfRows = new TextField( "Rows", "", 3, TextField.NUMERIC );
	
	protected TextField tfFg = new TextField( "Foreground", "", 6, TextField.ANY );
	
	protected TextField tfBg = new TextField( "Background", "", 6, TextField.ANY );
//#ifdef midp2    
    protected ChoiceGroup cgFullscreen = new ChoiceGroup( "Full Screen", ChoiceGroup.EXCLUSIVE );
    
    protected ChoiceGroup cgRotated = new ChoiceGroup( "Orientation", ChoiceGroup.EXCLUSIVE );
    
    protected ChoiceGroup cgPredictiveText = new ChoiceGroup("Predictive Text", ChoiceGroup.EXCLUSIVE);
//#endif
    
    protected ChoiceGroup cgFont = new ChoiceGroup( "Font Size", ChoiceGroup.EXCLUSIVE );
    
    protected ChoiceGroup cgPolling = new ChoiceGroup("Polling I/O", ChoiceGroup.EXCLUSIVE);
	
//#ifdef ssh2   
    protected ChoiceGroup cgSsh = new ChoiceGroup("Preferred Protocol", ChoiceGroup.EXCLUSIVE);
    
    protected ChoiceGroup cgSshKeys = new ChoiceGroup("Store Keys", ChoiceGroup.EXCLUSIVE);
    
    protected ChoiceGroup cgSshKeySize = new ChoiceGroup("Key Size", ChoiceGroup.EXCLUSIVE);
    
    private static final int[] sshKeySizes = new int[] { 32, 64, 128, 256, 512, 1024 };
//#endif
    
    private void booleanChoiceGroup(ChoiceGroup cg) {
    	cg.append( "On", null );
        cg.append( "Off", null );
    }
    
	public SettingsForm( String title, int mode ) {
		super( title );
        
        this.mode = mode;
        
        switch ( mode ) {
        case MODE_NETWORK:
        {
            append( new StringItem( "Terminal Type", "The terminal type reported to the remote server. The default type is VT320." ) );
            append( tfType );
            booleanChoiceGroup(cgPolling);
            append(cgPolling);
        }
        break;
        case MODE_INTERFACE:
        {
//#ifdef midp2
            booleanChoiceGroup(cgFullscreen);
            append( cgFullscreen );
//#endif
            
//#ifdef midp2
            cgRotated.append( "Normal", null );
            cgRotated.append( "Landscape", null );
            cgRotated.append( "Landscape Flipped", null );
            append( cgRotated );
//#else
            append( new StringItem( "Orientation", "Not available on this device." ) );
//#endif

            append( new StringItem( "Terminal Size", "The default is to use the maximum available screen area." ) );
            append( tfCols );
            append( tfRows );
            
            //#ifdef midp2
            booleanChoiceGroup(cgPredictiveText);
            append(cgPredictiveText);
            //#endif
        }
        break;
        case MODE_FONTS:
        {
            cgFont.append( "Tiny", null );
            cgFont.append( "Small", null );
            cgFont.append( "Medium", null );
            cgFont.append( "Large", null );
            append( cgFont );
            
            append( tfFg );
            append( tfBg );
        }
        break;
//#ifdef ssh2
        case MODE_SSH:
        {
            cgSsh.append( "SSH1", null);
            cgSsh.append( "SSH2", null);
            append(cgSsh);
            
            booleanChoiceGroup(cgSshKeys);
            append(cgSshKeys);
            
            for (int i = 0; i < sshKeySizes.length; i++) {
                cgSshKeySize.append("" + sshKeySizes[i], null);
            }
            append(cgSshKeySize);
        }
        break;
//#endif
        }
        
        addCommand(MessageForm.okCommand);
	}
    
    /* (non-Javadoc)
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction( Command command, Displayable arg1 ) {
        if ( command == MessageForm.okCommand ) {
            save();
        }
        else {
            super.commandAction( command, arg1 );
        }
    }
    
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
        switch ( mode ) {
        case MODE_NETWORK:
        {
            tfType.setString( Settings.terminalType );
        	cgPolling.setSelectedIndex(Settings.pollingIO ? 0 : 1, true);
        }
        break;
        case MODE_INTERFACE:
        {
//#ifdef midp2
            cgFullscreen.setSelectedIndex( Settings.terminalFullscreen ? 0 : 1, true );
//#endif
            
//#ifdef midp2
            switch ( Settings.terminalRotated ) {
            case Settings.ROT_NORMAL:
                cgRotated.setSelectedIndex( 0, true );
                break;
            case Settings.ROT_270:
                cgRotated.setSelectedIndex( 1, true );
                break;
            case Settings.ROT_90:
                cgRotated.setSelectedIndex( 2, true );
                break;
            }
//#endif
            
            int cols = Settings.terminalCols;
            int rows = Settings.terminalRows;
            if ( cols > 0 ) {
                tfCols.setString( "" + cols );
            }
            else {
                tfCols.setString( "" );
            }
            if ( rows > 0 ) {
                tfRows.setString( "" + rows );
            }
            else {
                tfRows.setString( "" );
            }
            
            //#ifdef midp2
            cgPredictiveText.setSelectedIndex(Settings.predictiveText ? 0 : 1, true);
            //#endif
        }
        break;
        case MODE_FONTS:
        {
            switch ( Settings.fontMode ) {
            case Settings.FONT_NORMAL:
                cgFont.setSelectedIndex( 0, true );
                break;
            case Settings.FONT_SMALL:
                cgFont.setSelectedIndex( 1, true );
                break;
            case Settings.FONT_MEDIUM:
                cgFont.setSelectedIndex( 2, true );
                break;
            case Settings.FONT_LARGE:
                cgFont.setSelectedIndex( 3, true );
                break;
            }
            
            tfFg.setString( toHex( Settings.fgcolor ) );
            tfBg.setString( toHex( Settings.bgcolor ) );
        }
        break;
//#ifdef ssh2
        case MODE_SSH:
        {
            switch (Settings.sshVersionPreferred) {
            case 2:
                cgSsh.setSelectedIndex(1, true);
                break;
            default:
                cgSsh.setSelectedIndex(0, true);
                break;
            }
            cgSshKeys.setSelectedIndex(Settings.ssh2StoreKey ? 0 : 1, true);
            for (int i = 0; i < sshKeySizes.length; i++) {
                if (Settings.ssh2KeySize == sshKeySizes[i]) {
                    cgSshKeySize.setSelectedIndex(i, true);
                    break;
                }
            }
        }
        break;
//#endif
        }
		
		super.activate();
	}
    
    private void save() {
        boolean ok = doSave();
        if ( ok ) {
            Settings.saveSettings( );
            doBack();
        }
    }
	
	protected boolean doSave() {
        switch ( mode ) {
        case MODE_NETWORK:
        {
            Settings.terminalType = tfType.getString();
        	Settings.pollingIO = cgPolling.getSelectedIndex() == 0;
        }
        break;
        case MODE_INTERFACE:
        {
//#ifdef midp2
            Settings.terminalFullscreen = cgFullscreen.getSelectedIndex() == 0;
//#endif
                
//#ifdef midp2
            switch ( cgRotated.getSelectedIndex() ) {
            case 0:
                Settings.terminalRotated = Settings.ROT_NORMAL;
                break;
            case 1:
                Settings.terminalRotated = Settings.ROT_270;
                break;
            case 2:
                Settings.terminalRotated = Settings.ROT_90;
                break;
            }
//#endif
                
            try {
                Settings.terminalCols = Integer.parseInt( tfCols.getString() );
            }
            catch ( NumberFormatException e ) {
                Settings.terminalCols = 0;
            }
            try {
                Settings.terminalRows = Integer.parseInt( tfRows.getString() );
            }
            catch ( NumberFormatException e ) {
                Settings.terminalRows = 0;
            }
            
            //#ifdef midp2
            Settings.predictiveText = cgPredictiveText.getSelectedIndex() == 0;
            //#endif
        }
        break;
        case MODE_FONTS:
        {
            switch (cgFont.getSelectedIndex()) {
            case 0:
                Settings.fontMode = Settings.FONT_NORMAL;
                break;
            case 1:
                Settings.fontMode = Settings.FONT_SMALL;
                break;
            case 2:
                Settings.fontMode = Settings.FONT_MEDIUM;
                break;
            case 3:
                Settings.fontMode = Settings.FONT_LARGE;
                break;
            }
            try {
                int col = fromHex( tfFg.getString() );
                Settings.fgcolor = col;
            }
            catch ( NumberFormatException e ) {
                Settings.fgcolor = Settings.DEFAULT_FGCOLOR;
            }
            
            try {
                int col = fromHex( tfBg.getString() );
                Settings.bgcolor = col;
            }
            catch ( NumberFormatException e ) {
                Settings.bgcolor = Settings.DEFAULT_BGCOLOR;
            }
        }
        break;
//#ifdef ssh2
        case MODE_SSH:
        {
            Settings.sshVersionPreferred = cgSsh.getSelectedIndex() == 1 ? 2 : 1;
            boolean ssh2StoreKey = cgSshKeys.getSelectedIndex() == 0;
            Settings.ssh2StoreKey = ssh2StoreKey;
            int newKeySize = sshKeySizes[cgSshKeySize.getSelectedIndex()];
            if (newKeySize != Settings.ssh2KeySize) {
                Settings.ssh2KeySize = newKeySize;
                Settings.ssh2x = null;
                Settings.ssh2y = null;
            }
            
            if (ssh2StoreKey) {
                if (Settings.ssh2x == null || Settings.ssh2y == null) {
                    /* Pregenerate ssh2 key */
                    Alert alert = new Alert("MidpSSH");
                    alert.setString("Please wait while the SSH2 key is generated");
                    alert.setTimeout(1);
                    alert.setCommandListener(new CommandListener() {
                        /* (non-Javadoc)
                         * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
                         */
                        public void commandAction(Command arg0, Displayable arg1) {
                            byte[][] keys = DHKeyExchange.generateKeyPairBytes(Settings.ssh2KeySize);
                            Settings.ssh2x = keys[0];
                            Settings.ssh2y = keys[1];
                            Settings.saveSettings();
//#ifdef midp2
                            Main.getDisplay().vibrate(300);
//#endif
                            doBack();
                        }
                    });
                    Main.setDisplay(alert);
                    return false;
                }
            }
            else {
                Settings.ssh2x = null;
                Settings.ssh2y = null;
            }
        }
        break;
//#endif
        }
		return true;
	}
	
	private static int fromHex( String hex ) throws NumberFormatException {
		hex = hex.toLowerCase();
		int total = 0;
		for ( int i = 0; i < hex.length(); i++ ) {
			total <<= 4;
			char c = hex.charAt( i );
			if ( c >= '0' && c <= '9' ) {
				total += ( c - '0' );
			}
			else if ( c >= 'a' && c <= 'f' ) {
				total += ( c - 'a' ) + 10;
			}
			else {
				throw new NumberFormatException( hex );
			}
		}
		return total;
	}
	
	private static String toHex( int i ) {
		char[] buf = new char[32];
		int charPos = 32;
		do {
		    buf[--charPos] = digits[i & 15];
		    i >>>= 4;
		//} while (i != 0);
		} while ( charPos > 26 || i != 0 );

		return new String(buf, charPos, (32 - charPos));
	}
	
    private final static char[] digits = {
    		'0' , '1' , '2' , '3' , '4' , '5' ,
    		'6' , '7' , '8' , '9' , 'a' , 'b' ,
    		'c' , 'd' , 'e' , 'f' 
    	    };
}
