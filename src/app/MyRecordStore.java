/*
 * Created on Oct 13, 2004
 *
 */
package app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * @author Karl
 * 
 */
public abstract class MyRecordStore {

    protected Vector load(String rmsName, boolean sort) {
        try {
            RecordStore rec = RecordStore.openRecordStore(rmsName, false);

            Vector vector = new Vector();

            int n = rec.getNumRecords();
            for (int i = 0; i < n; i++) {
                byte[] data = rec.getRecord(i);
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
                try {
                    vector.addElement(read(in));
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                in.close();
            }
            rec.closeRecordStore();
            
//#ifndef small
            if (sort) {
                insertSort(vector);
            }
//#endif
            return vector;
        } catch (RecordStoreFullException e) {
            // e.printStackTrace();
        } catch (RecordStoreNotFoundException e) {
            // Start with an empty Vector
        } catch (RecordStoreException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return new Vector();
    }

    protected void insertSort(Vector v) {
        int in, out;
        int n = v.size();

        for (out = 1; out < n; out++) // out is dividing line
        {
            Object temp = v.elementAt(out);
            in = out;
            while (in > 0 && compare(v.elementAt(in - 1), temp) >= 0) {
                v.setElementAt(v.elementAt(in - 1), in);
                --in;
            }
            v.setElementAt(temp, in);
        }
    }
    
    protected int compare(Object a, Object b) {
        return 0;
    }

    protected void save(String rmsName, Vector vector) {
        if (vector != null) {
            try {
                try {
                    RecordStore.deleteRecordStore(rmsName);
                } catch (RecordStoreNotFoundException e1) {

                }

                RecordStore rec = RecordStore.openRecordStore(rmsName, true);
                for (int i = 0; i < vector.size(); i++) {
                    Object ob = vector.elementAt(i);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    DataOutputStream dout = new DataOutputStream(out);
                    try {
                        write(dout, ob);
                        dout.close();

                        byte[] data = out.toByteArray();
                        rec.addRecord(data, 0, data.length);
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                }

                rec.closeRecordStore();
            } catch (RecordStoreFullException e) {
                // e.printStackTrace();
            } catch (RecordStoreNotFoundException e) {
                // e.printStackTrace();
            } catch (RecordStoreException e) {
                // e.printStackTrace();
            }
        }
    }

    protected abstract Object read(DataInputStream in) throws IOException;

    protected abstract void write(DataOutputStream out, Object ob) throws IOException;
}
