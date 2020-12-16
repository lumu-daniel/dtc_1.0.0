package com.mlt.e200cp.utilities.helper.protocol;


import android.util.Log;

import com.mlt.e200cp.utilities.helper.util.Utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simon_chen on 2013/6/27.
 */
public class EMV extends VNG {

    // <STX>YM<RS><LEN><CMD ID><CMD TYPE><P1><P2><LEN><DATA_READY><ETX>

    public byte cmdId = 0;
    public byte cmdType = 0;
    public byte[] parameter = new byte[2];
    public TLV dataField = null;

    public boolean isSuccess = false;
    public boolean isEmv = false;

    public enum RESULT { UNKNONW, TC, AAC, ARPC }
    public RESULT result = RESULT.UNKNONW;

    public EMV() {
        init();
    }

    public EMV(byte [] data) {
        setData(data);
    }

    public EMV(byte _cmdId) {
        setHeader(_cmdId, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0);
        init();
    }

    public EMV(byte _cmdId, byte _cmdType, byte P1, byte P2) {
        setHeader(_cmdId, _cmdType, P1, P2);
        init();
    }

    public EMV(byte _cmdId, byte _cmdType, byte P1, byte P2, byte [] data) {
        setHeader(_cmdId, _cmdType, P1, P2);
        init(data, data.length);
    }

    public EMV(byte _cmdId, byte _cmdType, byte P1, byte P2, byte [] data, int length) {
        setHeader(_cmdId, _cmdType, P1, P2);
        init(data, length);
    }

    public EMV(byte _cmdId, byte _cmdType, byte P1, byte P2, TLV data) {
        setHeader(_cmdId, _cmdType, P1, P2);
        dataField = data;
        init();
    }

    private void setHeader(byte _cmdId, byte _cmdType, byte P1, byte P2) {
        cmdId = _cmdId;
        cmdType = _cmdType;
        parameter[0] = P1;
        parameter[1] = P2;
    }

    private void init() {
        super.clear();;
        this.addData("YM");                 // 0, 1
        this.addRSLength(6);                // 2, 3, 4 : reserve <RS><LEN>
        this.addData(cmdId);                // 5
        this.addData(cmdType);              // 6
        this.addData(parameter);            // 7, 8
        this.addData(new byte [] {0, 0});   // 9, 10 reserve <LEN>
    }

    private void init(byte [] data, int length) {
        super.clear();;
        this.addData("YM");                 // 0, 1
        this.addRSLength(6);                // 2, 3, 4 : reserve <RS><LEN>
        this.addData(cmdId);                // 5
        this.addData(cmdType);              // 6
        this.addData(parameter);            // 7, 8
        this.addData(new byte [] {0, 0});   // 9, 10 reserve <LEN>

        if (data != null && length != 0) {
            dataField = new TLV();
            dataField.addData(data, length);
        }
    }

    @Override
    public void clear()
    {
        init();
        parseIndex = 5;     // YM<RS><LEN>
    }

    @Override
    public void setData(byte [] rspData)
    {
        super.setData(rspData);
        isEmv = tryToParse();
    }

    @Override
    public byte [] getCmdBuffer()
    {
        buffer[7] = parameter[0];
        buffer[8] = parameter[1];

        if (dataField != null && !dataField.isEmpty()) {
            byte [] data = dataField.toByteArray();
            addData(data);
        }
        buffer[3] = (byte)(((this.size -5) >> 8) & 0xFF);
        buffer[4] = (byte)((this.size - 5) & 0xFF);
        buffer[9] = (byte)(((this.size -11) >> 8) & 0xFF);
        buffer[10] = (byte)((this.size -11) & 0xFF);

        return super.getCmdBuffer();
    }

    public boolean tryToParse() {

        isEmv = false;

        if (size < 11)   // minimum package size
            return false;

        parseIndex = 0;

        if (!parseString(2).equals("YM"))
            return false;

        int emvLen = parseRSLen();

        if (emvLen == -1)
            return false;

        cmdId = parseByte();
        cmdType = parseByte();
        parameter = parseBytes(2);
        int dataFieldLen = parseValue(2);

        if (dataFieldLen + parseIndex > size)
            return false;

        if (dataFieldLen != 0)
            dataField = new TLV(parseBytes(dataFieldLen));

        isSuccess = checkError();
        isEmv = true;

        return true;
    }

    public static EMV tryToParse(byte [] data) {

        EMV cmd = new EMV(data);
        if (!cmd.tryToParse())
            return null;
        return cmd;
    }

    public String getError() {
        int err = ((parameter[0] & 0xFF) << 8) + (parameter[1] & 0xFF);
        if (EMV_MAP.ERR.containsKey(err))
            return EMV_MAP.ERR.get(err);
        else
            return "EMV UNKNOWN ERR : 0x" + Integer.toString(err, 16);
    }

    private void checkResult() {
        if (parameter[0] == 0x00 && (parameter[1] == 0x00 || parameter[1] == (byte)0xC8)) {         // TC
            result = RESULT.TC;
        } else if (parameter[0] == 0x00 && (parameter[1] == 0x01 || parameter[1] == (byte)0xC9)) {  // AAC
            result = RESULT.AAC;
        } else if (parameter[0] == 0x00 && parameter[1] == 0x02) {  // ARQC
            result = RESULT.ARPC;
        } else
            result = RESULT.UNKNONW;
    }

    public boolean checkError() {
        if (cmdId == CMD_ID._51_PayStart) {
            if ((parameter[0] == (byte)0) && (parameter[1] == (byte)0))
                return true;
        } else if (cmdId == CMD_ID._52_PayAuth) {
            if (parameter[0] == (byte)0 && parameter[1] == (byte)0)
                return true;
        } else if (cmdId == CMD_ID._53_PayFirstGenAC) {
            if (parameter[0] == (byte)0 && (parameter[1] >= 0 && parameter[1] <= 2)) {
                checkResult();
                return true;
            }
        } else if (cmdId == CMD_ID._54_PaySecGenAC) {
            if (parameter[0] == (byte)0) {
                if (parameter[1] == 0x00 || parameter[1] == 0x01 || parameter[1] == (byte)0xC8 || parameter[1] == (byte)0xC9) {
                    checkResult();
                    return true;
                }
            }
        } else {
            if (parameter[0] == (byte)0)
                return true;
        }
        return false;
    }

    // region EMV Definition

    public static class ICS_DATA {
        public byte[] szIcsName;
        public byte byTerminalType;
        // bit8: offline only
        // bit7: offline with online
        // bit6: online only
        // bit5: attend
        // bit4: unattend
        // bit3: finical
        // bit2: cardholder
        // bit1: merchant

        public byte byCardDataInput;    // bit8: Manual TestData Entry
        // bit7: Magnetic Stripe
        // bit6: IC with Contacts (M)

        public byte byCVMCapability;
        // bit8: offline palin pin
        // bit7: online encrypher pin
        // bit6: signature
        // bit5: offline encipher pin
        // bit4: no cvm required

        public byte bySecurityCapability; // bit8: SDA
        // bit7: DDA
        // bit6: Card capture
        // bit5: RFU
        // bit4: CDA

        public byte byTxnTypeB1;        // byte1 bit8: cash
        // byte1 bit7: goods
        // byte1 bit6: service
        // byte1 bit5: cashback
        // byte1 bit4: Inquiry
        // byte1 bit3: Transfer
        // byte1 bit2: Payment
        // byte1 bit1: Administrative
        public byte byTxnTypeB2;
        // byte2 bit8: Cash Deposit
        // byte2 bit7~bit1: RFU

        public byte byTmDataInput;        //	bit8: Keypad
        //	bit7: Numeric Keys
        //	bit6: Alphabetic and Special Character Keys
        //	bit5: Command Keys
        //	bit4: Function Keys

        public byte byTmDataOutputB1;    //	byte1 bit8: Print Attendant
        //	byte1 bit7: Print Cardholder
        //	byte1 bit6: Display Attendant
        //	byte1 bit5: Display Cardholder
        //	byte1 bit4: RFU
        //	byte1 bit3: RFU
        //	byte1 bit2: Code Table 10
        //	byte1 bit1: Code Table 9

        public byte byTmDataOutputB2;   //	byte2 bit8: Code Table 8
        //	byte2 bit7: Code Table 7
        //	byte2 bit6: Code Table 6
        //	byte2 bit5: Code Table 5
        //	byte2 bit4: Code Table 4
        //	byte2 bit3: Code Table 3
        //	byte2 bit2: Code Table 2
        //	byte2 bit1: Code Table 1

        public byte byApplicationSelect;    // bit8: support PSE
        // bit7: support cardholder confirm
        // bit6: prefer order
        // bit5: partial aid select(M)
        // bit4: support multi language
        // bit3: common character set(M)
        // bit2: EMV Language selection Method

        public byte byMaxCAPK;
        public byte byExponent;            //	bit8: 3
        //	bit7: 2^16+1
        //	bit6: 3 and 2^16+1
        public byte byDataAuthen;        // bit8: support check revocation ca public key
        // bit7: contain default DDOL
        // bit6: operator ation required during loading ca public key fail
        // bit5: verify ca public key checksum

        public byte byCVM;                // bit8: support bypass pin entry
        // bit7: support Sub bypass pin entry
        // bit6: support get data for pin try counter
        // bit5: support fail cvm
        // bit4: amount knows before cvm

        public byte byTRM;                // bit8: floor limit checking
        // bit7: random selection
        // bit6: velocity checking
        // bit5: support transaction log
        // bit4: support exception file
        // bit3: performance of terminal risk management based on AIP setting

        public byte byActionAnalysis;    // bit8: support terminal action codes
        // bit7: default action codes prior to first generate ac
        // bit6: default action codes after first generate ac
        // bit5: TAC/IAC default skipped
        // bit4: TAC/IAC default normal
        // bit3: CDA failure before TAA
        // bit2: Can the values of TACs be changed
        // bit1: Can the TACs be deleted or disabled


        public byte byCDAMode;            // bit8: CDA always 1 GenAC
        // bit7: CDA never 1 GenAC
        // bit6: CDA always 2 GenAC
        // bit5: CDA never 2 GenAC

        public byte byCompletion;        // bit8: support force online
        // bit7: support force accept
        // bit6: support advice
        // bit5: support issuer initiated voice referral
        // bit4: support batch data capture
        // bit3: support online data capture
        // bit2: support default TDOL

        public byte byException;        // POS entry mode

        public byte byMISC;                // bit8: PINPAD
        // bit7: PIN and amount in same keypad
        // bit6: ICC/Magstripe Reader commbined
        // bit5: Magstripe Reader first
        // bit4: Account Type select
        // bit3: on fly script processing
        // bit2: Issuer Script device limt >128?
        // bit1: Internel date management

        public byte bySelKernelConfig;    // bit8: Support Selectable Kernel Config
        // bit7: Amount Select Range Criteria X
        // bit6: Amount Select Range Criteria X and Y
        // bit5: Amount Select Range Criteria X per AID
        // bit4: Amount Select Range Criteria X and Y per AID

        public byte[] toByteArray() {
            int index = 0;

            byte[] data = new byte[44];

            data[index++] = (byte) 0xDF;
            data[index++] = (byte) 0x01;
            data[index++] = 41;

            System.arraycopy(szIcsName, 0, data, index, 20);
            index += 20;

            data[index++] = byTerminalType;
            data[index++] = byCardDataInput;
            data[index++] = byCVMCapability;
            data[index++] = bySecurityCapability;
            data[index++] = byTxnTypeB1;
            data[index++] = byTxnTypeB2;
            data[index++] = byTmDataInput;
            data[index++] = byTmDataOutputB1;
            data[index++] = byTmDataOutputB2;
            data[index++] = byApplicationSelect;
            data[index++] = byMaxCAPK;
            data[index++] = byExponent;
            data[index++] = byDataAuthen;
            data[index++] = byCVM;
            data[index++] = byTRM;
            data[index++] = byActionAnalysis;
            data[index++] = byCDAMode;
            data[index++] = byCompletion;
            data[index++] = byException;
            data[index++] = byMISC;
            data[index++] = bySelKernelConfig;

            return data;
        }

        public ICS_DATA() {
            szIcsName = new byte[20];
            byTerminalType = 0;
            byCardDataInput = 0;
            byCVMCapability = 0;
            bySecurityCapability = 0;
            byTxnTypeB1 = 0;
            byTxnTypeB2 = 0;
            byTmDataInput = 0;
            byTmDataOutputB1 = 0;
            byTmDataOutputB2 = 0;
            byApplicationSelect = 0;
            byMaxCAPK = 0;
            byExponent = 0;
            byDataAuthen = 0;
            byCVM = 0;
            byTRM = 0;
            byActionAnalysis = 0;
            byCDAMode = 0;
            byCompletion = 0;
            byException = 0;
            byMISC = 0;
            bySelKernelConfig = 0;
        }
    }

    public static class CANDIDATE
    {
        public byte byIndex;
        public byte byAppPI;						// 87 ApplPriorityIndicator
        public String szAid;                       // 9F16 AID
        public String szAppLabel;                  // 50 Application Label
        public String szApplName;                  // 9F12 Application Preferred Name
        public byte byIssuerCTI;					// 9F11 IssuerCodeTableIndex
        public byte[] byLanguagePre;

        public CANDIDATE()
        {
            byIndex = 0;
            byAppPI = 0;
            szAid = "";
            szAppLabel = "";
            szApplName = "";
            byIssuerCTI = 0;
            byLanguagePre = new byte[8];
        }

        public CANDIDATE(byte [] candidate)
        {
            int index = 0;

            byIndex = candidate[index++];
            byAppPI = candidate[index++];

            szAid = new String(Arrays.copyOfRange(candidate, index, index + 33)).trim();
            index += 33;

            szAppLabel = new String(Arrays.copyOfRange(candidate, index, index + 17)).trim();
            index += 17;

            szApplName = new String(Arrays.copyOfRange(candidate, index, index + 17)).trim();
            index += 17;

            byIssuerCTI = candidate[index++];

            byLanguagePre = new byte[8];
            System.arraycopy(candidate, index, byLanguagePre, 0, 8);
            index += 8;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("{AppEventBus:").append(byIndex)
                    .append(" App Priority=").append(byAppPI)
                    .append(", AID=").append(szAid)
                    .append(", App Label=").append(szAppLabel)
                    .append(", App Name=").append(szApplName)
                    .append(", Issue CTI=").append(byIssuerCTI)
                    .append(", Language Pre=").append(Utility.bytes2Hex(byLanguagePre))
                    .append("}").toString();
        }
    }

    public static class EPBINFO
    {
        public int byPubKeyLen;
        public byte[] byPubkey;
        public byte[] byExponents;
        public byte[] byChallenge;

        public EPBINFO()
        {
            byPubkey = new byte[256];
            byPubKeyLen = 0;
            byExponents = new byte[4];
            byChallenge = new byte[8];
        }
        public EPBINFO(byte [] epbInfo)
        {
            // {PK Len}{PK Modulus}{Challenge}{PK Exponent}

            int index = 0;

            byPubKeyLen = epbInfo[index++];

            byPubkey = new byte[256];
            System.arraycopy(epbInfo, index, byPubkey, 0, byPubKeyLen);
            index += (int)byPubKeyLen;

            byChallenge = new byte[8];
            System.arraycopy(epbInfo, index, byChallenge, 0, 8);
            index += 8;

            byExponents = new byte[4];
            System.arraycopy(epbInfo, index, byExponents, 0, 4);
            index += 4;
        }
    }

    public static class CVM_PIN_TYPE
    {
        public static final int OnlinePin = 1;
        public static final int OfflinePlaintextPin = 2;
        public static final int OfflineEncryptedPin = 3;
    }

    public static class TXN_TYPE
    {
        //Trans Type
        public static final int EMV_TRANSTYPE_CASH = 0x0001;
        public static final int EMV_TRANSTYPE_GOODS = 0x0002;
        public static final int EMV_TRANSTYPE_SERVICES = 0x0004;
        public static final int EMV_TRANSTYPE_CASHBACK = 0x0008;
    }

    public static class GENERATE_AC
    {
        public static final int TC = 0;
        public static final int AAC = 1;
        public static final int ARQC = 2;
        public static final int TC_NEED_PRINT = 200;
        public static final int AAC_NEED_PRINT = 201;
    }

    public static class EMV_MAP {
        public static Map<Integer, String> MSG_TABLE = new HashMap<Integer, String>();
        public static Map<Integer, String> ERR = new HashMap<Integer, String>();

        static
        {
            MSG_TABLE.put( 0x00 , "" );
            MSG_TABLE.put( 0x01 , "AMOUNT" );
            MSG_TABLE.put( 0x02 , "AMOUNT OK?" );
            MSG_TABLE.put( 0x03 , "APPROVED" );
            MSG_TABLE.put( 0x04 , "CALL YOUR BANK" );
            MSG_TABLE.put( 0x05 , "CANCEL OR ENTER" );
            MSG_TABLE.put( 0x06 , "CARD ERROR" );
            MSG_TABLE.put( 0x07 , "DECLINED" );
            MSG_TABLE.put( 0x08 , "ENTER AMOUNT" );
            MSG_TABLE.put( 0x09 , "ENTER PIN" );
            MSG_TABLE.put( 0x0A , "INCORRECT PIN" );
            MSG_TABLE.put( 0x0B , "INSERT CARD" );
            MSG_TABLE.put( 0x0C , "NOT ACCEPTED" );
            MSG_TABLE.put( 0x0D , "PIN OK" );
            MSG_TABLE.put( 0x0E , "Please Wait" );
            MSG_TABLE.put( 0x0F , "PROCESSING ERROR" );
            MSG_TABLE.put( 0x10 , "REMOVE CARD" );
            MSG_TABLE.put( 0x11 , "USE CHIP READER" );
            MSG_TABLE.put( 0x12 , "USE MAG STRIPE" );
            MSG_TABLE.put( 0x13 , "TRY AGAIN" );
            MSG_TABLE.put( 0xA1 , "INPUT PASSWORD" );
            MSG_TABLE.put( 0xA2 , "NEW PASSWORD" );
            MSG_TABLE.put( 0xA3 , "EMV PROCESS..." );
            MSG_TABLE.put( 0xA4 , "LAST PIN TRY" );
            MSG_TABLE.put( 0xA5 , "ONLINE PIN OK" );
            MSG_TABLE.put( 0xA6 , "CANCEL PIN" );
            MSG_TABLE.put( 0xD1 , "APP SELECT" );

            ERR.put( 0xF101, "EMVERR_TRANSACTION_TYPE_NOT_SUPPORT" );
            ERR.put( 0xF102, "EMVERR_INVALID_PARAMETER" );
            ERR.put( 0xF103, "EMVERR_MAJOR_CONFIG_SETTING" );
            ERR.put( 0xF104, "EMVERR_USER_CANCEL" );
            ERR.put( 0xF105, "EMVERR_ICC_COMM_ERROR" );
            ERR.put( 0xF106, "EMVERR_CB_NOTSET" );
            ERR.put( 0xF107, "EMVERR_TAG_NOTSUPPORT" );
            ERR.put( 0xF500, "EMVERR_INVALID_RSPDATA" );
            ERR.put( 0xF501, "EMVERR_UNSUFFICIENTDATA" );
            ERR.put( 0xF502, "EMVERR_INVALID_TLV" );
            ERR.put( 0xF503, "EMVERR_STOP_TRANSACTION" );//msr required
            ERR.put( 0xF504, "EMVERR_DATA_MISSING" );
            ERR.put( 0xF505, "EMVERR_CARD_BLOCK" );
            ERR.put( 0xF506, "EMVERR_NOSUPPORT_CARDAID" );
            ERR.put( 0xF507, "EMVERR_INVALIDPARAMETER" );
            ERR.put( 0xF508, "EMVERR_GENERATEAC_WRONG" );
            ERR.put( 0xF509, "EMVERR_FRAUDCARD_DATA" );
            ERR.put( 0xF50A, "EMVERR_MISS_ADFNAME" );
            ERR.put( 0xF50B, "EMVERR_MISS_APLABEL" );
            ERR.put( 0xF50C, "EMVERR_DATA_EXIST" );
            ERR.put( 0xF50D, "EMVERR_FILE_NOTFOUND" );
            ERR.put( 0xF50E, "EMVERR_RECORD_NOTFOUND" );
            ERR.put( 0xF50F, "EMVERR_DATAPARENT_NULL" );
            ERR.put( 0xF510, "EMVERR_CMD_NOTALLOWED" );
            ERR.put( 0xF511, "EMVERR_APPLICATION_BLOCKED" );
            ERR.put( 0xF512, "EMVERR_SCRIPT_BLOCKED" );
            ERR.put( 0xF513, "EMVERR_KEYVERIFY_ FAILED" );
            ERR.put( 0xF532, "DATA_INSUFFICIENT_BUFFER" );
            ERR.put( 0xF533, "DATA_INVALID_TLV" );
            ERR.put( 0xF534, "DATA_INVALIDE_TYPE" );
            ERR.put( 0xF535, "DATA_INVALID_PROFILE" );
            ERR.put( 0xF536, "DATA_PROFILE_INVALID_TLV" );
            ERR.put( 0xF537, "DATA_FILE_NO_EXIST" );
            ERR.put( 0xF538, "DATA_SYS_GETLASTERROR" );
            ERR.put( 0xF539, "DATA_TAG_ACCESS_REJECT" );
            ERR.put( 0xF53A, "DATA_UNAVAILALBE" );
            ERR.put( 0xF53B, "DATA_NOICS" );
            ERR.put( 0xF53C, "DATA_NOTM" );
            ERR.put( 0xF53D, "DATA_NOKEY" );
            ERR.put( 0xF53E, "DATA_NOAPP" );
            ERR.put( 0xF550, "MEM_ALLOCATE_FAILED" );
            ERR.put( 0xF551, "GET_FUNC_TABLE_FAILED" );
            ERR.put( 0xF552, "FUNC_TABLE_INIT_FAILED" );
            ERR.put( 0xF5F0, "ERROR_INVALID_PARAMETER" );
            ERR.put( 0xF5F1, "ERROR_INVALID_COMMAND_DATA" );
            ERR.put( 0xF5F2, "ERROR_INVALID_CALLBACK_DATA" );
            ERR.put( 0xF5F3, "ERROR_INVALID_EMVDATA_APICODE" );
            ERR.put( 0xF5F4, "ERROR_INVALID_EMVDATA_LENGTH" );
            ERR.put( 0xF5F5, "ERROR_INVALID_EMVDATA_TYPE" );
            ERR.put( 0xF5F6, "ERROR_INVALID_EMVDATA_VALUE" );
            ERR.put( 0xF5F7, "ERROR_INVALID_MAC_VALUE" );
            ERR.put( 0xF5F8, "ERROR_APDU_REQ_TIMEOUT" );
            ERR.put( 0xF5F9, "ERROR_APDU_RSP_TIMEOUT" );
        };
    }

    public static class CMD_ID
    {
        public static final int _B0_SetupPassword = 0xB0;
        public static final int _B1_EmvGetApConfiguration = 0xB1;
        public static final int _B2_EmvSetApConfiguration = 0xB2;
        public static final int _B3_EmvGetMsgCodeTable = 0xB3;
        public static final int _B4_EmvSetMsgCodeTable = 0xB4;

        public static final int _D1_PayGetCandidateNameList = 0xD1;
        public static final int _D2_PaySetCandidateNameList = 0xD2;

        public static final int _20_EmvGetParam = 0x20;
        public static final int _21_EmvSetParam = 0x21;
        public static final int _40_EmvDelProfile = 0x40;
        public static final int _41_EmvGetProfile = 0x41;
        public static final int _42_EmvSetProfile = 0x42;
        public static final int _43_EmvGetFirstProfile = 0x43;
        public static final int _44_EmvGetNextProfile = 0x44;
        public static final int _45_EmvDleAllProfile = 0x45;

        public static final int _01_EmvStart = 0x01;
        public static final int _02_EmvApplicationSelection = 0x02;
        public static final int _03_EmvInitiateApplication = 0x03;
        public static final int _04_EmvReadApplicationData = 0x04;
        public static final int _05_EmvOfflineDataAuthentication = 0x05;
        public static final int _06_EmvProcessingRestrications = 0x06;
        public static final int _07_EmvCardholderVerification = 0x07;
        public static final int _08_EmvTerminalRiskManagement = 0x08;
        public static final int _09_EmvTerminalAnalysis = 0x09;
        public static final int _0A_EmvOnlineProcessing = 0x0A;
        public static final int _0B_EmvCompletion = 0x0B;
        public static final int _0C_EmvDataCaptureType = 0x0C;
        public static final int _0D_EmvStop = 0x0D;

        public static final int _10_CB_SCRSEND = 0x10;
        public static final int _11_CB_SCRRECV = 0x11;
        public static final int _12_NF_DEBUGINFO = 0x12;
        public static final int _13_NF_DISPLAYMSG = 0x13;
        public static final int _14_CB_SELECTAPP = 0x14;
        public static final int _15_CB_TRANAMOUNT = 0x15;
        public static final int _16_CB_EXCEPTION = 0x16;
        public static final int _17_CB_VOICEREFERAL = 0x17;
        public static final int _18_CB_PINENTRY = 0x18;
        public static final int _19_CB_UNKNOWN_TAG = 0x19;

        public static final int _30_EmvGetApiVersion = 0x30;
        public static final int _31_EmvGetKernelVersion = 0x31;
        public static final int _32_EmvSetIcsValue = 0x32;
        public static final int _33_EmvEditTransactionType = 0x33;
        public static final int _34_EmvGetAidUserData = 0x34;
        public static final int _35_EmvGetCurrentCheckSum = 0x35;
        public static final int _36_EmvGetErrorInfo = 0x36;

        public static final int _51_PayStart = 0x51;
        public static final int _52_PayAuth = 0x52;
        public static final int _53_PayFirstGenAC = 0x53;
        public static final int _54_PaySecGenAC = 0x54;
        public static final int _55_PayStop = 0x55;
    }

    public static class CMD_TYPE
    {
        public static final int REQUEST = 'C';
        public static final int RESPONSE = 'R';
        public static final int NOTIFICATION = 'N';
    }

    public static class APP_CONFIG {
        /**
         * Configuration Access control <br/>
         * 0x00 : Disable <br/>
         * 0x01 : Protected by Password - 0xB0 : PaySetupPassword <br/>
         */
        public byte accessControl;

        /**
         * Handling SCR APDU – CB_SCRSEND / CB_SCRRECV<br/>
         * 0x00 : Send APDU directly without callback<br/>
         * 0x01 : Send callback command<br/>
         */
        public byte scrCallback;

        /**
         * Low Byte
         * 0xX0 : Display directly by Message Code Table and send notification <br/>
         * 0xX1 : Display directly and don’t send notification <br/>
         * 0xX2 : Do not display and send notification <br/>
         * High Byte (bit 5-8) <br/>
         * bit 8: Set to Always Send PIN related notification <br/>
         */
        public byte messageDisplayScheme;

        /**
         * Verify MAC as loading profile <br/>
         * 0x00 : Disable <br/>
         * others : encryption key index <br/>
         */
        public byte macVerification;

        /**
         * Algorithm: 1 ASCII byte, the MAC algorithm <br/>
         * '1' = ISO 9797 algorithm 3, ANSI X9.19 <br/>
         * '2' = ISO 9797 algorithm 1 with TDES CBC <br/>
         * Ignore if Disable MAC verification <br/>
         */
        public byte macAlgorithm;

        /**
         * PIN entry handling process configuration <br/>
         * 0x00 : Process by following setting. Send PIN input Notification “EP0” <br/>
         * 0x01 : Send Callback - CB_PINENTRY and return result to EMV kernel <br/>
         * 0x02 : The same with 0x00 but without PIN input Notification “EP0” <br/>
         */
        public byte pinEntryProcess;

        /**
         * EP1 / EP4 <br/>
         * 9 bytes = {PEK#}{PBF}{Min}{Max}{Timeout} <br/>
         * PEK#: PIN encryption key index - ignored as Offline PIN <br/>
         * PBF: PIN Block Format # (0,1,3) - ignored as Offline PIN <br/>
         * Min: Minium acceptable PIN length (04-12) <br/>
         * Max: Maximum acceptable PIN length (04-12) <br/>
         * Timeout = Timeout value in seconds, range 000-255 <br/>
         */
        public byte [] pinEntrySetting;


        /**
         * 0x00 : Config SRED and return encrypted data with following setting <br/>
         * 0x01 : Ignore SRED <br/>
         * Note: <br/>
         * This flag WILL NOT enable or disable SRED. <br/>
         * If AP enable the SRED of Reader, this flag should be set in order to pass the <br/>
         * enciphered 5A / 57 by command QZ3 in EMV transaction command. <br/>
         */
        public byte sredFlag;

        /**
         * QZ1 <br/>
         * 7 bytes = {RC}{LUBC}{TUBC}{Name}{ED}{SC}{MOD10} <br/>
         * RC = 1 ASCII byte, replacing char, default is ‘*’. <br/>
         * (Note. EMV data type ‘cn’ or ‘n’ is masked as F) <br/>
         * LUBC = 1 ASCII byte, leading unmasked PAN byte count, <br/>
         * ‘0’ ~ ‘6’, default is ‘6’ <br/>
         * TUBC = 1 ASCII byte, trailing unmasked PAN byte count, <br/>
         * ‘0’ ~ ‘4’, default is ‘4’ <br/>
         * Name = 1 ASCII byte, card holder name mask enable flag, <br/>
         * ‘0’ = disable (default), ‘1’ = enable <br/>
         * ED = 1 ASCII byte, expiration date mask enable flag, <br/>
         * ‘0’ = disable (default), ‘1’ = enable <br/>
         * SC = 1 ASCII byte, service code mask enable flag, <br/>
         * ‘0’ = disable (default), ‘1’ = enable <br/>
         * MOD10 = 1 ASCII byte, auto-adjust last masking digit of PAN to meet <br/>
         * MOD10 rule if replacing char is ‘0’ ~ ‘9’, <br/>
         * ‘0’ = disable (default), ‘1’ = enable <br/>
         */
        public byte [] sredMaskSetting;

        /**
         * QZ2 <br/>
         * 4 bytes = {TestData#}{Padding}{Hash}{Sentinel} <br/>
         * TestData#: 1 ASCII byte, key index for encrypting account data <br/>
         * Padding: 1 ASCII byte, padding method <br/>
         * ‘0’ = padding tailing zero 0x00 (default) <br/>
         * ‘1’ = padding tailing 0xFF <br/>
         * ‘2’ = padding tailing ‘F’ 0x46 <br/>
         * ‘3’ = padding by ISO 9797#3 method <br/>
         * Hash: 1 ASCII byte hash method (RFU) <br/>
         * Sentinel: 1 ASCII byte sentinel option (RFU) <br/>
         */
        public byte [] sredFormatSetting;

        /**
         * for external PINPAD with display. Set 0 here.
         */
        public byte applicationSelectionProcess;

        /**
         * 2 bytes <br/>
         * for external PINPAD with display. Set all zero here. <br/>
         */
        public byte [] applicationSelectionSetting;

        public byte[] toByteArray() {
            int index = 0;

            byte[] data = new byte[33];

            data[index++] = (byte) 0xDF;
            data[index++] = (byte) 0x40;
            data[index++] = 30;

            data[index++] = accessControl;
            data[index++] = scrCallback;
            data[index++] = messageDisplayScheme;
            data[index++] = macVerification;
            data[index++] = macAlgorithm;
            data[index++] = pinEntryProcess;
            System.arraycopy(pinEntrySetting, 0, data, index, 9);
            index += 9;
            data[index++] = sredFlag;
            System.arraycopy(sredMaskSetting, 0, data, index, 7);
            index += 7;
            System.arraycopy(sredFormatSetting, 0, data, index, 4);
            index += 4;
            data[index++] = applicationSelectionProcess;
            System.arraycopy(applicationSelectionSetting, 0, data, index, 2);
            index += 2;
            return data;
        }

        public APP_CONFIG() {

            accessControl = 0;
            scrCallback = 0;
            messageDisplayScheme = 2;
            macVerification = 0;
            macAlgorithm = 0;
            pinEntryProcess = 0;
            pinEntrySetting = "X00412060".getBytes();
            sredFlag = 1;
            sredMaskSetting = "*640000".getBytes();
            sredFormatSetting = "Y200".getBytes();
            applicationSelectionProcess = 0;
            applicationSelectionSetting = new byte[2];
        }

        public APP_CONFIG(byte [] data) {

            if (data ==  null)
                return;

            int index = 0;

            data = new TLV(data).retrieveTag(0xDF40, true);

            if (data.length != 30) {
                Log.i(this.getClass().toString(), "Invalid App Config Length");
                return;
            }

            accessControl = data[index++];
            scrCallback = data[index++];
            messageDisplayScheme = data[index++];
            macVerification = data[index++];
            macAlgorithm = data[index++];
            pinEntryProcess = data[index++];
            pinEntrySetting = new byte[9];
            System.arraycopy(data, index, pinEntrySetting, 0, 9);
            index += 9;
            sredFlag = data[index++];
            sredMaskSetting = new byte[7];
            System.arraycopy(data, index, sredMaskSetting, 0, 7);
            index += 7;
            sredFormatSetting = new byte[4];
            System.arraycopy(data, index, sredFormatSetting, 0, 4);
            index += 4;
            applicationSelectionProcess = data[index++];
            applicationSelectionSetting = new byte[2];
            System.arraycopy(data, index, applicationSelectionSetting, 0, 2);
            index += 2;
        }

        @Override
        public String toString() {

            return new StringBuilder()
                    .append("{Access Control = ").append(accessControl).append("\r\n")
                    .append("SCR callback = ").append(scrCallback).append("\r\n")
                    .append("Message Display = ").append(messageDisplayScheme).append("\r\n")
                    .append("MAC Verification =").append(macVerification).append("\r\n")
                    .append("PIN Entry Process =").append(pinEntryProcess).append("\r\n")
                    .append("PIN Entry Setting =").append(Utility.bytes2Hex(pinEntrySetting)).append("\r\n")
                    .append("SRED Flag =").append(sredFlag).append("\r\n")
                    .append("SRED Mask Setting =").append(Utility.bytes2Hex(sredMaskSetting)).append("\r\n")
                    .append("SRED Format Setting =").append(Utility.bytes2Hex(sredFormatSetting)).append("\r\n")
                    .append("App Selction Process =").append(applicationSelectionProcess).append("\r\n")
                    .append("App Selction Process Setting =").append(Utility.bytes2Hex(applicationSelectionSetting)).append("\r\n")
                    .append("}").toString();
        }
    }

    // endregion

}
