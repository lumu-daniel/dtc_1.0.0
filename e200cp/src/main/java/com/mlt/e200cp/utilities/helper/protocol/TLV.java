package com.mlt.e200cp.utilities.helper.protocol;

import com.mlt.e200cp.utilities.helper.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by simon_chen on 2013/6/27.
 */
public class TLV {
    public byte[] data = new byte[1024];
    public int index = 0;

    public TLV() {}
    public TLV(byte [] value) {
        addData(value);
    }
    public TLV(int tag, byte [] value) {
        addData(tag, value);
    }
    public void addData(byte[] value)
    {
        addData(value, value.length);
    }
    public void addData(byte[] value, int offset, int length)
    {
        System.arraycopy(value, offset, data, index, length);
        index += length;
    }
    public void addData(byte[] value, int length)
    {
        System.arraycopy(value, 0, data, index, length);
        index += length;
    }
    public void addData(int tag, byte[] value, int length)
    {
        if (tag > 0xFF && tag < 0xFFFF)
            data[index++] = (byte)((tag & 0xFF00) >> 8);
        data[index++] = (byte)(tag & 0xFF);

        if ((byte)length > 0xFF)
        {
            data[index++] = (byte)0x82;
            data[index++] = (byte)(value.length / 0xFF);
        }
        else if (length > 0x7F)
        {
            data[index++] = (byte)0x81;
        }
        data[index++] = (byte)(value.length % 0xFF);

        System.arraycopy(value, 0, data, index, length);
        index += length;
    }
    public void addData(int tag, byte[] value)
    {
        addData(tag, value, value.length);
    }
    public void addData(TLV tlv)
    {
        byte[] new_data = tlv.toByteArray();
        System.arraycopy(new_data, 0, data, index, new_data.length);
        index += new_data.length;
    }
    public void addData(String tlv)
    {
        byte[] new_data = Utility.hex2Byte(tlv);
        System.arraycopy(new_data, 0, data, index, new_data.length);
        index += new_data.length;
    }
    public void addData(int tag, String value)
    {
        byte[] new_data = Utility.hex2Byte(value);

        addData(tag, new_data);
    }

    public void clear()
    {
        index = 0;
    }

    public boolean isEmpty() { return index == 0; }

    public byte[] toByteArray()
    {
        return Arrays.copyOfRange(data, 0, index);
    }

    private List<Integer> parseTags(String reqTags)
    {
        byte[] tags = Utility.hex2Byte(reqTags);

        List<Integer> tagList = new LinkedList<Integer>();

        for (int i = 0; i < tags.length; i++)
        {
            if ((tags[i] & 0x1F) != 0x1F)    // 1 byte tag
            {
                tagList.add((int)tags[i]);
            }
            else                             // 2 byte tag
            {
                int tmpTag = tags[i] << 8;
                tmpTag += tags[i+1];
                i++;
                tagList.add(tmpTag);
            }
        }
        return tagList;
    }

    public static class ENTRY {
        public int tag = 0;
        public int length = 0;
        public byte [] value = null;
        public int itemLen = 0;
    }

    public ENTRY parseTLV(int parseIndex)
    {
        ENTRY item = new ENTRY();

        int i = parseIndex;

        if (i + 2 > this.index)
            return null;


        if ((data[i] & 0x1F) != 0x1F)    // 1 byte tag
        {
            item.tag = data[i++] & 0xFF;
            item.itemLen += 1;
        }
        else                             // 2 byte tag
        {
            if (i + 3 > this.index)
                return null;

            item.tag = (data[i++] << 8) & 0x0000FFFF;
            item.tag += data[i++] & 0xFF;
            item.itemLen += 2;
        }

        if (data[i] < 0x80)             // 1 byte lenght
        {
            if (i + 1 > this.index)
                return null;

            item.length = data[i++] & 0xFF;
            item.itemLen += 1;
        }
        else if (data[i] == 0x81)
        {
            if (i + 2 >= this.index)
                return null;

            i++;
            item.length = data[i++] & 0xFF;
            item.itemLen += 2;
        }
        else if (data[i] == 0x82)
        {
            if (i + 3 > this.index)
                return null;

            i++;
            item.length = (data[i++] << 8) & 0x0000FFFF;
            item.length += data[i++] & 0xFF;
            item.itemLen += 3;
        }

        if (i + item.length > index)
            return null;

        item.value = Arrays.copyOfRange(data, parseIndex + item.itemLen, parseIndex + item.itemLen + item.length);
        item.itemLen += item.length;
        return item;
    }

    public ENTRY [] parseTLV() {
        int parseIndex = 0;

        List<ENTRY> tlvList = new ArrayList<ENTRY>();

        while(true) {
            ENTRY e = parseTLV(parseIndex);
            if (e != null) {
                tlvList.add(e);
                parseIndex +=  e.itemLen;
            }
            else
                break;
        }
        return (ENTRY [])tlvList.toArray();
    }

    public byte[] retrieveTag(int reqTag, int offset, boolean onlyValue)
    {
        int tmpTag = 0;
        int tmpLength = 0;
        int i = offset;

        while (i < index)
        {
            int startIndex = i;

            if ((data[i] & (byte)0x1F) != (byte)0x1F)    // 1 byte tag
            {
                tmpTag = data[i++] & 0xFF;
            }
            else                             // 2 byte tag
            {
                tmpTag = (data[i++] << 8) & 0x0000FFFF;
                tmpTag += data[i++] & 0xFF;
            }

            if ((data[i] & 0xFF) < 0x80)             // 1 byte lenght
            {
                tmpLength = data[i++] & 0xFF;
            }
            else if ((data[i] & 0xFF) == 0x81)
            {
                i++;
                tmpLength = data[i++] & 0xFF;
            }
            else if ((data[i] & 0xFF) == 0x82)
            {
                i++;
                tmpLength = (data[i++] << 8) & 0x0000FFFF;
                tmpLength += data[i++] & 0xFF;
            }

            if (i + tmpLength > index)
                return null;

            if (reqTag == tmpTag)
            {
                if (onlyValue)
                {
                    byte[] ret = new byte[tmpLength];
                    System.arraycopy(data, i, ret, 0, tmpLength);
                    return ret;
                }
                else
                {
                    byte[] ret = new byte[i + tmpLength];
                    System.arraycopy(data, startIndex, ret, 0, i + tmpLength);
                    return ret;
                }
            }
            else
            {

            }

            i += tmpLength;
        }

        return null;
    }

    public byte[] retrieveTag(byte [] reqTag, boolean onlyValue)
    {
        int tag = 0;
        for(int i = 0 ; i < reqTag.length ; i++) {
            tag = tag * 256 + (reqTag[i] & 0xFF);
        }
        return retrieveTag(tag, 0, onlyValue);
    }

    public byte[] retrieveTag(int reqTag, boolean onlyValue)
    {
        return retrieveTag(reqTag, 0, onlyValue);
    }

    public byte[] retrieveTags(String reqTags)
    {
        List<Integer> tagList = parseTags(reqTags);

        byte[] buf = new byte[1024];
        int bufIndex = 0;

        for(Integer i = 0; i < tagList.size() ; i++)
        {
            byte[] tmp = retrieveTag(i, false);
            if (tmp != null)
            {
                System.arraycopy(tmp, 0, buf, bufIndex, tmp.length);
                bufIndex += tmp.length;
            }
        }
        if (bufIndex != 0)
            return Arrays.copyOfRange(buf, 0, bufIndex);

        return null;
    }

    public void putInTemplate(int tag) {
        byte [] tmp = this.toByteArray();
        this.clear();
        this.addData(tag, tmp);
    }

    public HashMap<Integer, byte []> retrieveTagsToHashMap(String reqTags)
    {
        HashMap<Integer, byte []> data = new HashMap<>();

        List<Integer> tagList = parseTags(reqTags);

        byte[] buf = new byte[1024];
        int bufIndex = 0;

        for(Integer i = 0; i < tagList.size() ; i++)
        {
            byte[] tmp = retrieveTag(i, false);
            if (tmp != null)
                data.put(i, tmp);
        }
        return data;
    }



}
