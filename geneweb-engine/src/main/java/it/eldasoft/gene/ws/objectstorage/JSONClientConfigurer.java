package it.eldasoft.gene.ws.objectstorage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.ext.ContextResolver;
import java.text.DateFormat;

public class JSONClientConfigurer implements ContextResolver<ObjectMapper> {
    private ObjectMapper mapper = new ObjectMapper();

    public JSONClientConfigurer() {
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
//        this.mapper.setDateFormat(new RFC3339DateFormat());
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.mapper.setDateFormat(dateFormat);
    }

    public ObjectMapper getContext(Class<?> type) {
        return this.mapper;
    }
}