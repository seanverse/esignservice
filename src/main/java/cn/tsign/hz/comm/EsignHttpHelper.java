package cn.tsign.hz.comm;

import cn.tsign.hz.enums.EsignHeaderConstant;
import cn.tsign.hz.enums.EsignRequestType;
import cn.tsign.hz.exception.EsignOPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @description Http 请求 辅助类
 * @since JDK1.7
 */
public class EsignHttpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsignHttpHelper.class);

    /**
     * 不允许外部创建实例
     */
    private EsignHttpHelper() {

    }

    /**
     * @param reqType  请求方式
     * @param url      请求路径
     * @param paramStr 请求参数
     * @return
     * @throws EsignOPException
     * @description 发送常规HTTP 请求
     */
    public static EsignHttpResponse doCommHttp(String host, String url, EsignRequestType reqType, Object paramStr, Map<String, String> httpHeader) throws EsignOPException {
        return EsignHttpCfgHelper.sendHttp(reqType, host + url, httpHeader, paramStr);
    }


    /**
     * @param reqType        请求方式
     * @param uploadUrl      请求路径
     * @param param          请求参数
     * @param fileContentMd5 文件fileContentMd5
     * @param contentType    文件MIME类型
     * @return
     * @throws EsignOPException
     * @description 发送文件流上传 HTTP 请求
     */
    public static EsignHttpResponse doUploadHttp(String uploadUrl, EsignRequestType reqType, byte[] param, String fileContentMd5,
                                                 String contentType) throws EsignOPException {
        Map<String, String> uploadHeader = buildUploadHeader(fileContentMd5, contentType);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("----------------------------start------------------------");
            LOGGER.debug("fileContentMd5:{}", fileContentMd5);
            LOGGER.debug("contentType:{}", contentType);
        }

        return EsignHttpCfgHelper.sendHttp(reqType, uploadUrl, uploadHeader, param);
    }


    /**
     * @return
     * @description 构建一个签名鉴权+json数据的esign请求头
     */
    public static Map<String, String> buildSignAndJsonHeader(String projectId, String contentMD5,
                                                             String accept, String contentType, String authMode, String httpMethod, String ESignServer) {

        Map<String, String> header = new HashMap<>();
        header.put("X-Tsign-Open-App-Id", projectId);
        header.put("X-Tsign-Open-Version-Sdk", EsignCoreSdkInfo.getSdkVersion());
        header.put("X-Tsign-Open-Ca-Timestamp", EsignEncryption.timeStamp());
        header.put("Accept", accept);
        header.put("Content-MD5", contentMD5);
        header.put("Content-Type", contentType);
        header.put("X-Tsign-Open-Auth-Mode", authMode);
        //TODO: 阿里云垮域访问需要设置header的信息
        header.put("Access-Control-Allow-Origin", ESignServer);
        header.put("Access-Control-Allow-Methods", httpMethod);
        header.put("Access-Control-Allow-Headers", "Content-Type");
        header.put("Access-Control-Max-Age", "3600");
        return header;
    }

    /**
     * 签名计算并且构建一个签名鉴权+json数据的esign请求头
     *
     * @param httpMethod *         The name of a supported {@linkplain java.nio.charset.Charset
     *                   *         charset}
     * @return
     */
    public static Map<String, String> signAndBuildSignAndJsonHeader(String projectId, String secret, String paramStr,
                                                                    String httpMethod, String ESignServer,
                                                                    String apiUrl) throws EsignOPException {
        String contentMD5 = "";
        //统一转大写处理
        httpMethod = httpMethod.toUpperCase();
        if ("GET".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            paramStr = null;
            contentMD5 = "";
        } else if ("PUT".equals(httpMethod) || "POST".equals(httpMethod)) {
            //对body体做md5摘要
            contentMD5 = EsignEncryption.doContentMD5(paramStr);
        } else {
            throw new EsignOPException(String.format("不支持的请求方法%s", httpMethod));
        }
        //构造一个初步的请求头
        Map<String, String> esignHeaderMap = buildSignAndJsonHeader(projectId, contentMD5,
                EsignHeaderConstant.ACCEPT.VALUE(),
                EsignHeaderConstant.CONTENTTYPE_JSON.VALUE(),
                EsignHeaderConstant.AUTHMODE.VALUE(),
                httpMethod, ESignServer);
        //排序
        apiUrl = EsignEncryption.sortApiUrl(apiUrl);
        //传入生成的bodyMd5,加上其他请求头部信息拼接成字符串
        String message = EsignEncryption.appendSignDataString(httpMethod,
                esignHeaderMap.get("Content-MD5"),
                esignHeaderMap.get("Accept"),
                esignHeaderMap.get("Content-Type"),
                esignHeaderMap.get("Headers"),
                esignHeaderMap.get("Date"), apiUrl);
        //整体做sha256签名
        String reqSignature = EsignEncryption.doSignatureBase64(message, secret);
        //请求头添加签名值
        esignHeaderMap.put("X-Tsign-Open-Ca-Signature", reqSignature);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("----------------------------start------------------------");
            //LOGGER.debug("secret sign message:{}\n", message);
            LOGGER.debug("MD5:{}", contentMD5);
            LOGGER.debug("To be signed value:{} \n signature:{}\n", message, reqSignature);
        }
        return esignHeaderMap;
    }


    /**
     * @return
     * @description 构建一个Token鉴权+jsons数据的esign请求头
     */
    public static Map<String, String> buildTokenAndJsonHeader(String appid, String token) {
        Map<String, String> esignHeader = new HashMap<>();
        esignHeader.put("X-Tsign-Open-Version-Sdk", EsignCoreSdkInfo.getSdkVersion());
        esignHeader.put("Content-Type", EsignHeaderConstant.CONTENTTYPE_JSON.VALUE());
        esignHeader.put("X-Tsign-Open-App-Id", appid);
        esignHeader.put("X-Tsign-Open-Token", token);
        return esignHeader;
    }

    /**
     * @param fileContentMd5
     * @param contentType
     * @return
     * @description 创建文件流上传 请求头
     */
    public static Map<String, String> buildUploadHeader(String fileContentMd5, String contentType) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-MD5", fileContentMd5);
        header.put("Content-Type", contentType);

        return header;
    }

    // ------------------------------私有方法end----------------------------------------------
}
