package com.wisepaas.esignservice.comm;


import java.util.List;

/**
 * 签署流程参数
 * <p>
 * ```json
 * {
 * "docs": [
 * {
 * "fileId": "55607bb*****702b5f92ed565",
 * "fileName": "xx企业劳动合同.pdf"
 * }
 * ],
 * "attachments": [
 * {
 * "fileId": "55607bb*****702b5f92ed565",
 * "fileName": "xx企业劳动合同附件.pdf"
 * }
 * ],
 * "signFlowTitle": "企业员工劳动合同签署",
 * "signFlowExpireTime": 169111118000,
 * "autoFinish": true,
 * "notifyUrl": "http://xxx/asyn/notify",
 * "redirectUrl": "http://www.xx.cn/",
 * "signers": [
 * {
 * "signOrder": 1,
 * "signerType": 0,
 * "noticeTypes": "1",
 * "psnAccount": "15****50",
 * "psnName": "张三",
 * "psnIDCard": "410xxxxx",
 * "orginfo": {
 * "orgCardId": "21545xxxxxxx4bd6b6d9694181668e1c",
 * "orgName": "xx公司"
 * },
 * "signFields": [
 * {
 * "customBizNum": "自定义编码001",
 * "fileId": "55607bb5*******ed565",
 * "signFieldStyle": 1,
 * "positionPage": "1",
 * "positionX": 200,
 * "positionY": 200
 * }
 * ]
 * },
 * {
 * "signOrder": 2,
 * "signerType": 1,
 * "noticeTypes": "1",
 * "psnAccount": "13****50",
 * "psnName": "李四",
 * "orginfo": {
 * "orgCardId": "21545xxxxxxx4bd6b6d9694181668e1c",
 * "orgName": "xx公司"
 * },
 * "signFields": [
 * {
 * "customBizNum": "自定义编码002",
 * "fileId": "67807bb5*******ed565",
 * "signFieldStyle": 1,
 * "positionPage": "1",
 * "positionX": 200,
 * "positionY": 200
 * }
 * ]
 * }
 * ]
 * }
 * ```
 */
public class SignParamEntity {
    private List<Doc> docs;
    private List<Doc> attachments;
    private String signFlowTitle;
    private long signFlowExpireTime;
    private boolean autoFinish;
    private String notifyUrl;
    private String redirectUrl;
    private List<Signer> signers;

    public List<Doc> getDocs() {
        return docs;
    }

    public String getSignFlowTitle() {
        return signFlowTitle;
    }

    public long getSignFlowExpireTime() {
        return signFlowExpireTime;
    }

    public boolean isAutoFinish() {
        return autoFinish;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public List<Doc> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Doc> attachments) {
        this.attachments = attachments;
    }

    public static class Doc {
        private String fileId;
        private String fileName;

        public String getFileId() {
            return fileId;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class Signer {
        private int signOrder;
        private int signerType;
        private String noticeTypes;
        private String psnAccount;
        private String psnName;
        private String psnIDCard;
        private OrgInfo orginfo;
        private List<SignField> signFields;

        public int getSignOrder() {
            return signOrder;
        }

        public int getSignerType() {
            return signerType;
        }

        public String getNoticeTypes() {
            return noticeTypes;
        }

        public String getPsnAccount() {
            return psnAccount;
        }

        public String getPsnName() {
            return psnName;
        }

        public OrgInfo getOrginfo() {
            return orginfo;
        }

        public List<SignField> getSignFields() {
            return signFields;
        }

        public String getPsnIDCard() {
            return psnIDCard;
        }

        public void setPsnIDCard(String psnIDCard) {
            this.psnIDCard = psnIDCard;
        }
    }

    public static class OrgInfo {
        private String orgCardId;
        private String orgName;

        public String getOrgCardId() {
            return orgCardId;
        }

        public String getOrgName() {
            return orgName;
        }
    }

    public static class SignField {
        private String customBizNum;
        private String fileId;
        private int signFieldStyle;
        private String positionPage;
        private int positionX;
        private int positionY;

        public String getCustomBizNum() {
            return customBizNum;
        }

        public String getFileId() {
            return fileId;
        }

        public int getSignFieldStyle() {
            return signFieldStyle;
        }

        public String getPositionPage() {
            return positionPage;
        }

        public int getPositionX() {
            return positionX;
        }

        public int getPositionY() {
            return positionY;
        }
    }
}
