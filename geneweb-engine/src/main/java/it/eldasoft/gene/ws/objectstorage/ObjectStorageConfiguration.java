package it.eldasoft.gene.ws.objectstorage;

import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class ObjectStorageConfiguration {

    private final String tenant;
    private final String url;

    public ObjectStorageConfiguration(final String tenantId, final String url) throws GestoreException {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new GestoreException("Per gestire il caricamento e il download dei file su Bucket è necessario configurare la proprietà 'objectStorage.tenantId'",null,null);
        }
        if (url == null || url.isEmpty()) {
            throw new GestoreException("Per gestire il caricamento e il download dei file su Bucket è necessario configurare la proprietà 'objectStorage.url'",null,null);
        }
        this.tenant = tenantId;
        this.url = url;
    }

    public String getTenant() {
        return tenant;
    }

    public String getUrl() {
        return url;
    }
}
