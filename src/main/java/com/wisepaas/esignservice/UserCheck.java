package com.wisepaas.esignservice;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserCheck implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        RespAppParamBean appParam = RespAppParamBean.fromReq(request);
        if (!ESignUtils.checkAuthKey(appParam)) {
            //返回错误并返回httcode 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
            return;
        }

        String requestPath = (String) request.getAttribute("FC_REQUEST_PATH");
        String requestURI = (String) request.getAttribute("FC_REQUEST_URI");
        String requestClientIP = (String) request.getAttribute("FC_REQUEST_CLIENT_IP");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("header1", "value1");
        response.setHeader("header2", "value2");
        response.setHeader("Content-Type", "text/plain");

        String body = String.format("It's OK \n Path: %s\n Uri: %s\n IP: %s\n", requestPath, requestURI, requestClientIP);
        OutputStream out = response.getOutputStream();
        out.write((body).getBytes());
        out.flush();
        out.close();
    }
}
