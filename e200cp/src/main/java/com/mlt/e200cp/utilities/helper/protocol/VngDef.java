package com.mlt.e200cp.utilities.helper.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by simon_chen on 2018/5/23.
 */

public class VngDef {

    /* Control Symbol */
    // Start of Text Symbol to mark the start of the Transaction Message/Response.
    public static final byte STX = (byte)0x02;

    // End of Text symbol to mark the end of the Transaction Message/Response.
    public static final byte ETX = (byte)0x03;

    // Shift In Symbol to mark the start of the Transaction Message/Response.
    public static final byte SI = (byte)0x0F;

    // Shift Out symbol to mark the end of the Transaction Message/Response.
    public static final byte SO = (byte)0x0E;

    // Acknowledge symbol informs the Message/Response was received correctly
    public static final byte ACK = (byte)0x06;

    // Not Acknowledge symbol if the previous Transaction Message/Response was not received correctly
    public static final byte NAK = (byte)0x15;

    // Field Separator is used to separate the various data segments within the Message/ Response.
    public static final byte FS = (byte)0x1C;

    // Clear Screen Symbol is used to clear LCD
    public static final byte SUB = (byte)0x1A;

    // Record Separator is used to separate the binary data segments within the Message/Response.
    public static final byte RS = (byte)0x1E;

    // End-of-Transmission symbol informs the transaction is complete and terminate the communication
    public static final byte EOT = (byte)0x04;
    /* Control Symbol - End*/

    /*
        {State}: device operation state
        ‘0’ = Initial (Manufacturing)
        ‘1’ = Run
        ‘2’ = Tamper
        ‘4’ = Stop
        ‘5’ = UA (Un-authenticated)
        ‘S’ = Shipping
        ‘W’ = Maintenance (WFA)
        ‘G’= Loader (Device Firmware Update)
    */
    public static Map<Character, String> R1 = new HashMap<Character, String>();
    static
    {
        R1.put( '0' , "init" );
        R1.put( '1' , "run" );
        R1.put( '2' , "tamper" );
        R1.put( '4' , "stop" );
        R1.put( '5' , "UA" );
        R1.put( 'S' , "shipping" );
        R1.put( 'W' , "maintenance" );
        R1.put( 'G' , "loader" );
    };
    public static String R1(byte b) {
        return R1((char)(b & 0xFF));
    }
    public static String R1(char c) {
        if (R1.containsKey(c)) return R1.get(c);
        else return "unknown";
    }

    /*
        ‘0’: Init (Manufacturing) State
        ‘1’: Shipping State
        ‘2’: UA State
        ‘3’: Run State
        ‘4’: Stop State
        ‘5’: Maintenance (WFA, Wait-for-activation) State
    */
    public static Map<Character, String> ST = new HashMap<Character, String>();
    static
    {
        ST.put( '0' , "init" );
        ST.put( '1' , "shipping" );
        ST.put( '2' , "UA" );
        ST.put( '3' , "run" );
        ST.put( '4' , "stop" );
        ST.put( '5' , "maintenance" );
    };
    public static String ST(byte b) {
        return ST((char)(b & 0xFF));
    }
    public static String ST(char c) {
        if (ST.containsKey(c)) return ST.get(c);
        else return "unknown";
    }
}
