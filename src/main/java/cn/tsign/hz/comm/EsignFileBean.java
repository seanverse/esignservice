package cn.tsign.hz.comm;

import cn.tsign.hz.exception.EsignOPException;

import java.io.File;

/**
 * @version JDK1.7
 * @description 文件基础信息封装类
 * @date 2020/10/26 14:54
 */
public class EsignFileBean {
    //文件名称
    private String fileName;
    //文件大小
    private int fileSize;
    //文件内容MD5
    private String fileContentMD5;
    //文件地址
    private String filePath;
    // pdf文件
    private boolean isPDF;


    public EsignFileBean(String filePath) throws EsignOPException {
        this.filePath = filePath;
        this.fileContentMD5 = FileTransformation.getFileContentMD5(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new EsignOPException("文件不存在");
        }
        this.fileName = file.getName();
        this.fileSize = (int) file.length();
        this.isPDF = EsignFileBean.isPDFByName(fileName);
    }

    public static boolean isPDFByName(String fileName) {
        return fileName.endsWith(".pdf");
    }


    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileContentMD5() {
        return fileContentMD5;
    }

    public boolean isPDF() {
        return isPDF;
    }

    /**
     * 传入本地文件地址获取二进制数据
     *
     * @return
     * @throws EsignOPException
     */
    public byte[] getFileBytes() throws EsignOPException {
        return FileTransformation.fileToBytes(filePath);
    }
}
