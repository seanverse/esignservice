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


    /*
    {
        "files": [
        {
            "fileId": "12345",
                "fileName": "example.pdf",
                "downloadUrl": "http://example.com/files/12345"
        },
        {
            "fileId": "67890",
                "fileName": "document.docx",
                "downloadUrl": "http://example.com/files/67890"
        }],
        "attachments": [
        {
            "fileId": "54321",
                "fileName": "attachment.pdf",
                "downloadUrl": "http://example.com/files/54321"
        }]
    }
    */
}

