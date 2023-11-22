package com.wisepaas.esignservice.comm;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

/**
 * @description 请求处理基类, 做了通过的header参数处理
 */
public class RequestHandlerBase implements HttpRequestHandler {
    protected RespAppParamBean appParam = null;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {

        //先更新LOGGER的等级
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.putProperty("LOG_LEVEL", System.getenv("LOG_LEVEL"));

        this.appParam = RespAppParamBean.fromReq(request);
        if (LibCommUtils.checkAuthKey(this.appParam)) {
            //返回错误并返回httcode 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
            return;
        }
    }
}