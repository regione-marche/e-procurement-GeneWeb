package it.eldasoft.gene.ws.objectstorage;

import it.eldasoft.gene.bl.LogEventiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class ObjectStorageUtils {

    private ObjectStorageUtils() { }

    private static ObjectStorageManager getObjectStorageManager(){
        ServletContext context = SpringAppContext.getServletContext();
        return (ObjectStorageManager) UtilitySpring.getBean("objectStorageManager", context, ObjectStorageManager.class);
    }

    /**
     * Utilizza usa transazione indipendente: usata per rimuovere il file caricato in caso di eccezione nella transazione principale.
     * Il valore di provenienza di provenienza del file corrisponderà a 1: objectstorage-ms
     */
    public static void rollbackFile(String bucketRef, String bucketUuid) {
        rollbackFile(bucketRef,bucketUuid,null);
    }
    /**
     * Utilizza usa transazione indipendente: usata per rimuovere il file caricato in caso di eccezione nella transazione principale.
     * Qui è possibile specificare il valore di provenienza del file 1: objectstorage-ms, 2: m-largefiles
     */
    public static void rollbackFile(String bucketRef, String bucketUuid, Long provenienza) {
        getObjectStorageManager().rollbackFile(bucketRef,bucketUuid,provenienza);
    }

    /**
     * utilizza la transazione corrente o ne apre una nuova: usata per eliminare il vecchio file in caso di update o per l'eliminazione.
     * Il valore di provenienza di provenienza del file corrisponderà a 1: objectstorage-ms
     */
    public static void deleteFile(String bucketRef, String bucketUuid) {
        deleteFile(bucketRef,bucketUuid,null);
    }
    /**
     * utilizza la transazione corrente o ne apre una nuova: usata per eliminare il vecchio file in caso di update o per l'eliminazione.
     * Qui è possibile specificare il valore di provenienza del file 1: objectstorage-ms, 2: m-largefiles
     */
    public static void deleteFile(String bucketRef, String bucketUuid, Long provenienza) {
        getObjectStorageManager().deleteFile(bucketRef,bucketUuid,provenienza);
    }

    public static void addFile(String idprg, Long iddocdig){
        getObjectStorageManager().addFile(idprg,iddocdig);
    }

    public static UploadInfo getUploadInfo(String query, Object[] parameters){
        return getObjectStorageManager().getUploadInfo(query,parameters);
    }
}
