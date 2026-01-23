package models;

import java.sql.Timestamp;

public class Material {
    private String materialId;
    private String submissionId;
    private String fileName;
    private String fileType;
    private Timestamp uploadDate;
    private String filePath;

    public Material(String materialId, String submissionId, String fileName, String fileType, Timestamp uploadDate,
            String filePath) {
        this.materialId = materialId;
        this.submissionId = submissionId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
        this.filePath = filePath;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    public String getFilePath() {
        return filePath;
    }
}

