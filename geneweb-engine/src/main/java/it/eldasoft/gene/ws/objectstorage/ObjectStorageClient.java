package it.eldasoft.gene.ws.objectstorage;

import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Client per contattare l'ObjectStorage-ms.
 */
public final class ObjectStorageClient {

    private static final String BUCKET_REF_HEADER =  "bucket_ref";
    private static final String UUID_HEADER = "uuid";
    private static final String TENANT_HEADER = "X-TENANT-ID";

    private final Client client;
    private final ObjectStorageConfiguration obs_conf;


    public ObjectStorageClient(String tenant, String url) throws GestoreException {
        this(new ObjectStorageConfiguration(tenant,url), new JSONClientConfigurer());
    }

    public ObjectStorageClient(
        final ObjectStorageConfiguration obs_conf
        , final JSONClientConfigurer json_configurer
    ) {
        this.obs_conf = obs_conf;
        client = ClientBuilder.newClient(
                    new ClientConfig()
                        .register(MultiPartFeature.class)
                        .register(json_configurer)
                )
        ;
    }

    /**
     * Inizializza la cartella dove fare l'upload creandoci un segnaposto (restituisce, bucket_ref e bucket_uuid)
     * @return
     */
    private UploadInfo initialize() throws GestoreException {
        return inner_call(
            "GET"
            , "/v1/upload/init"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }

    /**
     * Richiamare sempre, ma, fa quello che deve solo se è stato abilitato l'upload su bucket.
     *
     * @param to_upload
     * @return
     */
    public UploadInfo full_upload(final byte[] to_upload) throws GestoreException {
        if (to_upload != null && to_upload.length > 0) {
            UploadInfo info = initialize();
            upload(info.uuid(), to_upload);
            confirm(info.uuid());
            return info;
        } else
            return new UploadInfo(null, null);
    }

    /**
     * Si prega di usare full_upload(...) x usi esterni alla classe.
     * @param bucket_uuid
     * @param to_upload
     */
    private void upload(final String bucket_uuid, final byte[] to_upload) throws GestoreException {
        try (final FormDataMultiPart part = new FormDataMultiPart()) {
            final Entity<MultiPart> entity = Entity.entity(
                    part.bodyPart(
                        new StreamDataBodyPart(
                            "file"
                            , new ByteArrayInputStream(to_upload)
                            , "someFile.bin"
                            , MediaType.APPLICATION_OCTET_STREAM_TYPE
                        )
                    )
                    , MediaType.MULTIPART_FORM_DATA_TYPE
            );

            inner_call(
                "POST"
                , "/v1/upload/"
                , Optional.of(entity)
                , ListUtils.of(
                    Pair.of(UUID_HEADER, bucket_uuid)
                    , Pair.of(TENANT_HEADER, obs_conf.getTenant())
                    , Pair.of("COUNTER", 0) // Diamo per assunto che facciamo l'upload di un solo chunk
                )
            );

        } catch (IOException e) {
            throw new GestoreException("Gestione Bucket: errore nella creazione del pacchetto dati", null, null);
        }
    }

    /**
     * Cancella il segnaposto dalla cartella contenente i chunk.
     *
     * @param bucket_uuid
     * @return
     */
    private String confirm(final String bucket_uuid) throws GestoreException {
        return inner_call(
            "GET"
            , "/v1/upload/confirmUpload"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(UUID_HEADER, bucket_uuid)
                , Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }

    /**
     * Effettua la download dei chunk dal bucket.
     * Attualmente restituisce un byte[], ma è possibile farsi restituire uno stream di dati.
     *
     * @param bucket_ref
     * @param bucket_uuid
     * @return
     */
    public byte[] download(final String bucket_ref, final String bucket_uuid) throws GestoreException {
        if (!StringUtils.isNotEmpty(bucket_ref))
            throw new GestoreException("Gestione Bucket: per effettuare il download di un documento 'bucket_ref' è un campo obbligatorio",null,null);
        if (!StringUtils.isNotEmpty(bucket_uuid))
            throw new GestoreException("Gestione Bucket: per effettuare il download di un documento 'bucket_uuid' è un campo obbligatorio",null,null);

        final InputStream stream = inner_call(
                "POST"
                , "/v1/download/"
                , Optional.empty()
                , ListUtils.of(
                    Pair.of(UUID_HEADER, bucket_uuid + "/0")    // Nel nostro caso abbiamo un solo chunk
                    , Pair.of(BUCKET_REF_HEADER, bucket_ref)
                    , Pair.of(TENANT_HEADER, obs_conf.getTenant())
                )
        );
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            int nRead = -1;
            byte[] data = new byte[16384];
            while ((nRead = stream.read(data, 0, data.length)) != -1)
                bao.write(data, 0, nRead);
            return bao.toByteArray();
        } catch (IOException e) {
            throw new GestoreException("Gestione Bucket: errore nella lettura del pacchetto dati", null, e);
        }
    }

    /**
     * Effettua la restore dei chunk cancellati logicamente sul bucket.
     *
     * @param bucket_ref
     * @param bucket_uuid
     */
    public void logical_restore_folder(final String bucket_ref, final String bucket_uuid) throws GestoreException {
        inner_call(
            "POST"
            , "/v1/delete/logical-restore"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(UUID_HEADER, bucket_uuid)
                , Pair.of(BUCKET_REF_HEADER, bucket_ref)
                , Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }

    /**
     * Sposta i chunk su un'altra cartella destinata alla cancellazione.
     * I file su questa cartella vengono cancellati 1 giorno dopo la loro persistenza.
     *
     * @param bucket_ref
     * @param bucket_uuid
     */
    public void logical_delete_folder(final String bucket_ref, final String bucket_uuid) throws GestoreException {
        inner_call(
            "DELETE"
            , "/v1/delete/logical-folder"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(UUID_HEADER, bucket_uuid)
                , Pair.of(BUCKET_REF_HEADER, bucket_ref)
                , Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }

    public Long first_chunk_size(final String bucket_ref, final String bucket_uuid) throws GestoreException {
        return inner_call(
            "GET"
            , "/v1/metadata/size"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(UUID_HEADER, bucket_uuid + "/0")
                , Pair.of(BUCKET_REF_HEADER, bucket_ref)
                , Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }


    /**
     * Cancella definitivamente una cartella dal bucket.
     *
     * @param bucket_ref
     * @param bucket_uuid
     */
    public void delete_folder(final String bucket_ref, final String bucket_uuid) throws GestoreException {
        inner_call(
            "DELETE"
            , "/v1/delete/folder"
            , Optional.empty()
            , ListUtils.of(
                Pair.of(UUID_HEADER, bucket_uuid)
                , Pair.of(BUCKET_REF_HEADER, bucket_ref)
                , Pair.of(TENANT_HEADER, obs_conf.getTenant())
            )
        );
    }

    @SafeVarargs
    private final <T> T inner_call(
            final String method
            , final String path
            , final Optional<Entity<?>> entity
            , final List<Pair<String, Object>> headers
            , final T...return_type
    ) throws GestoreException {
        try {
            Class<T> to_return = reified_class(return_type);
            Invocation.Builder builder = client.target(obs_conf.getUrl()).path(path).request();
            if (headers != null && !headers.isEmpty())
                headers.forEach(it -> builder.header(it.key(), it.value()));

            Invocation invocation = StringUtils.equals(method, "PUT") || StringUtils.equals(method, "POST")
                    ? builder.build(method, entity.orElse(Entity.json("")))
                    : builder.build(method);

            return invocation.invoke(to_return);
        }catch (Exception e) {
            throw new GestoreException("Gestione Bucket: errore nell'esecuzione della chiamata al servizio", null, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> reified_class(final T...array) throws GestoreException {
        if (array == null) throw new GestoreException("Nessun argomento fornito",null,null);
        return (Class<T>) array.getClass().getComponentType();
    }

}
