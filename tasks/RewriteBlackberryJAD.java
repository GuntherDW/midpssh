import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Created on 15/04/2005
 *
 */

/**
 * @author Karl
 *
 */
public class RewriteBlackberryJAD {

    public static void main(String[] args) throws IOException {
        File jadIn = new File(args[0]);
        File jadOut = new File(args[1]);
        String cod1 = args[2];
        String cod2 = args[3];
        long length1 = Long.parseLong(args[4]);
        long length2 = Long.parseLong(args[5]);
        
        BufferedReader in = new BufferedReader(new FileReader(jadIn));
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(jadOut)));
        
        String line = in.readLine();
        while (line != null) {
            if (line.startsWith("RIM-COD-URL:") || line.startsWith("RIM-COD-SHA1:") || line.startsWith("RIM-COD-Size:")) {
                /* Ignore these lines */
            }
            else {
                out.println(line);
            }
            line = in.readLine();
        }
        
        out.println("RIM-COD-URL-1: " + cod1);
        out.println("RIM-COD-URL-2: " + cod2);
        out.println("RIM-COD-Size-1: " + length1);
        out.println("RIM-COD-Size-2: " + length2);
        
        in.close();
        out.close();
    }
}
