package com.wisepaas.esignservice;

import cn.tsign.hz.comm.EsignHttpHelper;
import cn.tsign.hz.comm.EsignHttpResponse;
import cn.tsign.hz.enums.EsignRequestType;
import cn.tsign.hz.exception.EsignOPException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.wisepaas.esignservice.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class SignFlowUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignFlowUtils.class);

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
        psnAccount = null;//为空表示：催签当前轮到签署但还未签署的所有签署人
        String jsonParm = String.format("{\"noticeTypes\":\"%s\",\"urgedOperator\":{\"psnAccount\":\"%s\"}}", 1, psnAccount);
        LOGGER.debug("signFlowUrge: {}", jsonParm);

        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParm, requestType.name(), appParam.getEsignUrl(), apiaddr);
        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header);
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
                jsonParm, requestType.name(), appParam.getEsignUrl(), apiaddr);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header);
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
                jsonParm, requestType.name(), appParam.getEsignUrl(), apiaddr);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header);
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
                jsonParm, requestType.name(), appParam.getEsignUrl(), apiaddr);
        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header);
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
                jsonParm, requestType.name(), appParam.getEsignUrl(), apiaddr);

        //发起接口请求
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<ESignResponse<Object>>() {
        }.getType());
    }

    public static String signFlowQuery(String signFlowId, RespAppParamBean appParam) throws EsignOPException {
        String apiaddr = "/v3/sign-flow/" + signFlowId + "/detail";
        String jsonParm = null;
        EsignRequestType requestType = EsignRequestType.GET;
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(
                appParam.getAppID(),
                appParam.getAppSecret(),
                jsonParm,
                requestType.name(), appParam.getEsignUrl(),
                apiaddr);
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                requestType, jsonParm, header);
        String body = resp.getBody();
        return body;

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
                requestType.name(), appParam.getEsignUrl(),
                apiaddr);
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                requestType, jsonParm, header);
        return ObjectMapperUtils.fromJson(resp.getBody(), new TypeToken<FileDownloadEntity>() {
        }.getType());
    }

    /**
     * 用关键字查到甲乙方的签章位置
     * create-by-file的多方签章是靠一组signFields来处理
     *
     * @param fileId
     * @return
     */
    public static SignFieldPosition[] getPosByKeyword(String fileId, String[] keyword, RespAppParamBean appParam) throws EsignOPException {
        Objects.requireNonNull(keyword);
        String apiaddr = "/v3/files/" + fileId + "/keyword-positions";
        String jsonParam = "{\"keywords\":[" + Arrays.stream(keyword)
                .map(word -> "\"" + word + "\"")
                .collect(Collectors.joining(", ")) + "]}";

        EsignRequestType requestType = EsignRequestType.POST;

        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                jsonParam, requestType.name(), appParam.getEsignUrl(), apiaddr);
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                requestType, jsonParam, header);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start getPosByKeyword jsonParm: {} \n getPosByKeyword resp:{}", jsonParam, resp.getBody());
            //            {
            //                "keywords": [
            //                "甲方盖章/签字",
            //                "乙方盖章/签字"  ]
            //            }
        }
        String retBody = resp.getBody();
        if (retBody == null || retBody.length() < 1)
            throw new EsignOPException("getPosByKeyword don't found anything.");

        PositionResponse retResp = ObjectMapperUtils.fromJson(retBody, PositionResponse.class);
        if (retResp != null) {
            //先判断有没有找到返回值
            if (retResp.getCode() != 0) {
                throw new EsignOPException(String.format(" keyword-positions api error:[%s] %s", retResp.getCode(), retResp.getMessage()));
            }

            PositionResponse.RespData retData = retResp.getData();
            if (retData != null) {
                List<PositionResponse.KeywordPosition> resultKeyPosList = retData.getKeywordPositions();
                if (resultKeyPosList != null) {
                    //返回用的数据结构
                    SignFieldPosition[] returnFieldPosList = new SignFieldPosition[resultKeyPosList.size()];

                    PositionResponse.KeywordPosition resultItem = null;
                    PositionResponse.Position resultPos = null;
                    PositionResponse.Coordinate coordinate = null;
                    List<SignFieldPosition.PosPoint> returnPosList = null;
                    SignFieldPosition.PosPoint retPosPoint = null;

                    //遍历找到的列表，用keyword去SignFieldPosition的返回结果中查找到对应的数组
                    for (int i = 0; i < resultKeyPosList.size(); i++) {
                        resultItem = resultKeyPosList.get(i);

                        returnFieldPosList[i] = new SignFieldPosition();
                        returnFieldPosList[i].setKeyword(resultItem.getKeyword());
                        returnFieldPosList[i].setSearchResult(resultItem.isSearchResult());
                        returnPosList = new ArrayList<SignFieldPosition.PosPoint>();
                        returnFieldPosList[i].setPoslist(returnPosList);

                        for (int j = 0; j < resultItem.getPositions().size(); j++) { //这是支持一个关键词可以在多个页签找到签署处
                            resultPos = resultItem.getPositions().get(j);
                            coordinate = resultPos.getCoordinates().get(resultPos.getCoordinates().size() - 1); //TODO:目前只每页最后一个坐标
                            if (coordinate != null) {
                                retPosPoint = new SignFieldPosition.PosPoint();
                                retPosPoint.setPage(resultPos.getPageNum());
                                retPosPoint.setX(coordinate.getPositionX());
                                retPosPoint.setY(coordinate.getPositionY());

                                returnPosList.add(retPosPoint); //返回posList
                            }
                        }
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("end getPosByKeyword reusult: {} \n ", ObjectMapperUtils.toJson(returnFieldPosList));
                    }
                    return returnFieldPosList;
                }
            }
        }
        return null;
    }
}
