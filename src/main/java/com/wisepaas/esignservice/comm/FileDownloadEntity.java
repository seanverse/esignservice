package com.wisepaas.esignservice.comm;

import java.util.List;

/**
 * FileDownloadEntity
 */
public class FileDownloadEntity extends ESignResponse<FileDownloadEntity.FileDownloadData> {
    public static class FileDownloadData {
        private List<SignFile> files;
        private List<SignFile> attachments;

        public List<SignFile> getFiles() {
            return files;
        }

        public List<SignFile> getAttachments() {
            return attachments;
        }
    }

    public static class SignFile {
        private String fileId;
        private String fileName;
        private String downloadUrl;

        public String getFileId() {
            return fileId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }
}

