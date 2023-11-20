package com.wisepaas.esignservice;

import cn.tsign.hz.comm.EsignFileBean;
import cn.tsign.hz.comm.EsignHttpHelper;
import cn.tsign.hz.comm.EsignHttpResponse;
import cn.tsign.hz.enums.EsignHeaderConstant;
import cn.tsign.hz.enums.EsignRequestType;
import cn.tsign.hz.exception.EsignOPException;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import com.google.gson.JsonObject;
import com.wisepaas.esignservice.comm.ESignResponse;
import com.wisepaas.esignservice.comm.LibCommUtils;
import com.wisepaas.esignservice.comm.ObjectMapperUtils;
import com.wisepaas.esignservice.comm.RequestHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class FileUploadHandle extends RequestHandlerBase implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadHandle.class);

    /**
     * 文件流上传
     *
     * @return
     */
    public static EsignHttpResponse uploadFile(String uploadUrl, String filePath) throws EsignOPException {
        //根据文件地址获取文件contentMd5
        EsignFileBean esignFileBean = new EsignFileBean(filePath);
        //请求方法
        EsignRequestType requestType = EsignRequestType.PUT;
        return EsignHttpHelper.doUploadHttp(uploadUrl, requestType, esignFileBean.getFileBytes(), esignFileBean.getFileContentMD5(),
                EsignHeaderConstant.CONTENTTYPE_STREAM.VALUE(), true);
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context);//基类方法一定要先执行
        String fileUrl = request.getHeader("fileurl");
        String fileName = request.getHeader("filename");

        // 获取当前工作目录
        String currentDirectory = System.getProperty("user.dir");
        // 指定文件路径
        String filePath = currentDirectory + java.io.File.separator + fileName;

        LOGGER.info("fileUrl:{0}, fileName:{1}, localPath:{2}", fileUrl, fileName, filePath);
        //先下载文件并写在当前用户目录中
        LibCommUtils.downloadFile(fileUrl, filePath);

        try {
            EsignHttpResponse eResp = this.getUploadUrl(filePath);

            JsonObject eRespObj = ObjectMapperUtils.fromJson(eResp.getBody(), JsonObject.class);
            JsonObject data = eRespObj.getAsJsonObject("data");
            String fileId = data.get("fileId").getAsString();
            String getUploadUrl = data.get("getUploadUrl").getAsString();

            System.out.println(String.format("获取文件id以及文件上传地址成功: \n 文件id:%s, 文件名称: %s \n 上传链接:%s",
                    fileId, fileName, getUploadUrl));

            //文件上传
            EsignHttpResponse uploadResp = FileUploadHandle.uploadFile(getUploadUrl, filePath);
            JsonObject uploadRespObj = ObjectMapperUtils.fromJson(uploadResp.getBody(), JsonObject.class);
            String uploadCode = uploadRespObj.get("errCode").getAsString();
            System.out.println("文件上传成功，状态码:" + uploadCode);

            //文件上传成功后文件会有一个异步处理过程，建议轮询文件状态，正常后发起签署
            //查询文件上传状态
            int i = 0;
            while (i < 3) {
                EsignHttpResponse fileStatus = getFileStatus(fileId);
                JsonObject fileStatusJsonObject = ObjectMapperUtils.fromJson(fileStatus.getBody(), JsonObject.class);
                String status = fileStatusJsonObject.getAsJsonObject("data").get("fileStatus").getAsString();
                System.out.println(String.format("查询文件状态执行第%s次", i + 1));
                if ("2".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)) {//查询状态为2或者5代表文件准备完成
                    System.out.println(String.format("文件[%s]准备完成，状态码:%s (查询状态为2或者5代表文件准备完成)", fileId, status));
                    break;
                }
                System.out.println("文件未准备完成,等待两秒重新查询");
                TimeUnit.SECONDS.sleep(2);
                i++;
            }
            String body = String.format("{\"code\":\"%s\", \"message\":\"%s\", \"data\":{ \"fileId\":\"%s\",\"fileName\":\"%s\"} \n }",
                    uploadCode, "success", fileId, fileName);
            OutputStream out = response.getOutputStream();
            out.write((body).getBytes());
            out.flush();
            out.close();
        } catch (EsignOPException esignOPException) {
            LOGGER.error("文件上传失败", esignOPException);
            ESignResponse<Object> eSignResponse = new ESignResponse<Object>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "文件上传失败: " + esignOPException.getMessage(), null);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, eSignResponse.toJson());

        } catch (InterruptedException e) {
            LOGGER.error("文件上传失败", e);
            ESignResponse<Object> eSignResponse = new ESignResponse<Object>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "文件上传失败: " + e.getMessage(), null);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, eSignResponse.toJson());
        }
    }

    /**
     * 获取文件上传地址
     *
     * @return
     */
    public EsignHttpResponse getUploadUrl(String filePath) throws EsignOPException {
        //自定义的文件封装类，传入文件地址可以获取文件的名称大小,文件流等数据
        EsignFileBean esignFileBean = new EsignFileBean(filePath);
        String apiaddr = "/v3/files/file-upload-url";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = "{\n" +
                "    \"contentMd5\": \"" + esignFileBean.getFileContentMD5() + "\",\n" +
                "    \"fileName\":\"" + esignFileBean.getFileName() + "\"," +
                "    \"fileSize\": " + esignFileBean.getFileSize() + ",\n" +
                "    \"convertToPDF\": false,\n" +
                "    \"contentType\": \"" + EsignHeaderConstant.CONTENTTYPE_STREAM.VALUE() + "\"\n" +
                "}";
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(this.appParam.getAppID(), this.appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(this.appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
    }

    /**
     * 获取文件上传状态
     */
    public EsignHttpResponse getFileStatus(String fileId) throws EsignOPException {
        String apiaddr = "/v3/files/" + fileId;

        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = null;
        //请求方法
        EsignRequestType requestType = EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(this.appParam.getAppID(), this.appParam.getAppSecret(),
                jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(this.appParam.getEsignUrl(), apiaddr, requestType, jsonParm, header, true);
    }

}
