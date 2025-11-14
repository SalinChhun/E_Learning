package com.elearning.common.payload.CBS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * com.elearning.common.payload.CBS Response - Exact same structure as com.elearning.common.payload.CBS Request
 * Only differences: msgReqResTypeCd, errCd, msgProcResTypeCd, msgRespDate, msgRespTime, dataBody
 */
@Getter
@Setter
public class CBSResponse {

    @JsonProperty("commonHdr")
    private CommonHdr commonHdr;

    @JsonProperty("commonBody")
    private CommonBody commonBody;

    @JsonProperty("msgData")
    private MsgData msgData;

    // Inner classes - same structure as CBSRequest
    @Getter
    @Setter
    public static class CommonHdr {
        private String encProcTypeCd;
        private String guid;
        private String msgReqResTypeCd;
        private String msgVerNo;
        private String firstIPAddr;
        private String firstReqSysCd;
        private String chnSysCd;
        private String mciNodeNo;
        private String sessionID;
        private String eaiNodeNo;
        private String fepNodeNo;
        private String fepSessionID;
        private String extReqSysCd;
        private String totTimeoutSec;
        private String trxTimeoutSec;
    }

    @Getter
    @Setter
    public static class CommonBody {
        // ALL fields from CBSRequest.CommonBody
        private String ctryCd;
        private String compIdCd;
        private String svcID;
        private String procResRecSvcID;
        private String ifID;
        private String trxSyncTypeCd;
        private String inExTypeCd;
        private String exInstCd;
        private String sysEnvTypeCd;
        private String trxProcTypeCd;
        private String orgTrxRestYN;
        private String orgTrxScnID;
        private String orgTrxGUID;
        private String bankCd;
        private String trxBrchCd;
        private String outBrchTypeCd;
        private String actTrxBrchCd;
        private String trmnInstBrchCd;
        private String chnTypeCd;
        private String chnDetTypeCd;
        private String trmnTypeCd;
        private String trmnNo;
        private String scnID;
        private String scnFileNm;
        private String scnFileVer;
        private String langTypeCd;
        private String terminalUniqueNo;
        private String mgrApprProcStatusCd;
        private String lastMgrLvl;
        private int mgrApprSeqNo;
        private String mgrApprTypeCd;
        private String mgrApprReqReason;
        private String mgrEmpNoLvl1;
        private String dsgtApprChngYNLvl1;
        private String chngDsgtApprEmpNoLvl1;
        private String mgrApprReasonLvl1;
        private String mgrEmpNoLvl2;
        private String dsgtApprChngYNLvl2;
        private String chngDsgtApprEmpNoLvl2;
        private String mgrApprReasonLvl2;
        private String mgrEmpNoLvl3;
        private String dsgtApprChngYNLvl3;
        private String chngDsgtApprEmpNoLvl3;
        private String mgrApprReasonLvl3;
        private String mgrEmpNoLvl4;
        private String dsgtApprChngYNLvl4;
        private String chngDsgtApprEmpNoLvl4;
        private String mgrApprReasonLvl4;
        private String lastMgrEmpNo;
        private String lastMgrAlternativeApprYN;
        private String dsgtApprChngYNLast;
        private String lastChngDsgtApprEmpNo;
        private String lastMgrApprReason;
        private String bizDate;
        private String acctProcYN;
        private String inqUpdTypeCd;
        private String atmClsTypeCd;
        private String tellerID;
        private String asstTellerID;
        private String custIDTypeCd;
        private String custID;
        private String perIDCardImgNo;
        private String abcTrxDetTypeCd;
        private String custNm;
        private String custInfoMaskYN;
        private String tellerCashBalApplyYN;
        private String slipTrxYN;
        private String slipNo;
        private int tellerSlipSeqNo;
        private double tellerUsdCashAmt;
        private double tellerUsdOthBankAmt;
        private double tellerUsdAltTrnsAmt;
        private double tellerKhrCashAmt;
        private double tellerKhrOthBankAmt;
        private double tellerKhrAltTrnsAmt;
        private double tellerEurCashAmt;
        private double tellerEurOthBankAmt;
        private double tellerEurAltTrnsAmt;
        private String loginTypeCd;
        private String procLoginTypeCd;
        private String trxDateExtYN;
        private String trxDate;
        private String trxTime;
        private String sysDate;
        private String sysTime;
        private String msgReqDate;
        private String msgReqTime;
        private String msgContYN;
        private String msgRespDate;
        private String msgRespTime;
        private String msgProcResTypeCd;
        private String outMsgTypeCd;
        private String massOutSuspYN;
        private String msgContSeqNo;
        private String errCd;
        private String errSysCd;
    }

    @Getter
    @Setter
    public static class MsgData {
        @JsonProperty("dataHdr")
        private DataHdr dataHdr;

        @JsonProperty("dataList")
        private List<DataListItem> dataList;
    }

    @Getter
    @Setter
    public static class DataHdr {
        private String dataHdrTypeCd;
    }

    @Getter
    @Setter
    public static class DataListItem<T> {
        @JsonProperty("dataBody")
        private T dataBody;
        // NO dataAppdHdr - just like request structure
    }
}