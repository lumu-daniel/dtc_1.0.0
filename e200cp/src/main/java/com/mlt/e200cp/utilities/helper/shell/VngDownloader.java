package com.mlt.e200cp.utilities.helper.shell;


import com.mlt.e200cp.utilities.helper.protocol.VNG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class VngDownloader extends BaseShell {

    public VngDownloader() {}

    public interface UpdateProgress{
        void run(String file, int numBlock, int totalBlocks);
    }

    private UpdateProgress updateProgress = null;

    private final int IMAGE_DOWNLOAD_TIMEOUT = 30000;

    public enum ConnectionType { USB, RS232 }
    private ConnectionType type = ConnectionType.RS232;

    private int dlBlocks = 0;

    List<File> list = new ArrayList<File>();

    public String init(ConnectionType type, String path, UpdateProgress updateProgress)
    {
        if (!portManager.isConnected())
            return "device is not connected";

        this.type = type;
        this.updateProgress = updateProgress;
        list.clear();

        File[] files = new File(path).listFiles();

        for(int i = 0 ; i < files.length ; i++)
        {
            if (!files[i].getName().toUpperCase().endsWith("COR"))
                continue;

            if (files[i].length() < 896 + 2048 || ((files[i].length() - 896) % 2048 != 0))
                return "incorrect image format";

            if (isOSImage(files[i].getName()))
                list.add(0, files[i]);
            else
                list.add(files[i]);
        }

        if (list.size() == 0)
            return "empty list";

        return "OK";
    }

    public String startDownload()
    {
        MODE mode = getMode(false);
        if (mode == MODE.Error) return "fail to get firmware mode";

        String result = "";

        for (int i = 0; i < list.size(); i++)
        {
            dlBlocks = (int)(list.get(i).length() - 896) / 2048;

            updateProgress.run("Update Image : " + list.get(i).getName(), i, list.size());

            if (i == 0 && mode != MODE.Loader)
            {
                if (!(result = X72_InitDownloadFirmware(list.get(i), mode)).equals("OK"))
                    return result;
                if (getMode(true) != MODE.Loader)
                    return "fail to jump to loader mode";
            }

            if (!(result = X72_InitDownloadFirmware(list.get(i), mode)).equals("OK"))
                return result;
            if (!(result = X73_StartDownloadFirmware(list.get(i))).equals("OK"))
                return result;
            if (!(result = X74_FinalDownloadFirmware(list.get(i))).equals("OK"))
                return result;

            if (i == 0 && isOSImage(list.get(i).getName()))
            {
                if (getMode(true) == MODE.Error)
                    return "error after loading OS image";
            }
        }

        portManager.sendData("X81".getBytes(), 3);

        if (getMode(true) != MODE.Normal)
            return "fail to return normal mode";

        return "Download Completed";
    }

    private boolean isOSImage(String fileName)
    {
        if (fileName.startsWith("00_") ||
                fileName.startsWith("0000_") ||
                fileName.startsWith("m00_") ||
                fileName.startsWith("m0000_"))
            return true;
        return false;
    }

    private String X72_InitDownloadFirmware(File dlFile, MODE mode)
    {
        updateProgress.run(dlFile.getName() + " - X72", 0, dlBlocks);

        // <STX>X72{SZ}{LEN}{Data}<ETX>{LRC}
        // -- SZ is 1 byte binary data = 0x0E
        // -- LEN is two bytes binary of {Data} = 896 (0x03 0x80)
        // <STX>X72{Status}<ETX>{LRC}
        // -- Status =0 success, 1 = fail
        VNG req = new VNG();
        VNG rsp;
        byte[] data = new byte[896];

        try(FileInputStream inputStream = new FileInputStream(dlFile)){

            inputStream.skip(dlFile.length() - 896);
            inputStream.read(data);

        } catch (FileNotFoundException e) {
            return "file not found : " + dlFile.getName();
        } catch (IOException e) {
            return "fail to read file : " + dlFile.getName();
        }


        // 1. X72 + 896 bytes Dummy
        req.clear();
        req.addData("X72");
        req.addData(new byte[] { 0x0E, 0x03, (byte)0x80 });
        req.addData(data);

        if ((rsp = portManager.exchangeData(req, IMAGE_DOWNLOAD_TIMEOUT)) == null)
            return "X72 No Response";

        if (!rsp.parseHeader("X72"))
            return "X72 Unexpected Response";

        byte status = rsp.parseByte();
        if (status != 0)
            return "X72 Error " + status;

        return "OK";
    }

    private String X73_StartDownloadFirmware(File dlFile)
    {
        // 1. Send 2k block to Length - 896
        // <STX>X73{SEQ}{Data}<ETX>{LRC}
        // -- SEQ is the sequence number (1 byte binary data)
        // -- DATA is 2048 bytes binary data
        // <STX>X73{SEQ}{Status}<ETX>{LRC}
        // -- SEQ is the sequence number, (1 byte binary data)
        // -- Status =0 success, 1 = fail
        VNG req = new VNG();
        VNG rsp = null;
        byte seq = 1;

        try(FileInputStream inputStream = new FileInputStream(dlFile)) {

            int offset = 0;
            int endIndex = (int)(dlFile.length() - 896);

            while (offset + 2048 <= endIndex)
            {
                updateProgress.run(dlFile.getName() + " - X73", seq, dlBlocks);

                byte [] block = new byte[2048];
                inputStream.read(block);
                offset += 2048;

                req.clear();
                req.addData("X73");
                req.addData(seq);
                req.addData(block);

                if ((rsp = portManager.exchangeData(req, IMAGE_DOWNLOAD_TIMEOUT))== null)
                    return "X73 No Response";

                if (!rsp.parseHeader("X73"))
                    return "X73 Unexpected Response";

                if (rsp.parseByte() != seq)
                    return "X73 Mismatch Sequence";

                byte status = rsp.parseByte();
                if (status != 0)
                    return "X73 Error " + status;

                seq++;
            }

        } catch (FileNotFoundException e) {
            return "file not found : " + dlFile.getName();
        } catch (IOException e) {
            return "fail to read file : " + dlFile.getName();
        }



        return "OK";
    }

    private String X74_FinalDownloadFirmware(File dlFile)
    {
        updateProgress.run(dlFile.getName() + " - X74", dlBlocks, dlBlocks);

        // <STX>X74<ETX>{LRC}
        // -- X74 return packet
        // <STX>X74{Status}<ETX>{LRC}
        // -- Status =0 success, 1 = fail

        VNG req = new VNG();
        VNG rsp = null;

        req.clear();
        req.addData("X74");

        if ((rsp = portManager.exchangeData(req)) == null)
            return "X74 No Response";

        if (!rsp.parseHeader("X74"))
            return "X74 Unexpected Response";

        byte status = rsp.parseByte();
        if (status != 0)
            return "X74 Error " + status;

        return "OK";
    }

    private enum MODE { Loader, Normal, Error }
    private MODE getMode(boolean waitReconnected)
    {
        VNG rsp = null;

        if (waitReconnected)
        {
            long startTime = System.currentTimeMillis();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                appendLog(e.getLocalizedMessage());
            }

            int RESET_TIMEOUT = 15000;
            if (type == ConnectionType.USB)
            {
                while(!portManager.isConnected())
                {
                    if (System.currentTimeMillis() - startTime > RESET_TIMEOUT)
                        return MODE.Error;
                }
                rsp = portManager.exchangeData(new VNG("R1"));
                if (rsp == null) return MODE.Error;
            }
            else if (type == ConnectionType.RS232)
            {
                while(rsp == null)
                {
                    rsp = portManager.exchangeData(new VNG("R1"));
                    if (rsp == null && (System.currentTimeMillis() - startTime > RESET_TIMEOUT))
                        return MODE.Error;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        appendLog(e.getLocalizedMessage());
                    }
                }
            }

            portManager.purge();
        }
        else
        {
            rsp = portManager.exchangeData(new VNG("R1"));
            if (rsp == null) return MODE.Error;
        }

        if (rsp.parseHeader("R1G") || rsp.parseHeader("R1E"))
            return MODE.Loader;
        else
            return MODE.Normal;
    }
}
