/*
 * Created on Oct 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.session;

import java.io.IOException;

import telnet.Dimension;
import telnet.TelnetProtocolHandler;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TelnetSession extends Session {
    public void connect( String host ) {
        super.connect( host, new TelnetIOFilter() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see telnet.Session#defaultPort()
     */
    protected int defaultPort() {
        return 23;
    }

    private class TelnetIOFilter implements SessionIOListener {

        private TelnetProtocolHandler telnet;

        public TelnetIOFilter() {
            telnet = new TelnetProtocolHandler() {
                /** get the current terminal type */
                public String getTerminalType() {
                    return emulation.getTerminalID();
                }

                /** get the current window size */
                public Dimension getWindowSize() {
                    return new Dimension( emulation.width, emulation.height );
                }

                /** notify about local echo */
                public void setLocalEcho( boolean echo ) {
                    emulation.localecho = echo;
                }

                /** notify about EOR end of record */
                public void notifyEndOfRecord() {
                    // only used when EOR needed, like for line mode
                }

                /** write data to our back end */
                public void write( byte[] b ) throws IOException {
                    sendData( b, 0, b.length );
                }
            };
        }

        /*
         * (non-Javadoc)
         * 
         * @see terminal.TerminalIOListener#receiveData(byte[])
         */
        public void receiveData( byte[] data, int offset, int length )
                throws IOException {
            telnet.inputfeed( data, length );
            do {
                length = telnet.negotiate( data );
                if ( length > 0 ) {
                    TelnetSession.this.receiveData( data, 0, length );
                }
            }
            while ( length != -1 );
        }

        /*
         * (non-Javadoc)
         * 
         * @see terminal.TerminalIOListener#sendData(byte[])
         */
        public void sendData( byte[] data, int offset, int length )
                throws IOException {
            TelnetSession.this.sendData( data, offset, length );
        }
    }
}