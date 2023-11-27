package com.wisepaas.esignservice;

import cn.tsign.hz.comm.EsignHttpHelper;
import cn.tsign.hz.comm.EsignHttpResponse;
import cn.tsign.hz.enums.EsignRequestType;
import cn.tsign.hz.exception.EsignOPException;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.HttpRequestHandler;
import com.google.gson.JsonObject;
import com.wisepaas.esignservice.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SignByFileHandle extends RequestHandlerBase implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignByFileHandle.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        super.handleRequest(request, response, context);//基类方法一定要先执行

        String apiaddr = "/v3/sign-flow/create-by-file";
        EsignRequestType requestType = EsignRequestType.POST;


        String json = LibCommUtils.getReqBodyJson(request);
        LOGGER.debug("start create-by-file jsonParm: {}", json);

        if (json == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "400, request missing parameters.");
            return;
        }

        if (this.appParam.isDevStruct()) { //配合返回demo数据以支持对接的平台取得返回结构
            String body = String.format("{\"code\":\"%s\", \"message\":\"%s\", \"data\":{ \"signFlowId\":\"%s\"} \n }",
                    "200", "success", "flow8045830304");

            try (OutputStream out = response.getOutputStream()) {
                out.write((body).getBytes("UTF-8"));
                out.flush();
            }
            LOGGER.debug("end. dev return api body: {}", body);
            return;
        }

        SignParamEntity signParam = ObjectMapperUtils.fromJson(json, SignParamEntity.class);
        LOGGER.debug("@@start fixeSignPosByRemote: {}", signParam.toJson());
        //前端一般不传入正确的签署区的位置，这里再来调用ESign的API来计算签署区位置
        this.fixeSignPosByRemote(signParam);
        LOGGER.debug("@@end fixeSignPosByRemote: {}", signParam.toJson());
        /*--------------------*/

        ESignFlowRequest reqData = ESignFlowRequest.fromSignParm(signParam);

        String jsonParam = ObjectMapperUtils.toJson(reqData);
        try {
            Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appParam.getAppID(), appParam.getAppSecret(),
                    jsonParam, requestType.name(), appParam.getEsignUrl(), apiaddr);
            EsignHttpResponse resp = EsignHttpHelper.doCommHttp(appParam.getEsignUrl(), apiaddr,
                    requestType, jsonParam, header);
            //{
            //    "code":0,
            //    "message":"成功",
            //    "data":{
            //        "signFlowId":"165467****000"
            //    }
            //}
            String body = resp.getBody();
            LOGGER.debug(body);
            try (OutputStream out = response.getOutputStream()) {
                out.write(body.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("signFlowId", "");
            ESignResponse<Object> eSignResponse = new ESignResponse<Object>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Sign flow throw error: " + e.getMessage(), jsonObject);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, eSignResponse.toJson());
            return;
        }
    }

    /**
     * Fixes the sign position by remote for the given sign parameter entity.
     * 如果同一个文件内的一个关键词找到多个位置，要转换为多个SignParamEntity.SignField，以使得一份文件中多处盖章的效果
     *
     * @param signParam the sign parameter entity
     * @throws EsignOPException if an error occurs during the operation
     */
    public void fixeSignPosByRemote(SignParamEntity signParam) throws RuntimeException {
        Objects.requireNonNull(signParam);
        Map<String, List<SignParamEntity.SignField>> keywordMap = new HashMap<>();

        // 先做fileId的归集
        for (SignParamEntity.Signer signer : signParam.getSigners()) {
            //修正传入的手机号
            String psnAccount = signer.getPsnAccount();
            if (psnAccount != null) {
                if (psnAccount.startsWith("+86")) {
                    signer.setPsnAccount(psnAccount.substring(3));
                }
            }

            signer.getSignFields().forEach(signField -> {
                String fileId = signField.getFileId();
                keywordMap.computeIfAbsent(fileId, k -> new ArrayList<>()).add(signField);
            });
        }

        //按每个文件Id来分批调用，一般就一个文件。
        keywordMap.forEach((fileId, signFields) -> {
            List<String> keywordList = signFields.stream()
                    .map(SignParamEntity.SignField::getKeyword)
                    .collect(Collectors.toList());
            try {
                //todo: warring:这里是远程调用
                SignFieldPosition[] resultPosList = SignFlowUtils.getPosByKeyword(fileId, keywordList.toArray(new String[0]), appParam);
                if (resultPosList == null) return;
                //LOGGER.debug("！！！ start resultPosList={}", ObjectMapperUtils.toJson(resultPosList));

                HashMap<SignParamEntity.Signer, List<SignParamEntity.SignField>> signerFieldMap = new HashMap<>();

                for (SignFieldPosition resultSignFieldPosition : resultPosList) {
                    for (SignParamEntity.Signer signer : signParam.getSigners()) {
                        //把每个位置信息在原数据中找到相应的项目修改回原数据中,由于List stream不能修改对象，所以会用一个全新List来承接，最后再一次设回。
                        List<SignParamEntity.SignField> additionalList = new ArrayList<>();

                        for (SignParamEntity.SignField originSignField : signer.getSignFields()) {
                            if (originSignField.getKeyword().compareToIgnoreCase(resultSignFieldPosition.getKeyword()) == 0) {
                                //找到关键词相等的则更新它的位置
                                List<SignFieldPosition.PosPoint> resultPostList = resultSignFieldPosition.getPoslist();
                                if (resultPostList != null && resultPostList.size() > 0) {
                                    originSignField.setPositionPage(resultPostList.get(0).getPage());
                                    originSignField.setPositionX(resultPostList.get(0).getX());
                                    originSignField.setPositionY(resultPostList.get(0).getY());
                                    //如果同一个文件内的一个关键词找到多个位置，要转换为多个SignParamEntity.SignField，以使得一份文件中多处盖章的效果
                                    //如果是多于一个位置，则再创建新的SignField加进去，注意对象list的引用要正确
                                    if (resultPostList.size() > 1) {
                                        for (int i = 1; i < resultPostList.size(); i++) {
                                            SignParamEntity.SignField additionalField = new SignParamEntity.SignField(originSignField);
                                            additionalField.setPositionPage(resultPostList.get(i).getPage());
                                            additionalField.setPositionX(resultPostList.get(i).getX());
                                            additionalField.setPositionY(resultPostList.get(i).getY());
                                            //Note: 加回原对象的集合引用类才会有效,但是原集合在stream处理中不能进行修改
                                            //signer.getSignFields().add(additionalField);
                                            additionalList.add(additionalField);
                                        }
                                    }
                                }
                            }
                        }

                        if (additionalList.size() > 0) {
                            signerFieldMap.computeIfAbsent(signer, k -> new ArrayList<>()).addAll(additionalList);
                            // LOGGER.debug("！！！ add additionalList: {}", ObjectMapperUtils.toJson(additionalList));
                            additionalList = null; //上面会重新构建

                        }

                    } //end for signer

                } //enf for SignFieldPosition
                //都foreach完成了才能进行additionalList的增加，这样不会影响for循环
                if (signerFieldMap.size() > 0) {
                    signerFieldMap.forEach((k, v) -> k.getSignFields().addAll(v));
                }

                //LOGGER.debug("！！！ end signParam={}", ObjectMapperUtils.toJson(signParam));

                //todo:因为远程调用，所以调用一次后要Sleep一下吗？Sleep多久合适？
                if (keywordMap.size() > 1) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (EsignOPException e) {
                throw new RuntimeException(e);
            }
        }); //end keywordMap foreach
    }
}
