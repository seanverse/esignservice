package com.wisepaas.esignservice.comm;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ESignFlowRequest {
    @SerializedName("docs")
    private List<Document> documents;
    @SerializedName("attachments")
    private List<Document> attachments;
    @SerializedName("signFlowConfig")
    private SignFlowConfig signFlowConfig;
    @SerializedName("signers")
    private List<Signer> signers;

    // getters and setters

    public static ESignFlowRequest fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ESignFlowRequest.class);
    }

    public static ESignFlowRequest fromSignParm(SignParamEntity signParam) {
        Objects.requireNonNull(signParam);

        // 根据SignParamEntity生成ESignFlowRequest
        ESignFlowRequest request = new ESignFlowRequest();
        // 设置docs
        List<ESignFlowRequest.Document> docs = new ArrayList<Document>();
        for (SignParamEntity.Doc doc : signParam.getDocs()) {
            ESignFlowRequest.Document document = new ESignFlowRequest.Document();
            document.setFileId(doc.getFileId());
            document.setFileName(doc.getFileName());
            docs.add(document);
        }
        request.setDocuments(docs);

        if (signParam.getAttachments() != null && signParam.getAttachments().size() > 0) {
            // 设置attachments
            List<ESignFlowRequest.Document> attachments = new ArrayList<ESignFlowRequest.Document>();
            for (SignParamEntity.Doc attachment : signParam.getAttachments()) {
                ESignFlowRequest.Document document = new ESignFlowRequest.Document();
                document.setFileId(attachment.getFileId());
                document.setFileName(attachment.getFileName());
                attachments.add(document);
            }
            request.setAttachments(attachments);
        } else
            request.setAttachments(null);

        // 设置signFlowConfig
        ESignFlowRequest.SignFlowConfig signFlowConfig = new ESignFlowRequest.SignFlowConfig(signParam.getSignFlowTitle());
        signFlowConfig.setSignFlowExpireTime(signParam.getSignFlowExpireTime());
        signFlowConfig.autoFinish = signParam.isAutoFinish();
        signFlowConfig.notifyUrl = signParam.getNotifyUrl();
        signFlowConfig.redirectConfig = new RedirectConfig();
        //signFlowConfig.redirectConfig.setRedirectType(signParam.getRedirectType());
        signFlowConfig.redirectConfig.setRedirectUrl(signParam.getRedirectUrl());
        signFlowConfig.autoStart = signParam.isAutoFinish();//自动结束也自动开始，后续需要再调整传值
        request.setSignFlowConfig(signFlowConfig);

        // 设置signers
        List<ESignFlowRequest.Signer> signers = new ArrayList<ESignFlowRequest.Signer>();
        for (SignParamEntity.Signer signerParam : signParam.getSigners()) {

            ESignFlowRequest.Signer signer = new ESignFlowRequest.Signer();

            // 设置signConfig
            ESignFlowRequest.SignConfig signConfig = new ESignFlowRequest.SignConfig();
            signConfig.setSignOrder(signerParam.getSignOrder());
            signer.setSignConfig(signConfig);

            // 设置noticeConfig
            ESignFlowRequest.NoticeConfig noticeConfig = new ESignFlowRequest.NoticeConfig();
            noticeConfig.setNoticeTypes(signerParam.getNoticeTypes());
            signer.setNoticeConfig(noticeConfig);

            // 设置signerType
            signer.setSignerType(signerParam.getSignerType());

            // 设置psnSignerInfo或orgSignerInfo  签署方类型，0 - 个人，1 - 机构，2 - 法定代表人
            if (signerParam.getSignerType() == 1) {
                //组织机构证件类型，可选值如下：
                // CRED_ORG_USCC - 统一社会信用代码
                // CRED_ORG_REGCODE - 工商注册号
                ESignFlowRequest.OrgInfo orgInfo = new ESignFlowRequest.OrgInfo(signerParam.getOrginfo().getOrgCardId(), "CRED_ORG_USCC");
                ESignFlowRequest.OrgSignerInfo orgSignerInfo = new ESignFlowRequest.OrgSignerInfo(signerParam.getOrginfo().getOrgName(), orgInfo);
                TransactorInfo transactorInfo = new TransactorInfo();
                transactorInfo.psnAccount = signerParam.getPsnAccount();
                ESignFlowRequest.PsnInfo innerPsnInfo = new ESignFlowRequest.PsnInfo(signerParam.getPsnName(), signerParam.getPsnIDCard(), null); //身份证类型让平台自行选择
                transactorInfo.psnInfo = innerPsnInfo;
                orgSignerInfo.setTransactorInfo(transactorInfo);
                signer.setOrgSignerInfo(orgSignerInfo);
            } else { // 0-个人时
                //不管何时类型都会传入psnAccount
                ESignFlowRequest.PsnSignerInfo psnInfo = new ESignFlowRequest.PsnSignerInfo();
                psnInfo.setPsnAccount(signerParam.getPsnAccount());
                ESignFlowRequest.PsnInfo innerPsnInfo = new ESignFlowRequest.PsnInfo(signerParam.getPsnName(), signerParam.getPsnIDCard(), null); //身份证类型让平台自行选择
                psnInfo.setPsnInfo(innerPsnInfo);
                signer.setPsnSignerInfo(psnInfo);
            }

            // 设置signFields
            List<ESignFlowRequest.SignField> signFieldList = new ArrayList<ESignFlowRequest.SignField>();
            //至少有一项
            for (SignParamEntity.SignField fieldParam : signerParam.getSignFields()) {
                ESignFlowRequest.SignField field = new ESignFlowRequest.SignField();
                field.setCustomBizNum(fieldParam.getCustomBizNum());
                field.setFileId(fieldParam.getFileId());

                ESignFlowRequest.NormalSignFieldConfig config = new ESignFlowRequest.NormalSignFieldConfig();
                config.setSignFieldStyle(fieldParam.getSignFieldStyle());
                ESignFlowRequest.SignFieldPosition position = new ESignFlowRequest.SignFieldPosition();
                position.setPositionPage(fieldParam.getPositionPage());
                position.setPositionX(fieldParam.getPositionX());
                position.setPositionY(fieldParam.getPositionY());
                config.setSignFieldPosition(position);
                field.setNormalSignFieldConfig(config);
                //set SignDateConfig
                ESignFlowRequest.SignDateConfig signDateConfig = new ESignFlowRequest.SignDateConfig();
                signDateConfig.setDateFormat("yyyy-MM-dd");
                signDateConfig.setShowSignDate(1);
                if (signerParam.getSignOrder() == 1) { //甲方y位置向下，x 不变
                    signDateConfig.setSignDatePositionX(fieldParam.getPositionX());
                    signDateConfig.setSignDatePositionY(fieldParam.getPositionY() + 96);
                }
                field.setSignDateConfig(signDateConfig);
                signFieldList.add(field);
            }
            signer.setSignFields(signFieldList);
            signers.add(signer);
        }
        request.setSigners(signers);

        return request;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public SignFlowConfig getSignFlowConfig() {
        return signFlowConfig;
    }

    public void setSignFlowConfig(SignFlowConfig signFlowConfig) {
        this.signFlowConfig = signFlowConfig;
    }

    // toJson and fromJson methods using Gson library

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public List<Document> getAttachments() {
        return attachments;
    }

    // Inner classes representing the structure of the JSON

    public void setAttachments(List<Document> attachments) {
        this.attachments = attachments;
    }

    public static class Document {
        @SerializedName("fileId")
        private String fileId;
        @SerializedName("fileName")
        private String fileName;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    public static class SignFlowConfig {
        @SerializedName("signFlowTitle")
        private String signFlowTitle;
        @SerializedName("signFlowExpireTime")
        private long signFlowExpireTime;
        @SerializedName("autoStart")
        private boolean autoStart;
        @SerializedName("autoFinish")
        private boolean autoFinish;
        @SerializedName("notifyUrl")
        private String notifyUrl;
        @SerializedName("redirectConfig")
        private RedirectConfig redirectConfig;

        public SignFlowConfig(String signFlowTitle) {
            this.signFlowTitle = signFlowTitle;
        }

        public String getSignFlowTitle() {
            return signFlowTitle;
        }

        public void setSignFlowTitle(String signFlowTitle) {
            this.signFlowTitle = signFlowTitle;
        }

        public long getSignFlowExpireTime() {
            return signFlowExpireTime;
        }

        public void setSignFlowExpireTime(long signFlowExpireTime) {
            this.signFlowExpireTime = signFlowExpireTime;
        }

        public boolean isAutoStart() {
            return autoStart;
        }

        public void setAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
        }

        public boolean isAutoFinish() {
            return autoFinish;
        }

        public void setAutoFinish(boolean autoFinish) {
            this.autoFinish = autoFinish;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }

        public RedirectConfig getRedirectConfig() {
            return redirectConfig;
        }

        public void setRedirectConfig(RedirectConfig redirectConfig) {
            this.redirectConfig = redirectConfig;
        }
    }

    public static class RedirectConfig {
        @SerializedName("redirectUrl")
        private String redirectUrl;

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }
    }

    public static class Signer {
        @SerializedName("signConfig")
        private SignConfig signConfig;
        @SerializedName("noticeConfig")
        private NoticeConfig noticeConfig;
        @SerializedName("signerType")
        private int signerType;
        @SerializedName("psnSignerInfo")
        private PsnSignerInfo psnSignerInfo;
        @SerializedName("orgSignerInfo")
        private OrgSignerInfo orgSignerInfo;
        @SerializedName("signFields")
        private List<SignField> signFields;

        public SignConfig getSignConfig() {
            return signConfig;
        }

        public void setSignConfig(SignConfig signConfig) {
            this.signConfig = signConfig;
        }

        public NoticeConfig getNoticeConfig() {
            return noticeConfig;
        }

        public void setNoticeConfig(NoticeConfig noticeConfig) {
            this.noticeConfig = noticeConfig;
        }

        public int getSignerType() {
            return signerType;
        }

        public void setSignerType(int signerType) {
            this.signerType = signerType;
        }

        public PsnSignerInfo getPsnSignerInfo() {
            return psnSignerInfo;
        }

        public void setPsnSignerInfo(PsnSignerInfo psnSignerInfo) {
            this.psnSignerInfo = psnSignerInfo;
        }

        public OrgSignerInfo getOrgSignerInfo() {
            return orgSignerInfo;
        }

        public void setOrgSignerInfo(OrgSignerInfo orgSignerInfo) {
            this.orgSignerInfo = orgSignerInfo;
        }

        public List<SignField> getSignFields() {
            return signFields;
        }

        public void setSignFields(List<SignField> signFields) {
            this.signFields = signFields;
        }
    }

    public static class SignConfig {
        @SerializedName("signOrder")
        private int signOrder;

        public int getSignOrder() {
            return signOrder;
        }

        public void setSignOrder(int signOrder) {
            this.signOrder = signOrder;
        }
    }

    public static class NoticeConfig {
        @SerializedName("noticeTypes")
        private String noticeTypes;

        public String getNoticeTypes() {
            return noticeTypes;
        }

        public void setNoticeTypes(String noticeTypes) {
            this.noticeTypes = noticeTypes;
        }
    }

    public static class PsnSignerInfo {
        @SerializedName("psnAccount")
        private String psnAccount;
        @SerializedName("psnInfo")
        private PsnInfo psnInfo;

        public String getPsnAccount() {
            return psnAccount;
        }

        public void setPsnAccount(String psnAccount) {
            this.psnAccount = psnAccount;
        }

        public PsnInfo getPsnInfo() {
            return psnInfo;
        }

        public void setPsnInfo(PsnInfo psnInfo) {
            this.psnInfo = psnInfo;
        }
    }

    public static class PsnInfo {
        private String psnName;
        private String psnIDCardNum;
        private String psnIDCardType;

        public PsnInfo(String psnName, String psnIDCardNum, String psnIDCardType) {
            this.psnName = psnName;
            this.psnIDCardNum = psnIDCardNum;
            this.psnIDCardType = psnIDCardType;
        }

        public String getPsnName() {
            return psnName;
        }

        public void setPsnName(String psnName) {
            this.psnName = psnName;
        }

        public String getPsnIDCardNum() {
            return psnIDCardNum;
        }

        public void setPsnIDCardNum(String psnIDCardNum) {
            this.psnIDCardNum = psnIDCardNum;
        }

        public String getPsnIDCardType() {
            return psnIDCardType;
        }

        public void setPsnIDCardType(String psnIDCardType) {
            this.psnIDCardType = psnIDCardType;
        }
    }

    public static class OrgSignerInfo {
        private String orgName;
        private OrgInfo orgInfo;

        private TransactorInfo transactorInfo;

        public OrgSignerInfo(String orgName, OrgInfo orgInfo) {
            this.orgName = orgName;
            this.orgInfo = orgInfo;
        }

        public TransactorInfo getTransactorInfo() {
            return transactorInfo;
        }

        public void setTransactorInfo(TransactorInfo transactorInfo) {
            this.transactorInfo = transactorInfo;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public OrgInfo getOrgInfo() {
            return orgInfo;
        }

        public void setOrgInfo(OrgInfo orgInfo) {
            this.orgInfo = orgInfo;
        }
    }

    public static class OrgInfo {
        private String orgIDCardNum;
        private String orgIDCardType;

        public OrgInfo(String orgIDCardNum, String orgIDCardType) {
            this.orgIDCardNum = orgIDCardNum;
            this.orgIDCardType = orgIDCardType;
        }

        public String getOrgIDCardNum() {
            return orgIDCardNum;
        }

        public void setOrgIDCardNum(String orgIDCardNum) {
            this.orgIDCardNum = orgIDCardNum;
        }

        public String getOrgIDCardType() {
            return orgIDCardType;
        }

        public void setOrgIDCardType(String orgIDCardType) {
            this.orgIDCardType = orgIDCardType;
        }
    }

    public static class TransactorInfo {
        private String psnAccount;
        private PsnInfo psnInfo;

        public TransactorInfo() {
        }

        public TransactorInfo(String psnAccount, PsnInfo psnInfo) {
            this.psnAccount = psnAccount;
            this.psnInfo = psnInfo;
        }

        public String getPsnAccount() {
            return psnAccount;
        }

        public void setPsnAccount(String psnAccount) {
            this.psnAccount = psnAccount;
        }

        public PsnInfo getPsnInfo() {
            return psnInfo;
        }

        public void setPsnInfo(PsnInfo psnInfo) {
            this.psnInfo = psnInfo;
        }
    }

    public static class SignField {
        @SerializedName("customBizNum")
        private String customBizNum;
        @SerializedName("fileId")
        private String fileId;
        @SerializedName("normalSignFieldConfig")
        private NormalSignFieldConfig normalSignFieldConfig;
        @SerializedName("signDateConfig")
        private SignDateConfig signDateConfig;

        public String getCustomBizNum() {
            return customBizNum;
        }

        public void setCustomBizNum(String customBizNum) {
            this.customBizNum = customBizNum;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public NormalSignFieldConfig getNormalSignFieldConfig() {
            return normalSignFieldConfig;
        }

        public void setNormalSignFieldConfig(NormalSignFieldConfig normalSignFieldConfig) {
            this.normalSignFieldConfig = normalSignFieldConfig;
        }

        public SignDateConfig getSignDateConfig() {
            return signDateConfig;
        }

        public void setSignDateConfig(SignDateConfig signDateConfig) {
            this.signDateConfig = signDateConfig;
        }
    }

    public static class NormalSignFieldConfig {
        @SerializedName("signFieldStyle")
        private int signFieldStyle;
        @SerializedName("signFieldPosition")
        private SignFieldPosition signFieldPosition;

        public int getSignFieldStyle() {
            return signFieldStyle;
        }

        public void setSignFieldStyle(int signFieldStyle) {
            this.signFieldStyle = signFieldStyle;
        }

        public SignFieldPosition getSignFieldPosition() {
            return signFieldPosition;
        }

        public void setSignFieldPosition(SignFieldPosition signFieldPosition) {
            this.signFieldPosition = signFieldPosition;
        }
    }

    public static class SignDateConfig {
        private String dateFormat;
        private int showSignDate;
        private double signDatePositionX;
        private double signDatePositionY;

        public SignDateConfig() {
        }

        public SignDateConfig(String dateFormat, int showSignDate, int signDatePositionX, int signDatePositionY) {
            this.dateFormat = dateFormat;
            this.showSignDate = showSignDate;
            this.signDatePositionX = signDatePositionX;
            this.signDatePositionY = signDatePositionY;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public int getShowSignDate() {
            return showSignDate;
        }

        public void setShowSignDate(int showSignDate) {
            this.showSignDate = showSignDate;
        }

        public double getSignDatePositionX() {
            return signDatePositionX;
        }

        public void setSignDatePositionX(double signDatePositionX) {
            this.signDatePositionX = signDatePositionX;
        }

        public double getSignDatePositionY() {
            return signDatePositionY;
        }

        public void setSignDatePositionY(double signDatePositionY) {
            this.signDatePositionY = signDatePositionY;
        }
    }

    public static class SignFieldPosition {
        @SerializedName("positionPage")
        private int positionPage;
        @SerializedName("positionX")
        private double positionX;
        @SerializedName("positionY")
        private double positionY;

        public int getPositionPage() {
            return positionPage;
        }

        public void setPositionPage(int positionPage) {
            this.positionPage = positionPage;
        }

        public double getPositionX() {
            return positionX;
        }

        public void setPositionX(double positionX) {
            this.positionX = positionX;
        }

        public double getPositionY() {
            return positionY;
        }

        public void setPositionY(double positionY) {
            this.positionY = positionY;
        }
    }
}
