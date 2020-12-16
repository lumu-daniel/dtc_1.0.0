package com.mlt.e200cp.utilities.helper.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simon_chen on 2017/11/27.
 */

public class CTLS extends VNG
{
    // Request
    // <STX>QR<RS>{Len}{CTLS Data = <INS>{Len}{Data} }<ETX>{LRC}
    // Response
    // <STX>QR<RS>{Len}{CTLS Data = <INS>{Len}{Status}{Data} }<ETX>{LRC}

    public boolean qnCmd = false;
    public byte ins = 0;
    public byte status = 0;
    public byte [] ctlsData = null;

    public CTLS(byte _ins) {
        ins = _ins;
        init();
    }

    public CTLS(byte _ins, byte [] data) {
        ins = _ins;
        init(data, data.length);
    }

    public CTLS(byte _ins, byte [] data, int length) {
        ins = _ins;
        init(data, length);
    }

    public CTLS(byte [] data) {
        this.addData(data);
    }

    public CTLS(byte [] data, int length) {
        this.addData(data, length);
    }

    private void init() {
        super.clear();;
        this.addData("QR");         // 0, 1
        this.addRSLength(3);                // 2, 3, 4 : reserve <RS><LEN>
        this.addData(ins);                  // 5
        this.addData(new byte [] {0, 0});   // 6, 7 reserve <LEN>

        ctlsData = null;
    }

    private void init(byte [] data, int length) {
        super.clear();;
        this.addData("QR");         // 0, 1
        this.addRSLength(3 + length);                // 2, 3, 4 : reserve <RS><LEN>
        this.addData(ins);                // 5
        this.addData(new byte [] {(byte)((length >> 8) & 0xFF), (byte)(length & 0xFF)});   // 9, 10 reserve <LEN>
        this.addData(data, length);

        ctlsData = Arrays.copyOf(data, length);
    }

    public static class CMD_INS {
        public static final byte _10_QueryCaKeySet = 0x10;
        public static final byte _11_Get_CA_Key = 0x11;
        public static final byte _12_Set_CA_Key = 0x12;
        public static final byte _13_Delete_CA_Key = 0x13;
        public static final byte _14_Get_CRL = 0x14;
        public static final byte _15_Add_CRL = 0x15;
        public static final byte _16_Delete_CRL = 0x16;
        public static final byte _20_Get_Brand_Application_Parameter = 0x20;
        public static final byte _21_Set_Brand_Application_Parameter = 0x21;
        public static final byte _22_Get_Brand_Application_Volatile_Data = 0x22;
        public static final byte _23_Set_Brand_Application_Volatile_Dada = 0x23;
        public static final byte _28_Query_Visa_Application_Program_ID = 0x28;
        public static final byte _29_Get_Visa_DRL = 0x29;
        public static final byte _2A_Add_Visa_DRL = 0x2A;
        public static final byte _2B_Delete_Visa_DRL = 0x2B;
        public static final byte _2C_Get_Exception_File = 0x2C;
        public static final byte _2D_AddDel_Exception_File = 0x2D;
        public static final byte _40_Query_Application = 0x40;
        public static final byte _41_Get_Application = 0x41;
        public static final byte _42_Add_Application = 0x42;
        public static final byte _43_Delete_Application = 0x43;
        public static final byte _50_Set_Transaction_Required_Data = 0x50;
        public static final byte _51_Set_Transaction_Indicator = 0x51;
        public static final byte _54_Return_Transaction_Online_Response = 0x54;
        public static final byte _55_CTLS_on = 0x55;
        public static final byte _56_CTLS_off = 0x56;
        public static final byte _57_Get_RFID_response_processing_info = 0x57;
        public static final byte _58_Poll_RFID_Status = 0x58;
        public static final byte _60_Get_EMV_Objects = 0x60;
        public static final byte _61_Set_EMV_Objects = 0x61;
        public static final byte _62_Get_Brand_Terminal_Capabilities = 0x62;
        public static final byte _63_Set_Brand_Terminal_Capabilities = 0x63;
        public static final byte _6A_Get_Brand_Mode = 0x6A;
        public static final byte _6B_Set_Brand_Mode = 0x6B;
        public static final byte _6C_Set_Reader_Mode = 0x6C;
        public static final byte _6D_Set_Prompts = 0x6D;
        public static final byte _6E_Set_Indicator_Mode = 0x6E;
        public static final byte _6F_Indicator_Control = 0x6F;
        public static final byte _70_Get_Wallet_Data = 0x70;
        public static final byte _71_Set_Wallet_Data = 0x71;
        public static final byte _72_Delete_Wallet_Data = 0x72;
        public static final byte _E5_Component_Info = (byte)0xE5;
        public static final byte _F2_Reset_CTLS_Configuration = (byte)0xF2;
        public static final byte _F3_Get_Logic_Template = (byte)0xF3;
        public static final byte _F4_Set_Logic_Template = (byte)0xF4;
    }

    public static class C0_BRAND_CODE {
        public static final byte MasterCard = 1;
        public static final byte VISA = 2;
        public static final byte American_Express = 3;
        public static final byte Discover = 4;
        public static final byte First_Data = 5;
        public static final byte Google = 7;
        public static final byte Interac = 8;
        public static final byte ISIS = 9;
        public static final byte Eftpos = 0x0A;
        public static final byte UnionPay = 0x0B;
        public static final byte JCB = 0x0C;
    }

    public static class TAG_MAP {

        public static Map<Byte, String> _C1_CARD_SCHEME = new HashMap<Byte, String>();
        public static Map<Byte, String> _C2_TXN_RESULT = new HashMap<Byte, String>();
        public static Map<Byte, String> _C7_TXN_CVM = new HashMap<Byte, String>();
        public static Map<Byte, String> _C8_VISA_TERM_ENTRY_CAPABILITY = new HashMap<Byte, String>();
        public static Map<Integer, String> ERR = new HashMap<Integer, String>();

        public String getDescription(int tag, int value) {

            if (tag == 0xC1) {
                return _C1_CARD_SCHEME.get(value);
            } else if (tag == 0xC2) {
                return _C2_TXN_RESULT.get(value);
            } else if (tag == 0xC7) {
                return _C7_TXN_CVM.get(value);
            } else if (tag == 0xC8) {
                return _C8_VISA_TERM_ENTRY_CAPABILITY.get(value);
            } else
                return "unknown";
        }

        static
        {
            _C1_CARD_SCHEME.put( (byte)0x16 , "VISA_Wave_2" );
            _C1_CARD_SCHEME.put( (byte)0x17 , "VISA_qVSDC" );
            _C1_CARD_SCHEME.put( (byte)0x18 , "VISA_MSD" );
            _C1_CARD_SCHEME.put( (byte)0x20 , "MC_MagStripe" );
            _C1_CARD_SCHEME.put( (byte)0x21 , "MC_Mchip" );
            _C1_CARD_SCHEME.put( (byte)0x40 , "AE_MagStripe" );
            _C1_CARD_SCHEME.put( (byte)0x41 , "AE_Chip" );
            _C1_CARD_SCHEME.put( (byte)0x80 , "Discover_MagStripe" );
            _C1_CARD_SCHEME.put( (byte)0xA0 , "FD_Wallet_Gift_Card" );
            _C1_CARD_SCHEME.put( (byte)0xA2 , "FD_Star_Card" );
            _C1_CARD_SCHEME.put( (byte)0xA4 , "Google_Wallet" );
            _C1_CARD_SCHEME.put( (byte)0xA6 , "Interac_Chip" );

            _C2_TXN_RESULT.put( (byte)1 , "Offline_Approve" );
            _C2_TXN_RESULT.put( (byte)2 , "Offline_Decline" );
            _C2_TXN_RESULT.put( (byte)4 , "MagStripe" );
            _C2_TXN_RESULT.put( (byte)6 , "Chip_Online_Request" );
            _C2_TXN_RESULT.put( (byte)8 , "Terminated" );
            _C2_TXN_RESULT.put( (byte)0xA6 , "Try_Again" );

            _C7_TXN_CVM.put( (byte)0 , "No_CVM_Required" );
            _C7_TXN_CVM.put( (byte)1 , "Signature" );
            _C7_TXN_CVM.put( (byte)2 , "OnlinePin" );
            _C7_TXN_CVM.put( (byte)3 , "Signature_OnlinePin" );
            _C7_TXN_CVM.put( (byte)4 , "Confirmation_Code_Verified" );
            _C7_TXN_CVM.put( (byte)5 , "No_CVM_Perference" );

            _C8_VISA_TERM_ENTRY_CAPABILITY.put( (byte)5 , "Support_VSDC_Contact_Chip" );
            _C8_VISA_TERM_ENTRY_CAPABILITY.put( (byte)8 , "Not_Support_VSDC_Contact_Chip" );
        };
    }

    @Override
    public void clear() {
        init();
        parseIndex = 5;     // QR<RS><LEN>
    }

    @Override
    public byte [] getCmdBuffer() {
        // QR<RS>{Len 1}{CTLS Data = <INS>{Len}{Data} }
        // Len 1 = cmd size - 5
        // Len 2 = cmd size - 8

        buffer[3] = (byte)(((size - 5) >> 8) & 0xFF);
        buffer[4] = (byte)((size - 5) & 0xFF);
        buffer[6] = (byte)(((size - 8) >> 8) & 0xFF);
        buffer[7] = (byte)((size - 8) & 0xFF);

        return super.getCmdBuffer();
    }

    public boolean tryToParse() {

        // QR<RS>{Len 1}{Ins}{Status}{Len 2}{Data}
        if (size < 9)   // minimum package size
            return false;

        parseIndex = 0;

        String cmd = parseString(2);

        if (cmd.equals("QN") || cmd.equals("QR")) {

            qnCmd = cmd.equals("QN");

            int ctlsLen = parseRSLen();
            if (ctlsLen == -1)
                return false;

            ins = parseByte();
            int ctlsDataLen = parseValue(2) - 1;    // data size without status byte
            status = parseByte();

            if (ctlsDataLen + parseIndex > size)
                return false;

            if (ctlsDataLen != 0)
                ctlsData = parseBytes(ctlsDataLen);

        } else
            return false;

        return true;
    }

    public static CTLS tryToParse(byte [] data) {

        CTLS cmd = new CTLS(data);

        if (!cmd.tryToParse())
            return null;

        return cmd;
    }

    public String parseCtlsData(TLV e2) {
        // {Len}{flag}{TK1 data}<FS>{TK2 data}<FS>{TK3 data}<RS>{Len}{XAC Proprietary template}
        // {Len}{Status}{Data}

        String retStr = "";

        String[] tk = new String[3];
        int index = 0;
        int j = 0;

        // {TK1 data}<FS>{TK2 data}<FS>{TK3 data}
        for (index = 0; index < ctlsData.length; index++)
        {
            if (ctlsData[index] == (byte) VngDef.FS)
            {
                j++;
            }
            else if (ctlsData[index] == (byte) VngDef.RS)
            {
                index++;
                break;
            }
            else
            {
                tk[j] += (char)ctlsData[index];
            }
        }

        if (tk[0] != null)
            retStr += "<Track 1> " + tk[0] + "\r\n";
        if (tk[1] != null)
            retStr += "<Track 2> " + tk[1] + "\r\n";
        if (tk[2] != null)
            retStr += "<Track 3> " + tk[2] + "\r\n";

        // {Len}{XAC Proprietary template}
        if (index + 2 < ctlsData.length)
        {
            int xacTemplateLen = ((int)ctlsData[index] << 8) + ctlsData[index + 1];
            index += 2;

            if (index + xacTemplateLen == ctlsData.length)
            {
                TLV tlv = new TLV(Arrays.copyOfRange(ctlsData, index, ctlsData.length));

                TLV.ENTRY [] tlvList = tlv.parseTLV();

                for(int i = 0 ; i < tlvList.length ; i++)
                {
                    if (tlvList[i].tag == 0xE0)
                    {
                        retStr += "E0 template–Vendor defined data elements template.\r\n";
                    }
                    else if (tlvList[i].tag == 0xE2)
                    {
                        retStr += "E2 template – EMV offline/online chip data template.\r\n";
                        e2.addData(tlvList[i].value);
                    }
                    else if (tlvList[i].tag == 0xE4)
                    {
                        retStr += "E4 template – ISIS wallet data & Date/Time stamp (DF11).\r\n";
                    }
                    else if (tlvList[i].tag == 0xE5)
                    {
                        retStr += "E5 template – PayPass 3.0 Discretionary Data .\r\n";
                    }
                    else if (tlvList[i].tag == 0xEA)
                    {
                        retStr += "EA template - Torn Record Template.\r\n";
                    }
                    else
                    {
                        retStr += "unknow template - 0x" + Integer.toString(tlvList[i].tag, 16) + ".\r\n";
                    }
                }
            }
        }
        return retStr;
    }
}