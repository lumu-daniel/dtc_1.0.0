package com.mlt.e200cp.utilities.helper.ports;

import android.content.Context;

import com.mlt.e200cp.utilities.helper.shell.Def;
import com.mlt.e200cp.utilities.helper.shell.IMsgEncrytHandler;
import com.mlt.e200cp.utilities.helper.util.Utility;

import saioapi.comm.v2.ComManager;

import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;

public class CommPort {

    public static boolean debug = false;

    public int READ_TIMEOUT = 3000;
    public ComManager com = null;
    public int devPort = 0;
    public int devProtocol = 0;

    private final static int MAX_PACKAGE_SIZE = 2048;

    public CommPort(Context context) {

        com = new ComManager(context);
        setListener();
    }

    public void setListener() {
        com.setOnComEventListener(event -> {
            switch(event){
                case ComManager.EVENT_CONNECT:
                    if (eventCallback != null)
                        eventCallback.onEvent(ERROR.CONNECTED, null, 0);
                    break;
                case ComManager.EVENT_DISCONNECT:
                    if (eventCallback != null)
                        eventCallback.onEvent(ERROR.DISCONNECTED, null, 0);
                    break;
                case ComManager.EVENT_EOT:
                    break;
                case ComManager.EVENT_DATA_READY:
                    byte [] tmp = new byte[MAX_PACKAGE_SIZE];
                    int nRead = readData(tmp, tmp.length);
                    if (nRead > 0 && nRead != -1){
                        if (eventCallback != null)
                            eventCallback.onEvent(ERROR.DATA_READY, tmp, nRead);
                    }
                    break;
                default:
                    break;
            }
        });
    }

    //region connection

    public boolean connect() {
        return connect(com.getBuiltInEppDevId(), ComManager.PROTOCOL_XAC_VNG);
    }

    public boolean connect(int port) {
        return connect(port, ComManager.PROTOCOL_XAC_VNG);
    }

    public boolean connect(int port, int protocol) {

        if (com.open(port) != ComManager.ERR_OPERATION){
            com.connect(protocol);
            setListener();
            devPort = port;
            devProtocol = protocol;
            return true;
        }
        log(String.valueOf(com.lastError()),LINE_OUT());
        return false;
    }

    public void disconnect() {
        com.setOnComEventListener(null);
        com.close();
    }

    public boolean isConnected() { return com.isOpened(); }

    //endregion

    //region read/write

    public boolean sendData(byte [] data) {
        return sendData(data, data.length);
    }

    public boolean sendData(byte [] data, int length) {
        int ret = 0;
        if (com != null && com.isOpened()) {

            if (msgEncrytHandler != null && msgEncrytHandler.isEnabled() && Def.requiredEncryption(data)) {
                data = msgEncrytHandler.encryption(data, length);
                length = data.length;
            }

            if (debug)
                log("-> " + devPort + " : " + Utility.bytes2Hex(data, length), LINE_OUT());

            ret = com.write(data, length, 2000);
            if (ret != ComManager.ERR_OPERATION) {
                return true;
            }
        }
        return false;
    }

    public int readData(byte [] data, int length) {
        int nRead = com.read(data, data.length, READ_TIMEOUT);

        if (debug)
            log("<- " + devPort + " : " + Utility.bytes2Hex(data, nRead), LINE_OUT());

        // E3 ~ E8 : message encryption header
        if(msgEncrytHandler != null) {
            if (nRead >= 5 && data[0] == (byte)'E' && data[1] >= 0x33 && data[1] <= 0x38 && msgEncrytHandler.isEnabled()) {
                byte [] tmp = msgEncrytHandler.decryption(data, length);
                if (tmp != null) {
                    System.arraycopy(tmp, 0, data, 0, tmp.length);
                    nRead = tmp.length;
                }
            }
        }

        return nRead;
    }

    //endregion

    //region EVENT

    public enum ERROR {
        DATA_READY, CONNECTED, DISCONNECTED, OTHERS
    }

    public interface Event {
        void onEvent(ERROR err, byte[] data, int len);
    }

    Event eventCallback = null;
    public void setOnEvent(Event onEvent) {
        eventCallback = onEvent;
    }

    //endregion

    //region Message encryption

    public IMsgEncrytHandler msgEncrytHandler = null;

    public void setMsgEncrytHandler(IMsgEncrytHandler handler) {
        msgEncrytHandler = handler;
    }

    //endregion
}
