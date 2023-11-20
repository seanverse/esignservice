package com.wisepaas.esignservice.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;

public class LibCommUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibCommUtils.class);

    private LibCommUtils() {
    }

    public static boolean checkAuthKey(RespAppParamBean appParam) {
        return appParam.getAuthKey() != null && !appParam.getAuthKey().isEmpty()
                && System.getenv("app_authorize") != null
                && System.getenv("app_authorize").equals(appParam.getAuthKey());
    }

    public static void downloadFile(String url, String filePath) throws IOException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("开始下载文件:{}", url);
        URL fileUrl = new URL(url);

        try (InputStream in = new BufferedInputStream(fileUrl.openStream());
             FileOutputStream out = new FileOutputStream(filePath)) {
            // 从远程文件的输入流中读取数据，并写入到本地文件的输出流中
            byte[] buffer = new byte[4096];
            int bytesRead = 0, totalBytes = 0;
            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytes += bytesRead;
                out.write(buffer, 0, bytesRead);
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("下载文件成功:{0}bits - {1}", totalBytes, filePath);
        }
    }

    public static String getReqBodyJson(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        InputStream inputStream = request.getInputStream();

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
        }

        return requestBody.toString();
    }
}

