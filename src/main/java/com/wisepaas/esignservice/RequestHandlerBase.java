package com.wisepaas.esignservice;

import cn.tsign.hz.exception.EsignOPException;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description  请求处理基类,做了通过的header参数处理
 */
public class RequestHandlerBase implements HttpRequestHandler {
    protected RespAppParamBean appParam = null;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        this.appParam = RespAppParamBean.fromReq(request);
        if (ESignUtils.checkAuthKey(this.appParam))
        {
            //返回错误并返回httcode 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
            return;
        }
    }
}