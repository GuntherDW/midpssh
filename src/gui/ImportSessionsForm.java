/*
 * Created on 22/05/2005
 *
 */
package gui;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import app.LineInputStream;
import app.Main;
import app.SessionManager;
import app.SessionSpec;
import app.Settings;

/**
 * Import sessions using an HTTP connection. Parses the returned page looking for lines of the form:
 * ssh username@hostname[:port] alias
 * telnet hostname[:port] alias
 * @author Karl
 *
 */
public class ImportSessionsForm extends ExtendedTextBox implements Runnable {

    private String url;
    
    /**
     * @param title
     * @param text
     * @param maxSize
     * @param constraints
     */
    public ImportSessionsForm() {
        super("Import Sessions URL", Settings.sessionsImportUrl, 255, TextField.ANY);
        
        addCommand(MessageForm.okCommand);
        addCommand(MessageForm.backCommand);
    }
    
    /* (non-Javadoc)
     * @see gui.ExtendedTextBox#handleText(javax.microedition.lcdui.Command, java.lang.String)
     */
    protected boolean handleText(Command command, String url) {
        this.url = url;
        new Thread(this).start();
        return false;
    }
    
    public void run() {
        HttpConnection c = null;
        LineInputStream in = null;
        
        try {
            int imported = 0;
            
            c = (HttpConnection) Connector.open(url);
            int rc = c.getResponseCode();
            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + rc);
            }
            
            in = new LineInputStream(c.openInputStream());
            String line = in.readLine();
            while (line != null) {
                String username = "", host = null, alias = "";
                SessionSpec spec = null;
                
                if (line.startsWith("ssh ")) {
                    int soh = 4;
                    int eoh = line.indexOf(' ', soh);
                    if (eoh != -1) {
                        int at = line.indexOf('@', soh);
                        if (at != -1 && at < eoh) {
                            /* Contains username */
                            username = line.substring(soh, at);
                            soh = at + 1;
                        }
                        
                        host = line.substring(soh, eoh);
                        alias = line.substring(eoh + 1).trim();
                        
                        spec = new SessionSpec();
                        spec.type = SessionSpec.TYPE_SSH;
                    }
                }
                else if (line.startsWith("telnet ")) {
                    int soh = 7;
                    int eoh = line.indexOf(' ', soh);
                    if (eoh != -1) {
                        host = line.substring(soh, eoh);
                        alias = line.substring(eoh + 1).trim();
                        
                        /* Insert or replace in Sessions list */
                        spec = new SessionSpec();
                        spec.type = SessionSpec.TYPE_TELNET;
                    }
                }
                
                if (spec != null) {
                    /* Insert or replace in Sessions list */
                    spec.alias = alias;
                    spec.host = host;
                    spec.username = username;
                    spec.password = "";
                    appendOrReplaceSession(spec);
                    
                    imported++;
                }
                
                line = in.readLine();
            }
            
            back.activate();
            Settings.sessionsImportUrl = url;
            Settings.saveSettings();
            
            Alert alert = new Alert( "Import Complete" );
            alert.setType( AlertType.INFO );
            alert.setString( "Imported " + imported + " sessions" );
            Main.alert(alert, (Displayable)back);
        }
        catch (Exception e) {
            e.printStackTrace();
            
            Alert alert = new Alert( "Import Failed" );
            alert.setType( AlertType.ERROR );
        
            alert.setString( e.getMessage() );
            alert.setTimeout( Alert.FOREVER );
            Main.alert(alert, this);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private void appendOrReplaceSession(SessionSpec newSpec) {
        SessionSpec spec = null;
        int replaceAt = -1;
        
        Vector sessions = SessionManager.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            spec = (SessionSpec) sessions.elementAt(i);
            if (spec.type.equals(newSpec.type)) {
                if (newSpec.alias.equals(spec.alias)) {
                    /* Replace this one */
                    replaceAt = i;
                    break;
                }
            }
        }
        
        if (replaceAt == -1) {
            SessionManager.addSession(newSpec);
        }
        else {
            spec.alias = newSpec.alias;
            spec.username = newSpec.username;
            spec.host = newSpec.host;
            SessionManager.replaceSession(replaceAt, spec);
        }
    }
}
