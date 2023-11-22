package com.wisepaas.esignservice.comm;

import javax.servlet.http.HttpServletRequest;

/**
 * 取得访问esign的核心参数
 */
public class RespAppParamBean {
    private String appID;
    private String appSecret;
    private String esignUrl;
    private String authKey;

    public RespAppParamBean(String appID, String appSecret, String esignUrl, String authKey) {
        this.appID = appID;
        this.appSecret = appSecret;
        this.esignUrl = esignUrl;
        this.authKey = authKey;
    }

    /**
     * Generates a RespAppParamBean object from the given HttpServletRequest.
     *
     * @param request the HttpServletRequest object to generate the RespAppParamBean from
     * @return the generated RespAppParamBean object
     * @throws IllegalArgumentException if the request parameter is null
     */
    public static RespAppParamBean fromReq(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RespAppParamBean fromReq method's param request cannot be null");
        }

        String appID = request.getHeader("appID");
        String appSecret = request.getHeader("appSecret");
        String esignUrl = request.getHeader("esignUrl");
        String authKey = request.getHeader("authKey");

        if (appID == null || appSecret == null || esignUrl == null || authKey == null) {
            throw new IllegalArgumentException("Request Header don't have spacify param, RespAppParam method's param cannot be null.");
        }

        if (!esignUrl.matches("^https://.*") && !esignUrl.startsWith("http")) {
            // 添加https://前缀
            esignUrl = "https://" + esignUrl;
        }

        return new RespAppParamBean(appID, appSecret, esignUrl, authKey);
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getEsignUrl() {
        return esignUrl;
    }

    public void setEsignUrl(String esignUrl) {
        this.esignUrl = esignUrl;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
