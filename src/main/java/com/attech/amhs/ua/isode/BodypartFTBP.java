/*
 * BodypartFTBP - File Transfer Body Part for AMHS X.400 messages
 * Supports file attachments as per EUR Doc 047
 */
package com.attech.amhs.ua.isode;

import com.isode.x400.highlevel.Bodypart;
import com.isode.x400.highlevel.X400APIException;

/**
 * Represents a file-transfer-body-part (FTBP) in X.400 messages
 * Used for attaching binary files to AMHS messages
 */
public class BodypartFTBP extends Bodypart {
    
    private String fileName;
    private byte[] fileContent;
    private String fileType;
    private long fileSize;
    
    /**
     * Create a file transfer body part with filename and content
     * @param fileName Name of the file
     * @param fileContent Binary content of the file
     */
    public BodypartFTBP(String fileName, byte[] fileContent) throws X400APIException {
        super(Bodypart_Type.BODYPART_FTBP);
        this.fileName = fileName != null ? fileName : "attachment.bin";
        this.fileContent = fileContent != null ? fileContent : new byte[0];
        this.fileSize = this.fileContent.length;
    }
    
    /**
     * Create a file transfer body part with just filename (empty content)
     * @param fileName Name of the file
     */
    public BodypartFTBP(String fileName) throws X400APIException {
        this(fileName, new byte[0]);
    }
    
    /**
     * Get the file name
     * @return File name string
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Set the file name
     * @param fileName File name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Get the file content as byte array
     * @return Byte array of file content
     */
    public byte[] getFileContent() {
        return fileContent;
    }
    
    /**
     * Set the file content
     * @param fileContent Byte array of file content
     */
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent != null ? fileContent : new byte[0];
        this.fileSize = this.fileContent.length;
    }
    
    /**
     * Get the file type/MIME type
     * @return File type string or null
     */
    public String getFileType() {
        return fileType;
    }
    
    /**
     * Set the file type/MIME type
     * @param fileType File type (e.g., "application/octet-stream", "text/plain")
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    /**
     * Get the file size in bytes
     * @return File size
     */
    public long getFileSize() {
        return fileSize;
    }
    
    /**
     * Check if file size exceeds maximum allowed size
     * @param maxSize Maximum allowed size in bytes
     * @return true if file exceeds maximum size
     */
    public boolean exceedsMaxSize(long maxSize) {
        return fileSize > maxSize;
    }
    
    /**
     * Get file extension from filename
     * @return File extension or empty string
     */
    public String getFileExtension() {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * Infer file type from file extension
     * @return Inferred MIME type
     */
    public String inferFileType() {
        String ext = getFileExtension();
        
        switch (ext) {
            case "txt":
                return "text/plain";
            case "bin":
                return "application/octet-stream";
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "xml":
                return "application/xml";
            case "json":
                return "application/json";
            default:
                return "application/octet-stream";
        }
    }
}
