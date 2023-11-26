package com.wisepaas.esignservice;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import com.wisepaas.esignservice.comm.RequestHandlerBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class UserCheckHandle extends RequestHandlerBase implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context);

        String requestPath = (String) request.getAttribute("FC_REQUEST_PATH");
        String requestURI = (String) request.getAttribute("FC_REQUEST_URI");
        String requestClientIP = (String) request.getAttribute("FC_REQUEST_CLIENT_IP");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("header1", "value1");
        response.setHeader("header2", "value2");
        response.setContentType("text/plain");

        String body = String.format("It's OK. 网络是通的。 \n Path: %s\n Uri: %s\n IP: %s\n", requestPath, requestURI, requestClientIP);
        try (PrintWriter out = response.getWriter()) {
            out.write(body);
        }
    }
}
