package com.wisepaas.esignservice.comm;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description 请求处理基类, 做了通过的header参数处理
 */
public class RequestHandlerBase implements HttpRequestHandler {
    protected RespAppParamBean appParam = null;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RequestHandlerBase.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        this.appParam = RespAppParamBean.fromReq(request);
        if (!LibCommUtils.checkAuthKey(this.appParam)) {
            //返回错误并返回httcode 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access[app code]");
        }

        //先更新LOGGER的等级
        LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
        String logLevel = System.getenv("LOGLEVEL");
        if (logLevel != null) {
            //改全局日志级别；否则按传递的包名或类名修改日志级别。loggerContext.getLogger(packageName)
            Logger logger = loggerContext.getLogger("root");
            if (logger != null) {
                logger.setLevel(ch.qos.logback.classic.Level.toLevel(logLevel));
            }
        }

        LOGGER.debug("@@************************************************@@ \n begin request url: {}, requestparam: {}",
                request.getRequestURL(), this.appParam.toString());

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
    }
}