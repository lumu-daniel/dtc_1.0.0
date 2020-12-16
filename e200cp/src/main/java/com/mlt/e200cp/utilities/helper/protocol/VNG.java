package com.mlt.e200cp.utilities.helper.protocol;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.mlt.e200cp.utilities.helper.protocol.VngDef.RS;


public class VNG {

    private final int BUFSIZE = 3072;

    // End of Text symbol to mark the end of the Transaction Message/Response.
    public static final byte ETX = (byte)0x03;
    // Field Separator is used to separate the various data segments within the Message/ Response.
    public static final byte FS = (byte)0x1C;

    public byte[] buffer = new byte[BUFSIZE];
    public int size = 0;

    public VNG() {}


    public void clear() {
        size = 0;
        parseIndex = 0;
        isSuccess = false;
        status = 0;
    }

    //region builder

    public VNG(String ascCmd) {
        addData(ascCmd);
    }

    public VNG(byte [] data) {
        addData(data);
    }

    public byte[] getCmdBuffer()
    {
        return Arrays.copyOfRange(buffer, 0, size);
    }
    public boolean addData(byte symbol) {
        if (size + 1 > BUFSIZE)
            return false;

        buffer[size++] = symbol;
        return true;
    }
    public boolean addRSLength(int length) {
        if (size + 3 + length > BUFSIZE)
            return false;

        buffer[size++ ] = RS;
        buffer[size++ ] = (byte)((length >> 8) & 0xFF);
        buffer[size++ ] = (byte)(length & 0xFF);

        return true;
    }
    public boolean addRSLenData(byte [] data) {
        return addRSLenData(data, data.length);
    }
    public boolean addRSLenData(byte [] data, int length) {
        if (size + 3 + length > BUFSIZE)
            return false;

        buffer[size++ ] = RS;
        buffer[size++ ] = (byte)((length >> 8) & 0xFF);
        buffer[size++ ] = (byte)(length & 0xFF);
        return addData(data, length);
    }
    public boolean addData(String ascData) {
        byte[] data = ascData.getBytes(Charset.forName("US-ASCII"));

        if (this.size + data.length > BUFSIZE)
            return false;

        this.addData(data, data.length);

        return true;
    }
    public boolean addData(byte[] data, int length) {
        if (this.size + length > BUFSIZE)
            return false;

        if (length > 0 || data != null)
        {
            System.arraycopy(data, 0, buffer, size, length);
            size += length;
        }

        return true;
    }
    public boolean addData(byte[] data) {
        if (data == null)
            return false;
        return addData(data, data.length);
    }

    //endregion

    //region Parser

    public int parseIndex = 0;
    public boolean isSuccess = false;
    public char status = 0x00;

    public VNG(byte [] rspData, String cmd) {
        buffer = rspData;
        size = buffer.length;
        parseHeader(cmd);
    }

    public VNG(byte [] rspData, String cmd, boolean withStatus, boolean checkZeroStatus) {
        setData(rspData);
        parseHeader(cmd, withStatus, checkZeroStatus);
    }

    public void setData(byte [] rspData) {
        clear();

        if (rspData != null) {
            System.arraycopy(rspData, 0, buffer,0, rspData.length);
            size = rspData.length;
        }
    }

    public boolean parseHeader(String cmd) {
        return parseHeader(cmd, false, false);
    }

    public boolean parseHeader(String cmd, boolean withStatus, boolean checkZeroStatus) {
        if (buffer != null && size != 0) {
            parseIndex = 0;

            String prefix = parseString(cmd.length());
            isSuccess = prefix.equals(cmd);

            if (isSuccess && withStatus) {
                status = (char)(parseByte() & 0xFF);

                if (checkZeroStatus && status != '0')
                    isSuccess = false;
            }
        }
        return isSuccess;
    }

    public String parseStringToSymbol(byte symbol) {
        String tmp = "";
        int i = 0;
        for(i = parseIndex ; i < size ; i++) {
            if (buffer[i] == symbol)
                break;
            tmp += (char)buffer[i];
        }
        parseIndex = i+1;
        return tmp;
    }

    public String parseString(int length) {

        // up to size
        length = (parseIndex + length <= size) ? length : (size - parseIndex);

        String tmp = "";
        for(int i = 0 ; i < length ; i++)
            tmp += (char)buffer[parseIndex++];
        return tmp;
    }


    public String[] parseOptions()
    {
        byte nextByte = parseByte();
        List<String> options = new LinkedList<String>();

        while (parseIndex < size && nextByte != (byte)RS)
        {
            if (nextByte == (byte)FS)
            {
                String option = parseString();
                nextByte = buffer[parseIndex];
                options.add(option);
            }
        }

        if (nextByte == (byte)RS) parseIndex--;

        String[] stockArr = new String[options.size()];
        stockArr = options.toArray(stockArr);
        return stockArr;

    }

    public String parseString() {
        String tmp = "";
        for(int i = parseIndex ; i < size ; i++)
        {
            if ( buffer[parseIndex] >= 32 && buffer[parseIndex] <= 126)
                tmp += (char)buffer[parseIndex++];
            else
                return tmp;
        }
        return tmp;
    }

    public byte parseByte() {
        byte b = buffer[parseIndex++];
        return b;
    }

    public byte [] parseBytes(int length) {
        byte [] b = Arrays.copyOfRange(buffer, parseIndex, parseIndex + length);
        parseIndex += length;
        return b;
    }

    public int parseValue(int length) {
        int v = 0;
        for(int i = 0 ; i < length ; i++)
            v = v * 256 + (buffer[parseIndex++] & 0xFF);
        return v;
    }

    public byte [] parseRSLenData() {
        if (parseByte() != RS)
            return null;

        int length = parseValue(2);

        if (parseIndex + length > size)
            return null;

        return parseBytes(length);
    }

    public int parseRSLen() {
        if (parseByte() != RS)
            return -1;

        int length = parseValue(2);

        if (parseIndex + length > size)
            return -1;

        return length;
    }

    public boolean tryToParse(String value) {
        String tmp = "";
        for(int i = 0 ; i < value.length() ; i++)
            tmp += (char)buffer[parseIndex++];
        if (tmp.equals(value))
            return true;
        else {
            parseIndex -= value.length();
            return false;
        }
    }

    //endregion
}
