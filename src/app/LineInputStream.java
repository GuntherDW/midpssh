/*
 * Created on 22/05/2005
 *
 */
package app;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Karl
 *
 */
public class LineInputStream extends InputStream {

    private static final int BUFSIZ = 8192;
    
    private InputStream in;
    
    private byte[] buf = new byte[BUFSIZ];
    
    private int index = 0;
    
    public LineInputStream(InputStream in) {
        this.in = in;
    }
    
    public int read() throws IOException {
        return in.read();
    }
    
    public void close() throws IOException {
        in.close();
    }
    
    public String readLine() throws IOException {
        while (true) {
            /* Read into buffer */
            int read;
            if (index < BUFSIZ) {
                read = in.read(buf, index, BUFSIZ - index);
                if (read > 0 ) {
                    index += read;
                }
            }
            else {
                read = -1;
            }
            
            /* Look for EOL */
            int eol = -1;
            int next = -1;
            
            for (int i = 0; i < index; i++) {
                if (buf[i] == '\r') {
                    if (i + 1 < index && buf[i+1] == '\n') {
                        eol = i;
                        next = i + 2;
                    }
                    else {
                        eol = i;
                        next = i + 1;
                    }
                    break;
                }
                else if (buf[i] == '\n') {
                    eol = i;
                    next = i + 1;
                    break;
                }
            }
            
            if (eol != -1) {
                String str = new String(buf, 0, eol);
                index = index - next;
                System.arraycopy(buf, next, buf, 0, index);
                return str;
            }
            else if (read == -1) {
                /* EOF */
                if (index > 0) {
                    String str = new String(buf, 0, index);
                    index = 0;
                    return str;
                }
                else {
                    return null;
                }
            }
        }
    }

}
