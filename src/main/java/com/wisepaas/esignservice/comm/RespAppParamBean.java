package com.wisepaas.esignservice.comm;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 取得访问esign的核心参数
 */
public class RespAppParamBean {
    private String appID;
    private String appSecret;
    private String esignUrl;
    private String authKey;

    private boolean isDevStruct;

    public RespAppParamBean(String appID, String appSecret, String esignUrl, String authKey, boolean isDevStruct) {
        this.appID = appID;
        this.appSecret = appSecret;
        this.esignUrl = esignUrl;
        this.authKey = authKey;
        this.isDevStruct = isDevStruct;
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
        String devTag = request.getHeader("structTag");

        if (appID == null || appSecret == null || esignUrl == null || authKey == null) {
            throw new IllegalArgumentException("Request Header don't have spacify param, RespAppParam method's param cannot be null.");
        }

        boolean isDevStruct = false;
        if (Objects.nonNull(devTag)) {
            isDevStruct = devTag.compareToIgnoreCase("true") == 0;
        }

        if (!esignUrl.matches("^https://.*") && !esignUrl.startsWith("http")) {
            // 添加https://前缀
            esignUrl = "https://" + esignUrl;
        }

        return new RespAppParamBean(appID, appSecret, esignUrl, authKey, isDevStruct);
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

    public boolean isDevStruct() {
        return isDevStruct;
    }

    @Override
    public String toString() {
        return "RespAppParamBean{" +
                "appID='" + appID + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", esignUrl='" + esignUrl + '\'' +
                ", authKey='" + authKey + '\'' +
                ", isDevStruct=" + isDevStruct +
                '}';
    }
}
