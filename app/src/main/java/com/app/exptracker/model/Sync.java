package com.app.exptracker.model;

public class Sync {

    String syncId;
    String syncDate;
    String fileId;
    String fileName;
    String syncStatus;
    String accountName;
   /* public Sync(String syncId, String syncDate, String fileId, String fileName, String syncStatus) {
        this.syncId = syncId;
        this.syncDate = syncDate;
        this.fileId = fileId;
        this.fileName = fileName;
        this.syncStatus = syncStatus;
    }*/


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

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

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
