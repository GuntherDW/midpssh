/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ConnectionSpec {
    public static final String TYPE_SSH = "ssh";

    public static final String TYPE_TELNET = "telnet";

    public String alias, type, host, username, password;

    public void read( DataInputStream in ) throws IOException {
        alias = in.readUTF();
        type = in.readUTF();
        host = in.readUTF();
        username = in.readUTF();
        password = in.readUTF();
    }

    public void write( DataOutputStream out ) throws IOException {
        out.writeUTF( alias );
        out.writeUTF( type );
        out.writeUTF( host );
        out.writeUTF( username );
        out.writeUTF( password );
    }
}