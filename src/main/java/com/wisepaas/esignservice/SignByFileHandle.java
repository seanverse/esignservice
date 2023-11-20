package com.wisepaas.esignservice;

import cn.tsign.hz.comm.EsignHttpHelper;
import cn.tsign.hz.comm.EsignHttpResponse;
import cn.tsign.hz.enums.EsignRequestType;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import com.wisepaas.esignservice.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SignByFileHandle extends RequestHandlerBase implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignByFileHandle.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context);//基类方法一定要先执行

        String apiaddr = "/v3/sign-flow/create-by-file";
        EsignRequestType requestType = EsignRequestType.POST;

        String json = LibCommUtils.getReqBodyJson(request);
        if (json == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401, request missing parameters.");
            return;
        }

        SignParamEntity signParam = ObjectMapperUtils.fromJson(json, SignParamEntity.class);
        ESignFlowRequest reqData = ESignFlowRequest.fromSignParm(signParam);

        String jsonParam = ObjectMapperUtils.toJson(reqData);
        try {
            Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                    jsonParam, requestType.name(), apiaddr, true);
            EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                    requestType, jsonParam, header, true);

            /*if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("start create-by-file jsonParm: {0} \n resp:{1}", jsonParam, resp.getBody());
            }

            ESignResponse<SignByFileHandle.FlowIdBean> retResp = ObjectMapperUtils.fromJson(resp.getBody(),
                    new TypeToken<ESignResponse<SignByFileHandle.FlowIdBean>>() {
            }.getType());
            */

            try (OutputStream out = response.getOutputStream()) {
                out.write((resp.getBody()).getBytes());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sign flow throw error: " + e.getMessage());
            return;
        }

    }

    public static class FlowIdBean {
        private String signFlowId;

        public String getFlowId() {
            return signFlowId;
        }

        public void setFlowId(String flowId) {
            this.signFlowId = flowId;
        }
    }
}
