package com.mlt.e200cp.utilities.helper.shell;

import com.mlt.e200cp.utilities.helper.util.Crypto;
import com.mlt.e200cp.utilities.helper.util.Utility;

import java.util.Arrays;

public class MsgEncrytHandlerE5 implements IMsgEncrytHandler {

    public MsgEncrytHandlerE5() {

    }

    public MsgEncrytHandlerE5(byte [] MSK) {
        init(MSK);
    }

    long KSN = 0;
    int BLOCK_SIZE = 16;

    byte[] MSK = null;

    public boolean init(byte [] sessionKey) {

        if (sessionKey == null)
            return false;

        MSK = Arrays.copyOf(sessionKey, sessionKey.length);
        KSN = 0;

        return true;
    }

    public boolean isEnabled() {
        return MSK != null;
    }

    private byte [] fillHeader(byte [] cmd, int len) {

        byte [] fCmd = new byte[len + 3];
        fCmd[0] = 0x02;
        fCmd[len+1] = 0x03;
        System.arraycopy(cmd, 0, fCmd, 1, len);

        byte xor = 0;
        for(int i = 1 ; i < len + 2 ; i++)
            xor ^= fCmd[i];
        fCmd[len + 2] = xor;
        return fCmd;
    }

    private byte [] getHMEDK() {
        // HMEDK (16 bytes) = AES-ECB (MSK, CV_H || KSN)
        byte[] cvKsn = Utility.concateArray(
                new byte[] { 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12 }, 8,
                Utility.long2Bytes(KSN), 8);

        return Crypto.encrypt(Crypto.ALGORITHM.AES, "ECB", cvKsn, MSK);
    }

    private byte [] paddingReqPkg(byte [] data)
    {
      // {two bytes Len}{Original Data}{Padding 0x00 to mutilple of BLOCK SIZE)
        int pkgLen = BLOCK_SIZE * ((data.length + 2 + BLOCK_SIZE - 1) / BLOCK_SIZE);
        byte[] paddingData = new byte[pkgLen];
        System.arraycopy(data, 0, paddingData, 2, data.length);

        paddingData[0] = (byte)((data.length >> 8) & 0xFF);
        paddingData[1] = (byte)(data.length & 0xFF);

        return paddingData;
    }

    @Override
    public byte[] encryption(byte[] cmd, int len) {

        byte [] HMEDK = getHMEDK();

        byte [] fCmd = fillHeader(cmd, len);

        byte[] paddingData = paddingReqPkg(fCmd);

        // Encrypted Packet = AES/TDES CBC-Enc (HMEDK, IV, Original Packet with padding)
        byte[] enData = Crypto.encrypt(Crypto.ALGORITHM.AES, "CBC", paddingData, HMEDK);

        // remove hash
//        byte[] sha256 = Crypto.digestSHA256(enData, enData.length);
        byte[] hostKsn = Utility.long2Bytes(KSN);

        // E5<RS>{Len}{Encrypted Data}{KSN}
        int fCmdLen = 5 + enData.length + 8;
        byte[] ret = new byte[fCmdLen];
        int index = 0;

        ret[index++] = (byte)'E';
        ret[index++] = (byte)'5';
        ret[index++] = (byte)0x1E;
        ret[index++] = (byte)(((fCmdLen-5) >> 8) & 0xFF);
        ret[index++] = (byte)((fCmdLen-5) & 0xFF);

        System.arraycopy(enData, 0 , ret, index, enData.length);
        index += enData.length;

//        System.arraycopy(sha256, 0 , ret, index, sha256.length);
//        index += sha256.length;

        System.arraycopy(hostKsn, 0 , ret, index, hostKsn.length);
        index += hostKsn.length;

        KSN++;

        return ret;
    }

    private byte [] getDMEDK(byte [] devKsn) {

        // HMEDK (16 bytes) = AES-ECB (MSK, CV_H || KSN)
        byte[] cvKsn = Utility.concateArray(
                new byte[] { 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34 }, 8,
                devKsn, 8);
        return Crypto.encrypt(Crypto.ALGORITHM.AES, "ECB", cvKsn, MSK);

    }
    @Override
    public byte[] decryption(byte[] cmd, int len) {

        // E5<RS>{Len}{Encrypted Data}{KSN}
        int rsLen = ((cmd[3] & 0xFF) << 8) + (cmd[4] & 0xFF);
        int index = 5;

        byte[] enData = Arrays.copyOfRange(cmd, index, index + rsLen - 8);
        index += rsLen - 8;
        byte[] devKsn = Arrays.copyOfRange(cmd, index, index + 8);
        index += 8;

        byte []  DMEDK = getDMEDK(devKsn);


        byte[] data = null;

        // Decrypted Packet = AES-CBC-Dec (DMEDK, IV, Original Packet with padding)
        data = Crypto.decryt(Crypto.ALGORITHM.AES, "CBC", enData, DMEDK);

        int retLen = ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);

        // skip header, 2 (LEN padding), 1 (STX)
        index = 2 + 1;

        // Data Len - STX, ETX, LRC
        return Arrays.copyOfRange(data, index, index + retLen - 3);
    }
}
