package it.eldasoft.gene.ws.objectstorage;

public class UploadInfo {

    private String uuid;
    private String bucket_ref;

    public UploadInfo() { }
    public UploadInfo(final String uuid, final String bucket_ref) {
        this.uuid = uuid;
        this.bucket_ref = bucket_ref;
    }

    public String uuid() {
        return uuid;
    }
    public String bucket_ref() {
        return bucket_ref;
    }

    void setUuid(String uuid) {
        this.uuid = uuid;
    }
    void setBucket_ref(String bucket_ref) {
        this.bucket_ref = bucket_ref;
    }

    @Override
    public String toString() {
        return String.format("{ \"uuid\": \"%s\", \"bucket_ref\": \"%s\" }", uuid, bucket_ref);
    }

}
