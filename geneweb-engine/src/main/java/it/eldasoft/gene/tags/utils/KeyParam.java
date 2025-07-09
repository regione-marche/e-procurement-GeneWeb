package it.eldasoft.gene.tags.utils;

import java.util.regex.Pattern;

import it.eldasoft.utils.metadata.domain.Campo;

/**
 * Bean che rappresenta un parametro "key".
 *
 * @author Alvise Gorinati
 */
public class KeyParam {
    
    // Costanti		
    private static final String KEY_VALIDATION_ERROR_MSG = "La chiave non è stata validata correttamente: %s.";
    
    // Campi
    private String entity;
    private String name;
    private short type;
    private String value;
    
    // Costruttore
    public KeyParam(final String key) {
        if (!Pattern.matches(KeyParamValidator.KEY_FORMAT_VALIDATION_REGEX, key))
            throw new RuntimeException(String.format(KEY_VALIDATION_ERROR_MSG, key));
        
        entity = key.substring(0, key.indexOf('.'));
        name = key.substring(key.indexOf('.') + 1, key.indexOf('='));
        final char typeChar = key.charAt(key.indexOf('=') + 1);
        if (typeChar == 'T')
            type = Campo.TIPO_STRINGA;
        else if (typeChar == 'N')
            type = Campo.TIPO_INTERO;
        value = key.substring(key.indexOf(':') + 1);
    }
    
    // Getters & Setters
    public String getEntity() {
        return entity;
    }
    
    public String getName() {
        return name;
    }
    
    public short getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
}
