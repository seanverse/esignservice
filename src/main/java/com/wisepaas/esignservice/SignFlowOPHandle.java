package com.wisepaas.esignservice;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import com.google.gson.JsonObject;
import com.wisepaas.esignservice.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class SignFlowOPHandle extends RequestHandlerBase implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignFlowOPHandle.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context); //基类方法一定要先执行

        String json = LibCommUtils.getReqBodyJson(request);
        if (json == null) {
            ESignResponse<Object> eSignResponse = new ESignResponse<Object>(500, "401, request missing parameters.", null);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, eSignResponse.toJson());
            return;
        }

        //{"signFlowId": "34234-342288", "cmd": "signFlowUrge", "cmdParam": {"psnAccount": "xxx"}}
        ReqParamBean reqParam = ObjectMapperUtils.fromJson(json, ReqParamBean.class);
        LOGGER.info("start: {}", ObjectMapperUtils.toJson(reqParam));

        if (this.appParam.isDevStruct()) { //配合返回demo数据以支持对接的平台取得返回结构
            String body = String.format("{\"code\":\"%s\", \"message\":\"%s\", \"data\": {} \n }",
                    "0", "成功");
            try (OutputStream out = response.getOutputStream()) {
                out.write((body).getBytes("UTF-8"));
                out.flush();
            }
            return;
        }


        String body = "";
        try {
            switch (reqParam.cmd) {
                case "signFlowUrge"://催促
                    String psnAccount = reqParam.cmdParam.getAsJsonPrimitive("psnAccount").getAsString();
                    if (psnAccount.startsWith("+86")) {
                        psnAccount = psnAccount.substring(3);
                    }
                    ESignResponse<Object> retUrge = SignFlowUtils.signFlowUrge(reqParam.signFlowId, psnAccount, this.appParam);
                    body = ObjectMapperUtils.toJson(retUrge);
                    break;
                case "signFlowDelay": //延期
                    BigInteger expireTime = reqParam.cmdParam.getAsJsonPrimitive("signFlowExpireTime").getAsBigInteger();
                    ESignResponse<Object> retDelay = SignFlowUtils.signFlowDelay(reqParam.signFlowId, expireTime, this.appParam);
                    body = ObjectMapperUtils.toJson(retDelay);
                    break;
                case "signFlowRevoke": //撤销
                    String reason = reqParam.cmdParam.getAsJsonPrimitive("revokeReason").getAsString();
                    ESignResponse<Object> retRevoke = SignFlowUtils.signFlowRevoke(reqParam.signFlowId, reason, this.appParam);
                    body = ObjectMapperUtils.toJson(retRevoke);
                    break;
                case "signFlowStart"://开启
                    ESignResponse<Object> flowStart = SignFlowUtils.signFlowStart(reqParam.signFlowId, this.appParam);
                    body = ObjectMapperUtils.toJson(flowStart);
                    break;
                case "signFlowFinish": //结束
                    ESignResponse<Object> flowEnd = SignFlowUtils.signFlowFinish(reqParam.signFlowId, this.appParam);
                    body = ObjectMapperUtils.toJson(flowEnd);
                    break;
                case "signFlowQuery": //查询状态
                    body = SignFlowUtils.signFlowQuery(reqParam.signFlowId, this.appParam);
                    break;
                case "fileDownloadUrl":
                    FileDownloadEntity files = SignFlowUtils.fileDownloadUrl(reqParam.signFlowId, this.appParam);
                    body = ObjectMapperUtils.toJson(files);
                    break;
                default:
                    body = String.format("{\"code\":\"%s\", \"message\":\"%s\", \"data\": null \n }",
                            "501", "flowop cmd not found.");
                    break;
            }
            LOGGER.info("end: {}", body);
            try (OutputStream out = response.getOutputStream()) {
                out.write((body).getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.error("sign flow operate throw error: ", e);
            ESignResponse<Object> eSignResponse = new ESignResponse<Object>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Sign flow operate throw error: " + e.getMessage(), null);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, eSignResponse.toJson());
            return;
        }
    }

    public static class ReqParamBean {
        public String signFlowId;
        public String cmd;

        public JsonObject cmdParam;

        @Override
        public String toString() {

            return String.format("flowId: %s, cmd: %s", this.signFlowId, this.cmd, this.cmdParam);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ReqParamBean other = (ReqParamBean) obj;
            return Objects.equals(this.signFlowId, other.signFlowId) &&
                   Objects.equals(this.cmd, other.cmd) && Objects.equals(this.cmdParam, other.cmdParam);
        }

        @Override
        public int hashCode() {
            return Objects.hash(signFlowId, cmd, cmdParam);
        }
    }
}