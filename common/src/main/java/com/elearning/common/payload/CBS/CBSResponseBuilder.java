package com.elearning.common.payload.CBS;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Component
@Slf4j
public class CBSResponseBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmssSSS");

    /**
     * Build success response - perfect copy with only necessary changes
     */
    public CBSResponse buildSuccessResponse(CBSRequest request, JsonNode clientResponseData) {
        log.debug("Building com.elearning.common.payload.CBS success response");

        CBSResponse response = new CBSResponse();

        // Copy header (only change msgReqResTypeCd)
        response.setCommonHdr(copyHeaderWithResponseType(request.getCommonHdr()));

        // Copy ALL body fields + add success status
        response.setCommonBody(copyBodyWithStatus(request.getCommonBody(), "0000", "SUCCESS"));

        // Copy msgData structure exactly + replace dataBody
        response.setMsgData(copyMsgDataWithNewDataBody(request.getMsgData(), clientResponseData));

        return response;
    }

    /**
     * Build success response - perfect copy with only necessary changes
     */
    public CBSResponse buildSuccessResponse(CBSRequest request, Map<String, Object> clientResponseData) {
        log.debug("Building com.elearning.common.payload.CBS success response");

        CBSResponse response = new CBSResponse();

        // Copy header (only change msgReqResTypeCd)
        response.setCommonHdr(copyHeaderWithResponseType(request.getCommonHdr()));

        // Copy ALL body fields + add success status
        response.setCommonBody(copyBodyWithStatus(request.getCommonBody(), "0000", "SUCCESS"));

        // Copy msgData structure exactly + replace dataBody
        response.setMsgData(copyMsgDataWithNewDataBody(request.getMsgData(), clientResponseData));

        return response;
    }

    /**
     * Build error response - perfect copy with error status
     */
    public CBSResponse buildErrorResponse(CBSRequest request, Map<String, Object> errorData, String errorCode) {
        log.debug("Building com.elearning.common.payload.CBS error response with error code: {}", errorCode);

        CBSResponse response = new CBSResponse();

        // Copy header (only change msgReqResTypeCd)
        response.setCommonHdr(copyHeaderWithResponseType(request.getCommonHdr()));

        // Copy ALL body fields + add error status
        response.setCommonBody(copyBodyWithStatus(request.getCommonBody(), errorCode, "ERROR"));

        // Copy msgData structure exactly + replace dataBody
        response.setMsgData(copyMsgDataWithNewDataBody(request.getMsgData(), errorData));

        return response;
    }

    /**
     * Copy header - only change msgReqResTypeCd
     */
    private CBSResponse.CommonHdr copyHeaderWithResponseType(CBSRequest.CommonHdr requestHdr) {
        CBSResponse.CommonHdr responseHdr = new CBSResponse.CommonHdr();

        responseHdr.setEncProcTypeCd(requestHdr.getEncProcTypeCd());
        responseHdr.setGuid(requestHdr.getGuid());
        responseHdr.setMsgReqResTypeCd("RESPONSE");  // Only change
        responseHdr.setMsgVerNo(requestHdr.getMsgVerNo());
        responseHdr.setFirstIPAddr(requestHdr.getFirstIPAddr());
        responseHdr.setFirstReqSysCd(requestHdr.getFirstReqSysCd());
        responseHdr.setChnSysCd(requestHdr.getChnSysCd());
        responseHdr.setMciNodeNo(requestHdr.getMciNodeNo());
        responseHdr.setSessionID(requestHdr.getSessionID());
        responseHdr.setEaiNodeNo(requestHdr.getEaiNodeNo());
        responseHdr.setFepNodeNo(requestHdr.getFepNodeNo());
        responseHdr.setFepSessionID(requestHdr.getFepSessionID());
        responseHdr.setExtReqSysCd(requestHdr.getExtReqSysCd());
        responseHdr.setTotTimeoutSec(requestHdr.getTotTimeoutSec());
        responseHdr.setTrxTimeoutSec(requestHdr.getTrxTimeoutSec());

        return responseHdr;
    }

    /**
     * Copy ALL body fields and set status
     */
    private CBSResponse.CommonBody copyBodyWithStatus(CBSRequest.CommonBody requestBody, String errCd, String msgProcResTypeCd) {
        CBSResponse.CommonBody responseBody = new CBSResponse.CommonBody();

        // Copy ALL fields from request
        responseBody.setCtryCd(requestBody.getCtryCd());
        responseBody.setCompIdCd(requestBody.getCompIdCd());
        responseBody.setSvcID(requestBody.getSvcID());
        responseBody.setProcResRecSvcID(requestBody.getProcResRecSvcID());
        responseBody.setIfID(requestBody.getIfID());
        responseBody.setTrxSyncTypeCd(requestBody.getTrxSyncTypeCd());
        responseBody.setInExTypeCd(requestBody.getInExTypeCd());
        responseBody.setExInstCd(requestBody.getExInstCd());
        responseBody.setSysEnvTypeCd(requestBody.getSysEnvTypeCd());
        responseBody.setTrxProcTypeCd(requestBody.getTrxProcTypeCd());
        responseBody.setOrgTrxRestYN(requestBody.getOrgTrxRestYN());
        responseBody.setOrgTrxScnID(requestBody.getOrgTrxScnID());
        responseBody.setOrgTrxGUID(requestBody.getOrgTrxGUID());
        responseBody.setBankCd(requestBody.getBankCd());
        responseBody.setTrxBrchCd(requestBody.getTrxBrchCd());
        responseBody.setOutBrchTypeCd(requestBody.getOutBrchTypeCd());
        responseBody.setActTrxBrchCd(requestBody.getActTrxBrchCd());
        responseBody.setTrmnInstBrchCd(requestBody.getTrmnInstBrchCd());
        responseBody.setChnTypeCd(requestBody.getChnTypeCd());
        responseBody.setChnDetTypeCd(requestBody.getChnDetTypeCd());
        responseBody.setTrmnTypeCd(requestBody.getTrmnTypeCd());
        responseBody.setTrmnNo(requestBody.getTrmnNo());
        responseBody.setScnID(requestBody.getScnID());
        responseBody.setScnFileNm(requestBody.getScnFileNm());
        responseBody.setScnFileVer(requestBody.getScnFileVer());
        responseBody.setLangTypeCd(requestBody.getLangTypeCd());
        responseBody.setTerminalUniqueNo(requestBody.getTerminalUniqueNo());
        responseBody.setMgrApprProcStatusCd(requestBody.getMgrApprProcStatusCd());
        responseBody.setLastMgrLvl(requestBody.getLastMgrLvl());
        responseBody.setMgrApprSeqNo(requestBody.getMgrApprSeqNo());
        responseBody.setMgrApprTypeCd(requestBody.getMgrApprTypeCd());
        responseBody.setMgrApprReqReason(requestBody.getMgrApprReqReason());
        responseBody.setMgrEmpNoLvl1(requestBody.getMgrEmpNoLvl1());
        responseBody.setDsgtApprChngYNLvl1(requestBody.getDsgtApprChngYNLvl1());
        responseBody.setChngDsgtApprEmpNoLvl1(requestBody.getChngDsgtApprEmpNoLvl1());
        responseBody.setMgrApprReasonLvl1(requestBody.getMgrApprReasonLvl1());
        responseBody.setMgrEmpNoLvl2(requestBody.getMgrEmpNoLvl2());
        responseBody.setDsgtApprChngYNLvl2(requestBody.getDsgtApprChngYNLvl2());
        responseBody.setChngDsgtApprEmpNoLvl2(requestBody.getChngDsgtApprEmpNoLvl2());
        responseBody.setMgrApprReasonLvl2(requestBody.getMgrApprReasonLvl2());
        responseBody.setMgrEmpNoLvl3(requestBody.getMgrEmpNoLvl3());
        responseBody.setDsgtApprChngYNLvl3(requestBody.getDsgtApprChngYNLvl3());
        responseBody.setChngDsgtApprEmpNoLvl3(requestBody.getChngDsgtApprEmpNoLvl3());
        responseBody.setMgrApprReasonLvl3(requestBody.getMgrApprReasonLvl3());
        responseBody.setMgrEmpNoLvl4(requestBody.getMgrEmpNoLvl4());
        responseBody.setDsgtApprChngYNLvl4(requestBody.getDsgtApprChngYNLvl4());
        responseBody.setChngDsgtApprEmpNoLvl4(requestBody.getChngDsgtApprEmpNoLvl4());
        responseBody.setMgrApprReasonLvl4(requestBody.getMgrApprReasonLvl4());
        responseBody.setLastMgrEmpNo(requestBody.getLastMgrEmpNo());
        responseBody.setLastMgrAlternativeApprYN(requestBody.getLastMgrAlternativeApprYN());
        responseBody.setDsgtApprChngYNLast(requestBody.getDsgtApprChngYNLast());
        responseBody.setLastChngDsgtApprEmpNo(requestBody.getLastChngDsgtApprEmpNo());
        responseBody.setLastMgrApprReason(requestBody.getLastMgrApprReason());
        responseBody.setBizDate(requestBody.getBizDate());
        responseBody.setAcctProcYN(requestBody.getAcctProcYN());
        responseBody.setInqUpdTypeCd(requestBody.getInqUpdTypeCd());
        responseBody.setAtmClsTypeCd(requestBody.getAtmClsTypeCd());
        responseBody.setTellerID(requestBody.getTellerID());
        responseBody.setAsstTellerID(requestBody.getAsstTellerID());
        responseBody.setCustIDTypeCd(requestBody.getCustIDTypeCd());
        responseBody.setCustID(requestBody.getCustID());
        responseBody.setPerIDCardImgNo(requestBody.getPerIDCardImgNo());
        responseBody.setAbcTrxDetTypeCd(requestBody.getAbcTrxDetTypeCd());
        responseBody.setCustNm(requestBody.getCustNm());
        responseBody.setCustInfoMaskYN(requestBody.getCustInfoMaskYN());
        responseBody.setTellerCashBalApplyYN(requestBody.getTellerCashBalApplyYN());
        responseBody.setSlipTrxYN(requestBody.getSlipTrxYN());
        responseBody.setSlipNo(requestBody.getSlipNo());
        responseBody.setTellerSlipSeqNo(requestBody.getTellerSlipSeqNo());
        responseBody.setTellerUsdCashAmt(requestBody.getTellerUsdCashAmt());
        responseBody.setTellerUsdOthBankAmt(requestBody.getTellerUsdOthBankAmt());
        responseBody.setTellerUsdAltTrnsAmt(requestBody.getTellerUsdAltTrnsAmt());
        responseBody.setTellerKhrCashAmt(requestBody.getTellerKhrCashAmt());
        responseBody.setTellerKhrOthBankAmt(requestBody.getTellerKhrOthBankAmt());
        responseBody.setTellerKhrAltTrnsAmt(requestBody.getTellerKhrAltTrnsAmt());
        responseBody.setTellerEurCashAmt(requestBody.getTellerEurCashAmt());
        responseBody.setTellerEurOthBankAmt(requestBody.getTellerEurOthBankAmt());
        responseBody.setTellerEurAltTrnsAmt(requestBody.getTellerEurAltTrnsAmt());
        responseBody.setLoginTypeCd(requestBody.getLoginTypeCd());
        responseBody.setProcLoginTypeCd(requestBody.getProcLoginTypeCd());
        responseBody.setTrxDateExtYN(requestBody.getTrxDateExtYN());
        responseBody.setTrxDate(requestBody.getTrxDate());
        responseBody.setTrxTime(requestBody.getTrxTime());
        responseBody.setSysDate(requestBody.getSysDate());
        responseBody.setSysTime(requestBody.getSysTime());
        responseBody.setMsgReqDate(requestBody.getMsgReqDate());
        responseBody.setMsgReqTime(requestBody.getMsgReqTime());
        responseBody.setMsgContYN(requestBody.getMsgContYN());
        responseBody.setOutMsgTypeCd(requestBody.getOutMsgTypeCd());
        responseBody.setMassOutSuspYN(requestBody.getMassOutSuspYN());
        responseBody.setMsgContSeqNo(requestBody.getMsgContSeqNo());
        responseBody.setErrSysCd(requestBody.getErrSysCd());

        // Set response-specific fields
        responseBody.setErrCd(errCd);
        responseBody.setMsgProcResTypeCd(msgProcResTypeCd);

        // Set current date/time
        LocalDateTime now = LocalDateTime.now();
        responseBody.setMsgRespDate(now.format(DATE_FORMAT));
        responseBody.setMsgRespTime(now.format(TIME_FORMAT));

        return responseBody;
    }

    private CBSResponse.MsgData copyMsgDataWithNewDataBody(CBSRequest.MsgData requestMsgData, JsonNode newDataBody) {
        CBSResponse.MsgData responseMsgData = new CBSResponse.MsgData();

        // Copy dataHdr EXACTLY from request (don't change it!)
        if (requestMsgData.getDataHdr() != null) {
            CBSResponse.DataHdr dataHdr = new CBSResponse.DataHdr();
            dataHdr.setDataHdrTypeCd(requestMsgData.getDataHdr().getDataHdrTypeCd());
            responseMsgData.setDataHdr(dataHdr);
        }

        // Copy dataList structure exactly like request
        responseMsgData.setDataList(new ArrayList<>());

        if (requestMsgData.getDataList() != null && !requestMsgData.getDataList().isEmpty()) {
            CBSResponse.DataListItem<JsonNode> responseItem = new CBSResponse.DataListItem<>();

            responseItem.setDataBody(newDataBody);

            responseMsgData.getDataList().add(responseItem);
        }

        return responseMsgData;
    }

    /**
     * Copy msgData exactly from request, only replace dataBody
     */
    private CBSResponse.MsgData copyMsgDataWithNewDataBody(CBSRequest.MsgData requestMsgData, Map<String, Object> newDataBody) {
        CBSResponse.MsgData responseMsgData = new CBSResponse.MsgData();

        // Copy dataHdr EXACTLY from request (don't change it!)
        if (requestMsgData.getDataHdr() != null) {
            CBSResponse.DataHdr dataHdr = new CBSResponse.DataHdr();
            dataHdr.setDataHdrTypeCd(requestMsgData.getDataHdr().getDataHdrTypeCd());
            responseMsgData.setDataHdr(dataHdr);
        }

        // Copy dataList structure exactly like request
        responseMsgData.setDataList(new ArrayList<>());

        if (requestMsgData.getDataList() != null && !requestMsgData.getDataList().isEmpty()) {
            CBSResponse.DataListItem responseItem = new CBSResponse.DataListItem();

            // ONLY set dataBody with client response
            GenericDataBody responseDataBody = new GenericDataBody();
            newDataBody.forEach(responseDataBody::addProperty);
            responseItem.setDataBody(responseDataBody);

            responseMsgData.getDataList().add(responseItem);
        }

        return responseMsgData;
    }

    /**
     * Extract error code from client error response
     */
    public String extractErrorCode(Map<String, Object> errorResponse) {
        try {
            if (errorResponse.containsKey("status")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> status = (Map<String, Object>) errorResponse.get("status");
                if (status.containsKey("code")) {
                    return String.valueOf(status.get("code"));
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract error code from response", e);
        }
        return "9999"; // Default error code
    }
}