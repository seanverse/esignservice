package com.wisepaas.esignservice;

import cn.tsign.hz.comm.EsignHttpHelper;
import cn.tsign.hz.comm.EsignHttpResponse;
import cn.tsign.hz.enums.EsignRequestType;
import cn.tsign.hz.exception.EsignOPException;
import com.google.gson.reflect.TypeToken;
import com.wisepaas.esignservice.comm.ESignResponse;
import com.wisepaas.esignservice.comm.FileDownloadEntity;
import com.wisepaas.esignservice.comm.ObjectMapperUtils;
import com.wisepaas.esignservice.comm.RespAppParamBean;

import java.math.BigInteger;
import java.util.Map;

public class SignFlowUtils {
    /**
     * 催签流程中签署人
     *
     * @return
     */
    public static ESignResponse<Object> signFlowUrge(String signFlowId, String psnAccount, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/urge";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        // noticeTypes: 通知方式（多种方式使用英文逗号分隔）1- 短信，2 - 邮件 ，默认按照流程设置
        //psnAccount:被催签人账号标识（手机号/邮箱）
        //为空表示：催签当前轮到签署但还未签署的所有签署人
        String jsonParm = """
                {
                    "noticeTypes": "%2$d",
                    "urgedOperator": {
                        "psnAccount": "%1$s"
                    }
                 }
                """;
        jsonParm = String.format(jsonParm, psnAccount, 1);
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    /**
     * 延期签署截止时间
     *
     * @return
     */
    public static ESignResponse<Object> signFlowDelay(String signFlowId, BigInteger delayTime, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/delay";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = String.format("{ \"signFlowExpireTime\": %d }", delayTime);//delayTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    /**
     * 撤销签署流程
     *
     * @return
     */
    public static ESignResponse<Object> signFlowRevoke(String signFlowId, String reason, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/revoke";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{" + "\"revokeReason\": \"" + reason + "\" }";//revokeReason}";
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    /**
     * 完结签署流程
     *
     * @param signFlowId
     * @return
     */
    public static ESignResponse<Object> signFlowFinish(String signFlowId, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/finish";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{}";
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    /**
     * 开启签署流程     *
     *
     * @param signFlowId
     * @return
     */
    public static ESignResponse<Object> signFlowStart(String signFlowId, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/start";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = null;
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    /**
     * Downloads the signed file and associated materials.
     *
     * @param signFlowId The ID of the sign flow.
     * @param appParam   The application parameters.
     * @return The downloaded file entity.
     * @throws EsignOPException If there is an error in the Esign operation.
     */
    public static FileDownloadEntity fileDownloadUrl(String signFlowId, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/file-download-url";
        String jsonParm = null;
        EsignRequestType requestType = EsignRequestType.GET;
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(
                appParam.getAppID(),
                appParam.getAppSecret(),
                jsonParm,
                requestType.name(),
                apiaddr,
                true
        );
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                requestType, jsonParm, header, true);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<FileDownloadEntity>() {
        }.getType());

    }
}
