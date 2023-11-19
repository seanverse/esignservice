package com.wisepaas.esignservice;

public class ESignUtils {
    private ESignUtils() {
    }

    public static boolean checkAuthKey(RespAppParamBean appParam) {
        return appParam.getAuthKey() != null && !appParam.getAuthKey().isEmpty()
                && System.getenv("app_authorize") != null
                && System.getenv("app_authorize").equals(appParam.getAuthKey());
    }
}

