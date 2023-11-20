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
import java.util.Objects;


public class SignFlowOPHandle extends RequestHandlerBase implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadHandle.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context);//基类方法一定要先执行
        String json = LibCommUtils.getReqBodyJson(request);
        if (json == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401, request missing parameters.");
            return;
        }

        ReqParamBean reqParam = ObjectMapperUtils.fromJson(json, ReqParamBean.class);
        System.out.println("start: " + reqParam.toString());

        String body = "";
        try {
            switch (reqParam.cmd) {
                case "signFlowUrge"://催促
                    String psnAccount = reqParam.cmdParam.getAsJsonPrimitive("psnAccount").getAsString();
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
                case "fileDownloadUrl":
                    FileDownloadEntity files = SignFlowUtils.fileDownloadUrl(reqParam.signFlowId, this.appParam);
                    body = ObjectMapperUtils.toJson(files);
                    break;
                default:
                    body = "";
                    break;
            }
            try (OutputStream out = response.getOutputStream()) {
                out.write((body).getBytes());
                out.flush();
            }
            System.out.println("end: " + reqParam.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sign flow throw error: " + e.getMessage());
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