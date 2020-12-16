package com.mlt.e200cp.utilities.helper.shell;

public interface IMsgEncrytHandler {
    boolean init(byte[] init_data);
    boolean isEnabled();
    byte [] encryption(byte[] cmd, int len);
    byte [] decryption(byte[] cmd, int len);
}
