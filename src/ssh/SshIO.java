/*
 * This file is part of "The Java Telnet Application".
 *
 * (c) Matthias L. Jugel, Marcus Meiﬂner 1996-2002. All Rights Reserved.
 * The file was changed by Radek Polak to work as midlet in MIDP 1.0
 * 
 * This file has been modified by Karl von Randow for MidpSSH.
 *
 * Please visit http://javatelnet.org/ for updates and contact.
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
 */

package ssh;

import java.io.IOException;
import java.util.Random;

import ssh.v1.BigInteger;
import ssh.v1.Cipher;
import ssh.v1.MD5;
import ssh.v1.SshCrypto;
import ssh.v1.SshPacket1;
import ssh.v2.DHKeyExchange;
import ssh.v2.SHA1Digest;
import ssh.v2.SshCrypto2;
import ssh.v2.SshPacket2;
import app.Settings;
import app.session.SshSession;

/**
 * Secure Shell IO
 * 
 * @author Marcus Meissner
 * @version $Id$
 */
public class SshIO {

    private static MD5 md5 = new MD5();

    private SshSession sshSession;

    /**
     * variables for the connection
     */
    private String idstr = ""; // ("SSH-<protocolmajor>.<protocolminor>-<version>\n")

    private String idstr_sent = "SSH/JTA (c) Marcus Meissner, Matthias L. Jugel\n";

    /**
     * Debug level. This results in additional diagnostic messages on the java
     * console.
     */
    // private static int debug = 0;
    /**
     * State variable for Ssh negotiation reader
     */
    private SshCrypto crypto = null;

    String cipher_type;// = "IDEA";

    public static java.util.Random rnd = new java.util.Random();

    private int remotemajor, remoteminor;

    private int mymajor, myminor;

    private int useprotocol;

    public String login, password;

    // nobody is to access those fields : better to use pivate, nobody knows :-)

    private String dataToSend = null;

    private String hashHostKey = null; // equals to the applet parameter if any

    private byte lastPacketSentType;

    // phase : handleBytes
    private int phase = 0;

    private final int PHASE_INIT = 0;

    private final int PHASE_SSH_RECEIVE_PACKET = 1;

    // SSH v2 RSA
    private BigInteger rsa_e, rsa_n;

    // handlePacket
    // messages
    // The supported packet types and the corresponding message numbers are
    // given in the following table. Messages with _MSG_ in their name may
    // be sent by either side. Messages with _CMSG_ are only sent by the
    // client, and messages with _SMSG_ only by the server.
    //
    private final byte SSH_MSG_NONE = 0;

    private final byte SSH_MSG_DISCONNECT = 1;

    private final byte SSH_SMSG_PUBLIC_KEY = 2;

    private final byte SSH_CMSG_SESSION_KEY = 3;

    private final byte SSH_CMSG_USER = 4;

    private final byte SSH_CMSG_AUTH_PASSWORD = 9;

    private final byte SSH_CMSG_REQUEST_PTY = 10;

    private final byte SSH_CMSG_EXEC_SHELL = 12;

    private final byte SSH_SMSG_SUCCESS = 14;

    private final byte SSH_SMSG_FAILURE = 15;

    private final byte SSH_CMSG_STDIN_DATA = 16;

    private final byte SSH_SMSG_STDOUT_DATA = 17;

    private final byte SSH_SMSG_STDERR_DATA = 18;

    private final byte SSH_SMSG_EXITSTATUS = 20;

    private final byte SSH_MSG_IGNORE = 32;

    private final byte SSH_CMSG_EXIT_CONFIRMATION = 33;

    private final byte SSH_MSG_DEBUG = 36;

    /* SSH v2 stuff */

    private static final byte SSH2_MSG_DISCONNECT = 1;

    private static final byte SSH2_MSG_IGNORE = 2;

    private static final byte SSH2_MSG_UNIMPLEMENTED = 3;

    private static final byte SSH2_MSG_SERVICE_REQUEST = 5;

    private static final byte SSH2_MSG_SERVICE_ACCEPT = 6;

    private static final byte SSH2_MSG_KEXINIT = 20;

    private static final byte SSH2_MSG_NEWKEYS = 21;

    private static final byte SSH2_MSG_KEXDH_INIT = 30;

    private static final byte SSH2_MSG_KEXDH_REPLY = 31;

    private static final byte SSH2_MSG_USERAUTH_REQUEST = 50;

    private static final byte SSH2_MSG_USERAUTH_FAILURE = 51;

    private static final byte SSH2_MSG_USERAUTH_SUCCESS = 52;

    private static final byte SSH2_MSG_USERAUTH_BANNER = 53;

    private static final byte SSH2_MSG_CHANNEL_OPEN = 90;

    private static final byte SSH2_MSG_CHANNEL_OPEN_CONFIRMATION = 91;

    private static final byte SSH2_MSG_CHANNEL_OPEN_FAILURE = 92;

    private static final byte SSH2_MSG_CHANNEL_WINDOW_ADJUST = 93;

    private static final byte SSH2_MSG_CHANNEL_DATA = 94;

    private static final byte SSH2_MSG_CHANNEL_EXTENDED_DATA = 95;

    private static final byte SSH2_MSG_CHANNEL_EOF = 96;

    private static final byte SSH2_MSG_CHANNEL_CLOSE = 97;

    private static final byte SSH2_MSG_CHANNEL_REQUEST = 98;

    private static final byte SSH2_MSG_CHANNEL_SUCCESS = 99;

    private static final byte SSH2_MSG_CHANNEL_FAILURE = 100;

    private String kexalgs, hostkeyalgs, encalgs2c, encalgc2s, macalgs2c,
            macalgc2s, compalgc2s, compalgs2c, langc2s, langs2;

    private int outgoingseq = 0, incomingseq = 0;

    //
    // encryption types
    //
    private int SSH_CIPHER_NONE = 0; // No encryption

    private int SSH_CIPHER_IDEA = 1; // IDEA in CFB mode (patented)

    private int SSH_CIPHER_DES = 2; // DES in CBC mode

    private int SSH_CIPHER_3DES = 3; // Triple-DES in CBC mode

    private int SSH_CIPHER_TSS = 4; // An experimental stream cipher

    private int SSH_CIPHER_RC4 = 5; // RC4 (patented)

    private int SSH_CIPHER_BLOWFISH = 6; // Bruce Scheiers blowfish (public
                                            // d)

    //
    // authentication methods
    //
    private final int SSH_AUTH_RHOSTS = 1; // .rhosts or /etc/hosts.equiv

    private final int SSH_AUTH_RSA = 2; // pure RSA authentication

    private final int SSH_AUTH_PASSWORD = 3; // password authentication,

    // implemented !

    private final int SSH_AUTH_RHOSTS_RSA = 4; // .rhosts with RSA host

    // authentication

    private boolean cansenddata = false;

    /**
     * Initialise SshIO
     */
    public SshIO(SshSession sshSession) {
        this.sshSession = sshSession;
        crypto = null;
    }

    SshPacket currentpacket;

    /** write data to our back end */
    public void write(byte[] b) throws IOException {
        sshSession.sendData(b);
    }

    byte[] one = new byte[1];

    private void write(byte b) throws IOException {
        one[0] = b;
        write(one);
    }

    public void disconnect() {
        login = "";
        password = "";
        phase = 0;
        crypto = null;
    }

    protected void sendDisconnect() throws IOException {
        sendDisconnect(11, "Finished");
    }

    protected void sendDisconnect(int reason, String reasonStr)
            throws IOException {
//#ifdef ssh2
        if (useprotocol == 2) {
            SshPacket2 pn = new SshPacket2(SSH_MSG_DISCONNECT);
            pn.putInt32(reason);
            pn.putString(reasonStr);
            pn.putString("en");
            sendPacket2(pn);
        } else
//#endif
        {
            SshPacket1 pn = new SshPacket1(SSH_MSG_DISCONNECT);
            pn.putInt32(reason);
            pn.putString(reasonStr);
            pn.putString("en");
            sendPacket1(pn);
        }

        disconnect();
    }

    public void sendData(byte[] data, int offset, int length)
            throws IOException {
        String str = new String(data, offset, length);
        // if (debug > 1) System.out.println("SshIO.send(" + str + ")");
        if (dataToSend == null)
            dataToSend = str;
        else
            dataToSend += str;
        if (cansenddata) {
//#ifdef ssh2
            if (useprotocol == 2) {
                SshPacket2 pn = new SshPacket2(SSH2_MSG_CHANNEL_DATA);
                pn.putInt32(0);
                pn.putString(dataToSend);
                sendPacket2(pn);
            } else
//#endif
            {
                Send_SSH_CMSG_STDIN_DATA(dataToSend);
            }
            dataToSend = null;
        }
    }

    /**
     * Read data from the remote host. Blocks until data is available.
     * 
     * Returns an array of bytes that will be displayed.
     * 
     */
    public byte[] handleSSH(byte buff[], int boffset, int length)
            throws IOException {
        String result;
        int boffsetend = boffset + length;

        // if (debug > 1)
        // Telnet.console.append("SshIO.getPacket(" + buff + "," + length +
        // ")");

        if (phase == PHASE_INIT) {
            byte b; // of course, byte is a signed entity (-128 -> 127)

            while (boffset < boffsetend) {
                b = buff[boffset++];
                // both sides MUST send an identification string of the form
                // "SSH-protoversion-softwareversion comments",
                // followed by newline character(ascii 10 = '\n' or '\r')
                idstr += (char) b;
                if (b == '\n') {
                    phase++;
                    // if (!idstr.substring(0, 4).equals("SSH-")) {
                    // System.out.println("Received invalid ID string: " +
                    // idstr
                    // + ", (substr " + idstr.substring(0, 4) + ")");
                    // throw (new IOException());
                    // }
                    remotemajor = Integer.parseInt(idstr.substring(4, 5));
                    String minorverstr = idstr.substring(6, 8);
                    if (!Character.isDigit(minorverstr.charAt(1)))
                        minorverstr = minorverstr.substring(0, 1);
                    remoteminor = Integer.parseInt(minorverstr);

                    // System.out.println("remotemajor " + remotemajor);
                    // System.out.println("remoteminor " + remoteminor);

//#ifdef ssh2
                    if (remotemajor == 2) {
                        mymajor = 2;
                        myminor = 0;
                        useprotocol = 2;
                    } else {
                        /* Check if we have discretion over whether to use ssh1 or ssh2 */
                        if (remoteminor == 99 && Settings.sshVersionPreferred == 2) {
                            mymajor = 2;
                            myminor = 0;
                            useprotocol = 2;
                        } else {
                            mymajor = 1;
                            myminor = 5;
                            useprotocol = 1;
                        }
                    }
//#else
                    if (remotemajor == 2) {
                        // TODO disconnect
                        return "Server requires SSH2.\r\n"
                                .getBytes();
                    } else {
                        mymajor = 1;
                        myminor = 5;
                        useprotocol = 1;
                    }
//#endif
                    // this is how we tell the remote server what protocol
                    // we
                    // use.
                    idstr_sent = "SSH-" + mymajor + "." + myminor + "-"
                            + idstr_sent;
                    write(idstr_sent.getBytes());

//#ifdef ssh2
                    if (useprotocol == 2)
                        currentpacket = new SshPacket2(null);
                    else
                        currentpacket = new SshPacket1(null);
//#else
                    currentpacket = new SshPacket1(null);
//#endif
                }
            }
            if (boffset == boffsetend)
                return "".getBytes();
            return "SSH PHASE_INIT error\n"
                    .getBytes();
        }

        result = "";
        while (boffset < boffsetend) {
            boffset = currentpacket.addPayload(buff, boffset,
                    (boffsetend - boffset));
            if (currentpacket.isFinished()) {
//#ifdef ssh2
                if (useprotocol == 1) {
                    result = result + handlePacket1((SshPacket1) currentpacket);
                    currentpacket = new SshPacket1(crypto);
                } else {
                    result = result + handlePacket2((SshPacket2) currentpacket);
                    currentpacket = new SshPacket2((SshCrypto2) crypto);
                }
//#else
                result = result + handlePacket1((SshPacket1) currentpacket);
                currentpacket = new SshPacket1(crypto);
//#endif
            }
        }
        return result.getBytes();
    }

    private String handlePacket1(SshPacket1 p) throws IOException { // the
        // message
        // to handle
        // is data
        // and its
        // length is

        byte b; // of course, byte is a signed entity (-128 -> 127)

        // we have to deal with data....

        // if (debug > 0)
        // System.out.println("1 packet to handle, type " + p.getType());

        switch (p.getType()) {
        case SSH_MSG_IGNORE:
            return "";

        case SSH_MSG_DISCONNECT:
            String str = p.getString();
            disconnect();
            return str;

        case SSH_SMSG_PUBLIC_KEY:
            byte[] anti_spoofing_cookie; // 8 bytes
            byte[] server_key_bits; // 32-bit int
            byte[] server_key_public_exponent; // mp-int
            byte[] server_key_public_modulus; // mp-int
            byte[] host_key_bits; // 32-bit int
            byte[] host_key_public_exponent; // mp-int
            byte[] host_key_public_modulus; // mp-int
            byte[] protocol_flags; // 32-bit int
            byte[] supported_ciphers_mask; // 32-bit int
            byte[] supported_authentications_mask; // 32-bit int

            anti_spoofing_cookie = p.getBytes(8);
            server_key_bits = p.getBytes(4);
            server_key_public_exponent = p.getMpInt();
            server_key_public_modulus = p.getMpInt();
            host_key_bits = p.getBytes(4);
            host_key_public_exponent = p.getMpInt();
            host_key_public_modulus = p.getMpInt();
            protocol_flags = p.getBytes(4);
            supported_ciphers_mask = p.getBytes(4);
            supported_authentications_mask = p.getBytes(4);

            // We have completely received the PUBLIC_KEY
            // We prepare the answer ...

            String ret = Send_SSH_CMSG_SESSION_KEY(anti_spoofing_cookie,
                    server_key_public_modulus, host_key_public_modulus,
                    supported_ciphers_mask, server_key_public_exponent,
                    host_key_public_exponent);
            if (ret != null)
                return ret;

            // we check if MD5(server_key_public_exponent) is equals to the
            // applet parameter if any .
            if (hashHostKey != null && hashHostKey.compareTo("") != 0) {
                // we compute hashHostKeyBis the hash value in hexa of
                // host_key_public_modulus
                byte[] Md5_hostKey = md5.digest(host_key_public_modulus);
                String hashHostKeyBis = "";
                for (int i = 0; i < Md5_hostKey.length; i++) {
                    String hex = "";
                    int[] v = new int[2];
                    v[0] = (Md5_hostKey[i] & 240) >> 4;
                    v[1] = (Md5_hostKey[i] & 15);
                    for (int j = 0; j < 1; j++)
                        switch (v[j]) {
                        case 10:
                            hex += "a";
                            break;
                        case 11:
                            hex += "b";
                            break;
                        case 12:
                            hex += "c";
                            break;
                        case 13:
                            hex += "d";
                            break;
                        case 14:
                            hex += "e";
                            break;
                        case 15:
                            hex += "f";
                            break;
                        default:
                            hex += String.valueOf(v[j]);
                            break;
                        }
                    hashHostKeyBis = hashHostKeyBis + hex;
                }
                // we compare the 2 values
                if (hashHostKeyBis.compareTo(hashHostKey) != 0) {
                    login = password = "";
                    return "\nHash value of the host key not correct \r\n";
                    // + "login & password have been reset \r\n"
                    // + "- erase the 'hashHostKey' parameter in the
                    // Html\r\n"
                    // + "(it is used for auhentificating the server and "
                    // + "prevent you from connecting \r\n"
                    // + "to any other)\r\n";
                }
            }
            break;

        case SSH_SMSG_SUCCESS:
            // if (debug > 0)
            // System.out.println("SSH_SMSG_SUCCESS (last packet was " +
            // lastPacketSentType + ")");
            if (lastPacketSentType == SSH_CMSG_SESSION_KEY) {
                // we have succefully sent the session key !! (at last :-) )
                Send_SSH_CMSG_USER();
                break;
            }

            if (lastPacketSentType == SSH_CMSG_USER) {
                // authentication is NOT needed for this user
                Send_SSH_CMSG_REQUEST_PTY(); // request a pseudo-terminal
                return "Empty password login.\r\n";
            }

            if (lastPacketSentType == SSH_CMSG_AUTH_PASSWORD) {// password
                // correct !!!
                // yahoo
                // if (debug > 0)
                // System.out.println("login succesful");

                // now we have to start the interactive session ...
                Send_SSH_CMSG_REQUEST_PTY(); // request a pseudo-terminal
                return "Login & password accepted\r\n";
            }

            if (lastPacketSentType == SSH_CMSG_REQUEST_PTY) {// pty
                // accepted
                // !!
                /*
                 * we can send data with a pty accepted ... no need for a
                 * shell.
                 */
                cansenddata = true;
                if (dataToSend != null) {
                    Send_SSH_CMSG_STDIN_DATA(dataToSend);
                    dataToSend = null;
                }
                Send_SSH_CMSG_EXEC_SHELL(); // we start a shell
                break;
            }
            if (lastPacketSentType == SSH_CMSG_EXEC_SHELL) {// shell is
                // running
                // ...
                /* empty */
            }

            break;

        case SSH_SMSG_FAILURE:
            if (lastPacketSentType == SSH_CMSG_AUTH_PASSWORD) {// password
                // incorrect ???
                // System.out.println("failed to log in");
                disconnect();
                return "Login & password not accepted\r\n";
            }
            if (lastPacketSentType == SSH_CMSG_USER) {
                // authentication is needed for the given user
                // (in most cases that's true)
                Send_SSH_CMSG_AUTH_PASSWORD();
                break;
            }

            if (lastPacketSentType == SSH_CMSG_REQUEST_PTY) {// pty not
                // accepted
                // !!
                break;
            }
            break;

        case SSH_SMSG_STDOUT_DATA: // receive some data from the server
            return p.getString();

        case SSH_SMSG_STDERR_DATA: // receive some error data from the
            // server
            // if(debug > 1)
            str = "Error : " + p.getString();
            // System.out.println("SshIO.handlePacket : " + "STDERR_DATA " +
            // str);
            return str;

        case SSH_SMSG_EXITSTATUS: // sent by the server to indicate that
            // the client program has terminated.
            // 32-bit int exit status of the command
            int value = p.getInt32();
            Send_SSH_CMSG_EXIT_CONFIRMATION();
            // System.out.println("SshIO : Exit status " + value);
            disconnect();
            break;

        case SSH_MSG_DEBUG:
            str = p.getString();
            // if (debug > 0) {
            // System.out.println("SshIO.handlePacket : " + " DEBUG " +
            // str);

            // bad bad bad bad bad. We should not do actions in DEBUG
            // messages,
            // but apparently some SSH demons does not send SSH_SMSG_FAILURE
            // for
            // just USER CMS.
            /*
             * if(lastPacketSentType==SSH_CMSG_USER) {
             * Send_SSH_CMSG_AUTH_PASSWORD(); break; }
             */
            // return str;
            // }
            return "";

        default:
            // System.err.print("SshIO.handlePacket1: Packet Type unknown: "
            // +
            // p.getType());
            break;

        }
        return "";
    } // handlePacket

    private void sendPacket1(SshPacket1 packet) throws IOException {
        write(packet.getPayLoad(crypto));
        lastPacketSentType = packet.getType();
    }

//#ifdef ssh2
    /**
     * Handle SSH protocol Version 2
     * 
     * @param p
     *            the packet we will process here.
     * @return a array of bytes
     */
    private String handlePacket2(SshPacket2 p) throws IOException {
        switch (p.getType()) {
        case SSH2_MSG_DISCONNECT:
            int discreason = p.getInt32();
            String discreason1 = p.getString();
            /* String discreason2 = p.getString(); */
            // System.out.println("SSH2: SSH2_MSG_DISCONNECT(" + discreason
            // +
            // "," + discreason1 + "," + /*discreason2+*/")");
            return "\r\nDisconnected: " + discreason1 + "\r\n";

        case SSH2_MSG_NEWKEYS: {
            // Send response
            sendPacket2(new SshPacket2(SSH2_MSG_NEWKEYS));

            // byte[] session_key = new byte[24];
            // crypto = new SshCrypto( cipher_type, session_key );
            updateKeys(dhkex);

            SshPacket2 pn = new SshPacket2(SSH2_MSG_SERVICE_REQUEST);
            pn.putString("ssh-userauth");

            sendPacket2(pn);
            break;
        }

        case SSH2_MSG_SERVICE_ACCEPT: {
            // Send login request
            SshPacket2 buf = new SshPacket2(SSH2_MSG_USERAUTH_REQUEST);
            buf.putString(login);
            buf.putString("ssh-connection");
            buf.putString("password");
            buf.putByte((byte) 0);
            buf.putString(password);

            sendPacket2(buf);
            break;
        }

        case SSH2_MSG_USERAUTH_SUCCESS: {
            // Open channel
            SshPacket2 pn = new SshPacket2(SSH2_MSG_CHANNEL_OPEN);
            pn.putString("session");
            pn.putInt32(0);
            pn.putInt32(0x100000);
            pn.putInt32(0x4000);
            sendPacket2(pn);
            break;
        }

        case SSH2_MSG_CHANNEL_OPEN_CONFIRMATION: {
            int localId = p.getInt32();
            int remoteId = p.getInt32();
            int remoteWindowSize = p.getInt32();
            int remotePacketSize = p.getInt32();

            // Open PTY
            SshPacket2 pn = new SshPacket2(SSH2_MSG_CHANNEL_REQUEST);
            pn.putInt32(remoteId);
            pn.putString("pty-req");
            pn.putByte((byte) 0); // want reply
            pn.putString(getTerminalID());
            pn.putInt32(getTerminalWidth());
            pn.putInt32(getTerminalHeight());
            pn.putInt32(0);
            pn.putInt32(0);
            pn.putString("");
            sendPacket2(pn);

            // Open Shell
            pn = new SshPacket2(SSH2_MSG_CHANNEL_REQUEST);
            pn.putInt32(remoteId);
            pn.putString("shell");
            pn.putByte((byte) 0); // want reply
            sendPacket2(pn);

            cansenddata = true;
            break;
        }

        case SSH2_MSG_CHANNEL_DATA: {
            int localId = p.getInt32();
            String data = p.getString();
            return data;
        }

        case SSH2_MSG_USERAUTH_FAILURE:
            String methods = p.getString();
            int partial_success = p.getByte();

            return "Login and password not accepted.\r\nAvailable methods are: "
                    + methods + "\r\n";

        case SSH2_MSG_IGNORE:
        case SSH2_MSG_CHANNEL_WINDOW_ADJUST:
        case SSH2_MSG_USERAUTH_BANNER:
        case SSH2_MSG_CHANNEL_EOF:
        case SSH2_MSG_CHANNEL_REQUEST:
            break;

        case SSH2_MSG_CHANNEL_CLOSE: {
            sendDisconnect();
            break;
        }

        case SSH2_MSG_KEXINIT: {
            byte[] fupp;
            byte kexcookie[] = p.getBytes(16); // unused.

            String kexalgs = p.getString();
            // System.out.println("- " + kexalgs);
            String hostkeyalgs = p.getString();
            // System.out.println("- " + hostkeyalgs);
            String encalgc2s = p.getString();
            // System.out.println("- " + encalgc2s);
            String encalgs2c = p.getString();
            // System.out.println("- " + encalgs2c);
            String macalgc2s = p.getString();
            // System.out.println("- " + macalgc2s);
            String macalgs2c = p.getString();
            // System.out.println("- " + macalgs2c);
            String compalgc2s = p.getString();
            // System.out.println("- " + compalgc2s);
            String compalgs2c = p.getString();
            // System.out.println("- " + compalgs2c);
            String langc2s = p.getString();
            // System.out.println("- " + langc2s);
            String langs2c = p.getString();
            // System.out.println("- " + langs2c);
            fupp = p.getBytes(1);
            // System.out.println("- first_kex_follows: " + fupp[0]);
            /* int32 reserved (0) */

            SshPacket2 pn = new SshPacket2(SSH2_MSG_KEXINIT);
            byte[] kexsend = new byte[16];
            Random random = new Random();
            for (int i = 0; i < kexsend.length; i++) {
                kexsend[i] = (byte) random.nextInt();
            }
            String ciphername;
            pn.putBytes(kexsend);
            pn.putString("diffie-hellman-group1-sha1");
            pn.putString("ssh-dss");

            cipher_type = "DES3";
            ciphername = "3des-cbc";

            pn.putString(ciphername);
            pn.putString(ciphername);
            pn.putString("hmac-sha1");
            pn.putString("hmac-sha1");
            pn.putString("none");
            pn.putString("none");
            pn.putString("");
            pn.putString("");
            pn.putByte((byte) 0);
            pn.putInt32(0);

            byte[] I_C = pn.getData();
            sendPacket2(pn);
            
            if (Settings.ssh2StoreKey) {
                if (Settings.ssh2x == null || Settings.ssh2y == null) {
                    byte[][] keys = DHKeyExchange.generateKeyPairBytes(Settings.ssh2KeySize);
                    Settings.ssh2x = keys[0];
                    Settings.ssh2y = keys[1];
                    Settings.saveSettings();
                }
                
                dhkex = new DHKeyExchange(Settings.ssh2x, Settings.ssh2y);
            }
            else {
                dhkex = new DHKeyExchange(Settings.ssh2KeySize);
            }
            
            dhkex.setV_S(idstr.trim().getBytes());
            dhkex.setV_C(idstr_sent.trim().getBytes());
            dhkex.setI_S(add20(p.getData()));
            dhkex.setI_C(add20(I_C));

            pn = new SshPacket2(SSH2_MSG_KEXDH_INIT);
            pn.putMpInt(dhkex.getE());
            sendPacket2(pn);

            return "Negotiating...";
        }

        case SSH2_MSG_KEXDH_REPLY: {
            byte[] K_S = p.getByteString();
            // System.out.println( "K_S=" + K_S );
            byte[] dhserverpub = p.getMpInt();
            // result += "DH Server Pub: " + dhserverpub + "\n\r";

            byte[] sig_of_h = p.getByteString();

            boolean ok = dhkex.next(K_S, dhserverpub, sig_of_h);
            /* signature is a new blob, length is Int32. */
            /*
             * RSA: String type (ssh-rsa) Int32/byte[] signed signature
             */
            // int siglen = p.getInt32();
            // String sigstr = p.getString();
            // result += "Signature: ktype is " + sigstr + "\r\n";
            // byte sigdata[] = p.getBytes( p.getInt32() );
            if (ok) {
                return "OK\r\n";
            } else {
                sendDisconnect(3, "Key exchange failed");
                return "FAILED\r\n";
            }
        }

        case SSH2_MSG_UNIMPLEMENTED:
            return "SSH2: Unimplemented\r\n";

        default:
            return "SSH2: handlePacket2 Unknown type " + p.getType() + "\r\n";
        }
        return "";
    }

    private void sendPacket2(SshPacket2 packet) throws IOException {
        write(packet.getPayLoad((SshCrypto2) crypto, outgoingseq));
        outgoingseq++;
        lastPacketSentType = packet.getType();
    }

    private DHKeyExchange dhkex;

    private byte[] session_id;

    private void updateKeys(DHKeyExchange kex) {
        byte[] K = kex.getK();
        byte[] H = kex.getH();
        SHA1Digest hash = new SHA1Digest();

        if (session_id == null) {
            session_id = new byte[H.length];
            System.arraycopy(H, 0, session_id, 0, H.length);
        }

        /*
         * Initial IV client to server: HASH (K || H || "A" || session_id)
         * Initial IV server to client: HASH (K || H || "B" || session_id)
         * Encryption key client to server: HASH (K || H || "C" || session_id)
         * Encryption key server to client: HASH (K || H || "D" || session_id)
         * Integrity key client to server: HASH (K || H || "E" || session_id)
         * Integrity key server to client: HASH (K || H || "F" || session_id)
         */

        SshPacket2 buf = new SshPacket2(null);
        buf.putMpInt(K);
        buf.putBytes(H);
        buf.putByte((byte) 0x41);
        buf.putBytes(session_id);
        byte[] b = buf.getData();

        hash.update(b, 0, b.length);
        byte[] IVc2s = new byte[hash.getDigestSize()];
        hash.doFinal(IVc2s, 0);

        int j = b.length - session_id.length - 1;

        b[j]++;
        hash.update(b, 0, b.length);
        byte[] IVs2c = new byte[hash.getDigestSize()];
        hash.doFinal(IVs2c, 0);

        b[j]++;
        hash.update(b, 0, b.length);
        byte[] Ec2s = new byte[hash.getDigestSize()];
        hash.doFinal(Ec2s, 0);

        b[j]++;
        hash.update(b, 0, b.length);
        byte[] Es2c = new byte[hash.getDigestSize()];
        hash.doFinal(Es2c, 0);

        b[j]++;
        hash.update(b, 0, b.length);
        byte[] MACc2s = new byte[hash.getDigestSize()];
        hash.doFinal(MACc2s, 0);

        b[j]++;
        hash.update(b, 0, b.length);
        byte[] MACs2c = new byte[hash.getDigestSize()];
        hash.doFinal(MACs2c, 0);

        int keySize = 24;

        while (keySize > Es2c.length) {
            buf = new SshPacket2(null);
            buf.putMpInt(K);
            buf.putBytes(H);
            buf.putBytes(Es2c);
            b = buf.getData();

            hash.update(b, 0, b.length);
            byte[] foo = new byte[hash.getDigestSize()];
            hash.doFinal(foo, 0);
            byte[] bar = new byte[Es2c.length + foo.length];
            System.arraycopy(Es2c, 0, bar, 0, Es2c.length);
            System.arraycopy(foo, 0, bar, Es2c.length, foo.length);
            Es2c = bar;
        }
        while (keySize > Ec2s.length) {
            buf = new SshPacket2(null);
            buf.putMpInt(K);
            buf.putBytes(H);
            buf.putBytes(Ec2s);
            b = buf.getData();

            hash.update(b, 0, b.length);
            byte[] foo = new byte[hash.getDigestSize()];
            hash.doFinal(foo, 0);
            byte[] bar = new byte[Ec2s.length + foo.length];
            System.arraycopy(Ec2s, 0, bar, 0, Ec2s.length);
            System.arraycopy(foo, 0, bar, Ec2s.length, foo.length);
            Ec2s = bar;
        }

        crypto = new SshCrypto2(IVc2s, IVs2c, Ec2s, Es2c, MACc2s, MACs2c);
    }

    private byte[] add20(byte[] in) {
        byte[] out = new byte[in.length + 1];
        out[0] = 20;
        System.arraycopy(in, 0, out, 1, in.length);
        return out;
    }

//#endif

    //
    // Send_SSH_CMSG_SESSION_KEY
    // Create :
    // the session_id,
    // the session_key,
    // the Xored session_key,
    // the double_encrypted session key
    // send SSH_CMSG_SESSION_KEY
    // Turn the encryption on (initialise the block cipher)
    //

    private String Send_SSH_CMSG_SESSION_KEY(byte[] anti_spoofing_cookie,
            byte[] server_key_public_modulus, byte[] host_key_public_modulus,
            byte[] supported_ciphers_mask, byte[] server_key_public_exponent,
            byte[] host_key_public_exponent) throws IOException {

        String str;
        int boffset;

        byte cipher_types; // encryption types
        byte[] session_key; // mp-int

        // create the session id
        // session_id = md5(hostkey->n || servkey->n || cookie) //protocol V
        // 1.5. (we use this one)
        // session_id = md5(servkey->n || hostkey->n || cookie) //protocol V
        // 1.1.(Why is it different ??)
        //

        byte[] session_id_byte = new byte[host_key_public_modulus.length
                + server_key_public_modulus.length
                + anti_spoofing_cookie.length];

        System.arraycopy(host_key_public_modulus, 0, session_id_byte, 0,
                host_key_public_modulus.length);
        System.arraycopy(server_key_public_modulus, 0, session_id_byte,
                host_key_public_modulus.length,
                server_key_public_modulus.length);
        System.arraycopy(anti_spoofing_cookie, 0, session_id_byte,
                host_key_public_modulus.length
                        + server_key_public_modulus.length,
                anti_spoofing_cookie.length);

        byte[] hash_md5 = md5.digest(session_id_byte);

        // SSH_CMSG_SESSION_KEY : Sent by the client
        // 1 byte cipher_type (must be one of the supported values)
        // 8 bytes anti_spoofing_cookie (must match data sent by the server)
        // mp-int double-encrypted session key (uses the session-id)
        // 32-bit int protocol_flags
        //
        if ((supported_ciphers_mask[3] & (byte) (1 << SSH_CIPHER_BLOWFISH)) != 0
                && hasCipher("Blowfish")) {
            cipher_types = (byte) SSH_CIPHER_BLOWFISH;
            cipher_type = "Blowfish";
        } else {
            if ((supported_ciphers_mask[3] & (1 << SSH_CIPHER_IDEA)) != 0
                    && hasCipher("IDEA")) {
                cipher_types = (byte) SSH_CIPHER_IDEA;
                cipher_type = "IDEA";
            } else {
                if ((supported_ciphers_mask[3] & (1 << SSH_CIPHER_3DES)) != 0
                        && hasCipher("DES3")) {
                    cipher_types = (byte) SSH_CIPHER_3DES;
                    cipher_type = "DES3";
                } else {
                    if ((supported_ciphers_mask[3] & (1 << SSH_CIPHER_DES)) != 0
                            && hasCipher("DES")) {
                        cipher_types = (byte) SSH_CIPHER_DES;
                        cipher_type = "DES";
                    } else {
                        // System.err.println("SshIO: remote server does not
                        // supported IDEA, BlowFish or 3DES, support cypher mask
                        // is " + supported_ciphers_mask[3] + ".\n");
                        disconnect();
                        return "\rIncompatible ciphers, closing connection.\r\n";
                    }
                }
            }
        }
        // if (debug > 0)
        // System.out.println("SshIO: Using " + cipher_type + "
        // blockcipher.\n");

        // anti_spoofing_cookie : the same
        // double_encrypted_session_key :
        // 32 bytes of random bits
        // Xor the 16 first bytes with the session-id
        // encrypt with the server_key_public (small) then the
        // host_key_public(big) using RSA.
        //

        // 32 bytes of random bits
        byte[] random_bits1 = new byte[16], random_bits2 = new byte[16];

        // / java.util.Date date = new java.util.Date(); ////the number of
        // milliseconds since January 1, 1970, 00:00:00 GMT.
        // Math.random() a pseudorandom double between 0.0 and 1.0.
        random_bits2 = random_bits1 =
        // md5.hash("" + Math.random() * (new java.util.Date()).getDate());
        md5.digest(("" + rnd.nextLong() * (new java.util.Date()).getTime())
                .getBytes()); // RADEK
        // -
        // zase
        // RANDOM

        random_bits1 = md5.digest(SshMisc.addArrayOfBytes(md5
                .digest((password + login).getBytes()), random_bits1));
        random_bits2 = md5.digest(SshMisc.addArrayOfBytes(md5
                .digest((password + login).getBytes()), random_bits2));

        // SecureRandom random = new java.security.SecureRandom(random_bits1);
        // //no supported by netscape :-(
        // random.nextBytes(random_bits1);
        // random.nextBytes(random_bits2);

        session_key = SshMisc.addArrayOfBytes(random_bits1, random_bits2);

        // Xor the 16 first bytes with the session-id
        byte[] session_keyXored = SshMisc.XORArrayOfBytes(random_bits1,
                hash_md5);
        session_keyXored = SshMisc.addArrayOfBytes(session_keyXored,
                random_bits2);

        // We encrypt now!!
        byte[] encrypted_session_key;
        /* Karl: according to SSH 1.5 protocol spec we encrypt first with the key with the shortest
         * modulus. Usually this was the server key but some servers have bigger keys than the host key!
         * So check here and swap around if necessary.
         */
        if ( server_key_public_modulus.length <= host_key_public_modulus.length ) {
            encrypted_session_key = SshCrypto.encrypteRSAPkcs1Twice(
                    session_keyXored, server_key_public_exponent,
                    server_key_public_modulus, host_key_public_exponent,
                    host_key_public_modulus);
        }
        else {
            encrypted_session_key = SshCrypto.encrypteRSAPkcs1Twice(
                    session_keyXored, host_key_public_exponent,
                    host_key_public_modulus, server_key_public_exponent,
                    server_key_public_modulus);
        }
        
        // protocol_flags :protocol extension cf. page 18
        int protocol_flags = 0; /* currently 0 */

        SshPacket1 packet = new SshPacket1(SSH_CMSG_SESSION_KEY);
        packet.putByte((byte) cipher_types);
        packet.putBytes(anti_spoofing_cookie);
        packet.putBytes(encrypted_session_key);
        packet.putInt32(protocol_flags);
        sendPacket1(packet);
        crypto = new SshCrypto(cipher_type, session_key);
        return "";
    }

    private boolean hasCipher(String cipherName) {
        return (Cipher.getInstance(cipherName) != null);
    }

    /**
     * SSH_CMSG_USER string user login name on server
     */
    private String Send_SSH_CMSG_USER() throws IOException {
        // if (debug > 0) System.err.println("Send_SSH_CMSG_USER(" + login +
        // ")");

        SshPacket1 p = new SshPacket1(SSH_CMSG_USER);
        p.putString(login);
        sendPacket1(p);

        return "";
    }

    /**
     * Send_SSH_CMSG_AUTH_PASSWORD string user password
     */
    private String Send_SSH_CMSG_AUTH_PASSWORD() throws IOException {
        SshPacket1 p = new SshPacket1(SSH_CMSG_AUTH_PASSWORD);
        p.putString(password);
        sendPacket1(p);
        return "";
    }

    /**
     * Send_SSH_CMSG_EXEC_SHELL (no arguments) Starts a shell (command
     * interpreter), and enters interactive session mode.
     */
    private String Send_SSH_CMSG_EXEC_SHELL() throws IOException {
        SshPacket1 packet = new SshPacket1(SSH_CMSG_EXEC_SHELL);
        sendPacket1(packet);
        return "";
    }

    /**
     * Send_SSH_CMSG_STDIN_DATA
     * 
     */
    private String Send_SSH_CMSG_STDIN_DATA(String str) throws IOException {
        SshPacket1 packet = new SshPacket1(SSH_CMSG_STDIN_DATA);
        packet.putString(str);
        sendPacket1(packet);
        return "";
    }

    /**
     * Send_SSH_CMSG_REQUEST_PTY string TERM environment variable value (e.g.
     * vt100) 32-bit int terminal height, rows (e.g., 24) 32-bit int terminal
     * width, columns (e.g., 80) 32-bit int terminal width, pixels (0 if no
     * graphics) (e.g., 480)
     */
    private String Send_SSH_CMSG_REQUEST_PTY() throws IOException {
        SshPacket1 p = new SshPacket1(SSH_CMSG_REQUEST_PTY);

        p.putString(getTerminalID());
        p.putInt32(getTerminalHeight()); // Int32 rows
        p.putInt32(getTerminalWidth()); // Int32 columns
        p.putInt32(0); // Int32 x pixels
        p.putInt32(0); // Int32 y pixels
        p.putByte((byte) 0); // Int8 terminal modes
        sendPacket1(p);
        return "";
    }

    private String Send_SSH_CMSG_EXIT_CONFIRMATION() throws IOException {
        SshPacket1 packet = new SshPacket1(SSH_CMSG_EXIT_CONFIRMATION);
        sendPacket1(packet);
        return "";
    }

    /**
     * Send_SSH_NOOP (no arguments) Sends a NOOP packet to keep the connection
     * alive.
     */
    public String Send_SSH_NOOP() throws IOException {
        // KARL The specification states that this packet is never sent, however
        // the OpenSSL source
        // for keep alives indicates that SSH_MSG_IGNORE (the alternative)
        // crashes some servers and
        // advocates SSH_MSG_NONE instead.
        if (useprotocol == 1) {
            SshPacket1 packet = new SshPacket1(SSH_MSG_NONE);
            sendPacket1(packet);
        }
//#ifdef ssh2
        else {
            SshPacket2 packet = new SshPacket2(SSH2_MSG_IGNORE);
            packet.putString("");
            sendPacket2(packet);
        }
//#endif
        return "";
    }

    protected String getTerminalID() {
        return sshSession.getTerminalID();
    }

    protected int getTerminalHeight() {
        return sshSession.getTerminalHeight();
    }

    protected int getTerminalWidth() {
        return sshSession.getTerminalWidth();
    }
}