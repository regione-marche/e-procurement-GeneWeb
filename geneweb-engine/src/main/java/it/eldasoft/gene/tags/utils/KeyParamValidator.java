package it.eldasoft.gene.tags.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

/**
 * Classe usata per la validazione del parametro "key" nelle request.
 *
 * @author Alvise Gorinati
 */
public final class KeyParamValidator {
	
	// Costanti
	public static final String KEY_FORMAT_VALIDATION_REGEX = "^[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+=[T|N]:[a-zA-Z0-9-_\\./ \\$@]+$";

	/**
	 * Bean che rappresenta un parametro "key".
	 *
	 * @author Alvise Gorinati
	 */
	private static class KeyParam {
		
		// Costanti		
		private static final String KEY_VALIDATION_ERROR_MSG = "La chiave non è stata validata correttamente: %s.";
		
		// Campi
		private String entity;
		private String name;
		private short type;
		private String value;
		
		// Costruttore
		public KeyParam(final String key) {
			if (!Pattern.matches(KEY_FORMAT_VALIDATION_REGEX, key))
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
	
	// Costanti
	private static final RuntimeException VALIDATION_EXCEPTION = new RuntimeException("Errore durante la validazione della chiave");
	
	// Variabili
	private static List<KeyParam> keys;
	
	// Metodi
	public static void validate(final String fullKey) {
		keys = new ArrayList<KeyParam>();
		
		try {
			for (final String key : fullKey.split(";"))
				keys.add(new KeyParam(key));
			
			final Tabella table = validateTable();
			validateKeyFields(table.getCampiKey());
			validateFieldType(table.getCampiKey());
			validateKeyValue(table.getCampiKey());
		} catch (RuntimeException e) {
			throw e;
		} finally {
			keys = null;
		}
	}
	
	/**
	 * Valida la tabella passata nelle chiavi.
	 * 
	 * @return Oggetto della tabella passata nelle chiavi
	 * @throws JspException
	 */
	private static Tabella validateTable() {
		// Controllo che tutte le chiavi appartengano alla stessa entità
		String entity = null;
		for (final KeyParam key : keys) {
			if (entity == null)
				entity = key.getEntity();
			else if (!entity.equals(key.getEntity()))
				throw VALIDATION_EXCEPTION;
		}
		
		// Controllo che la tabella sia effettivamente presente a database
		final Tabella table = DizionarioTabelle.getInstance().getDaNomeTabella(entity);
		if (table == null)
			throw VALIDATION_EXCEPTION;
		
		return table;
	}
	
	/**
	 * Valida i campi chiave dell'entità
	 * 
	 * @param keyFields
	 * 			Campi chiave
	 * @throws JspException
	 */
	private static void validateKeyFields(final List<Campo> keyFields) {
		// Se non ho tutti i campi chiave, lancio un'eccezione
		if (keyFields.size() < keys.size())
			throw VALIDATION_EXCEPTION;
		
		// Controllo se nelle chiavi passate a questa classe sono presenti tutte quelle necessarie
		final List<String> keyFieldNames = new ArrayList<String>();
		for (final Campo field : keyFields) {
			keyFieldNames.add(field.getNomeCampo());
		}
		
		boolean match = true;
		for (final KeyParam key : keys) {
			match = keyFieldNames.contains(key.getName());
			if (!match)
				throw VALIDATION_EXCEPTION;
		}
	}
	
	/**
	 * Controlla che i tipi delle chiavi rispettino il valore ricevuto.
	 * 
	 * @param keyFields
	 * 			Campi chiave
	 * @throws JspException
	 */
	private static void validateFieldType(final List<Campo> keyFields) {
		for (final Campo field : keyFields) {
			for(final KeyParam key : keys) {
				if (field.getNomeCampo().equals(key.getName()) &&
						field.getTipoColonna() != key.getType())
					throw VALIDATION_EXCEPTION;
			}
		}
	}
	
	private static void validateKeyValue(final List<Campo> keyFields) {
		for (final Campo field : keyFields) {
			for (final KeyParam key : keys) {
				if (key.getName().equals(field.getNomeCampo()))
					if (key.getValue().length() > field.getLunghezza())
						throw VALIDATION_EXCEPTION;
			}
		}
	}
	
}
