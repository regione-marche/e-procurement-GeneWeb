package it.eldasoft.gene.tags.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.validation.ValidationException;

import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;

/**
 * Classe usata per la validazione del parametro "key" nelle request.
 *
 * @author Alvise Gorinati
 */
public class KeyParamValidator {
	
	// Costanti
	private static final String VALIDATION_ERROR_MSG = 
		"Errore durante la validazione della chiave: %s";
	
	private static final String FIELD_NOT_OF_ENTITY_ERROR_MSG =
		"la chiave \"%s\" non appartiene all'entità \"%s\"";

	private static final String WRONG_FIELD_QUANTITY_ERROR_MSG =
		"aspettati %d campi, ricevuti %d";

	private static final String UNKNOWN_FIELD_FOR_TABLE_ERROR_MSG =
		"la chiave \"%s\" non fa parte dell'entità";

	private static final String COLUMN_TYPE_MISMATCH_ERROR_MSG =
		"aspettato parametro di tipo '%d', ricevuto '%d'";

	private static final String PARAM_LENGTH_MISMATCH_ERROR_MSG =
		"aspettato campo di lunghezza %d, ricevuto %d";
	
	public static final String KEY_FORMAT_VALIDATION_REGEX = 
		"^[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+=[T|N]:[a-zA-Z0-9-_\\./ \\$@]+$";
	
	// Metodi
	public static void validate(final String fullKey) {
		List<KeyParam> keys = new ArrayList<KeyParam>();
		
		try {
			for (final String key : fullKey.split(";"))
				keys.add(new KeyParam(key));
			
			final Tabella table = validateTable(keys);
			validateKeyFields(keys, table.getCampiKey());
			validateFieldType(keys, table.getCampiKey());
			validateKeyValue(keys, table.getCampiKey());
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
	private static Tabella validateTable(List<KeyParam> keys) {
		// Controllo che tutte le chiavi appartengano alla stessa entità
		String entity = null;
		for (final KeyParam key : keys) {
			if (entity == null)
				entity = key.getEntity();
			else if (!entity.equals(key.getEntity())) {
				final String details = String.format(FIELD_NOT_OF_ENTITY_ERROR_MSG, key.getName(), entity);
				final String errorMsg = String.format(VALIDATION_ERROR_MSG, details);

				throw new ValidationException(errorMsg);
			}
		}
		
		// Controllo che la tabella sia effettivamente presente a database
		final Tabella table = DizionarioTabelle.getInstance().getDaNomeTabella(entity);
		if (table == null) {
			final String errorMsg = String.format(VALIDATION_ERROR_MSG, "la tabella ricercata non è presente a database");

			throw new ValidationException(errorMsg);
		}
		
		return table;
	}
	
	/**
	 * Valida i campi chiave dell'entità
	 * 
	 * @param keyFields
	 * 			Campi chiave
	 * @throws JspException
	 */
	private static void validateKeyFields(final List<KeyParam> keys, final List<Campo> keyFields) {
		// Se non ho tutti i campi chiave, lancio un'eccezione
		if (keyFields.size() < keys.size()) {
			final String details = String.format(WRONG_FIELD_QUANTITY_ERROR_MSG, keys.size(), keyFields.size());
			final String errorMsg = String.format(VALIDATION_ERROR_MSG, details);

			throw new ValidationException(errorMsg);
		}
		
		// Controllo se nelle chiavi passate a questa classe sono presenti tutte quelle necessarie
		final List<String> keyFieldNames = new ArrayList<String>();
		for (final Campo field : keyFields) {
			keyFieldNames.add(field.getNomeCampo());
		}
		
		boolean match = true;
		for (final KeyParam key : keys) {
			match = keyFieldNames.contains(key.getName());
			if (!match) {
				final String details = String.format(UNKNOWN_FIELD_FOR_TABLE_ERROR_MSG, key.getName());
				final String errorMsg = String.format(KEY_FORMAT_VALIDATION_REGEX, details);

				throw new ValidationException(errorMsg);
			}
		}
	}
	
	/**
	 * Controlla che i tipi delle chiavi rispettino il valore ricevuto.
	 * 
	 * @param keyFields
	 * 			Campi chiave
	 * @throws JspException
	 */
	private static void validateFieldType(final List<KeyParam> keys, final List<Campo> keyFields) {
		for (final Campo field : keyFields) {
			for(final KeyParam key : keys) {
				if (field.getNomeCampo().equals(key.getName()) &&
						field.getTipoColonna() != key.getType()) {
					final String details = String.format(COLUMN_TYPE_MISMATCH_ERROR_MSG, field.getTipoColonna(), key.getType());
					final String errorMsg = String.format(VALIDATION_ERROR_MSG, details);
							
					throw new ValidationException(errorMsg);
				}
			}
		}
	}
	
	private static void validateKeyValue(final List<KeyParam> keys, final List<Campo> keyFields) {
		for (final Campo field : keyFields) {
			for (final KeyParam key : keys) {
				if (key.getName().equals(field.getNomeCampo()))
					if (key.getValue().length() > field.getLunghezza()) {
						final String details = String.format(PARAM_LENGTH_MISMATCH_ERROR_MSG, field.getLunghezza(), key.getValue().length());
						final String errorMsg = String.format(VALIDATION_ERROR_MSG, details);

						throw new ValidationException(errorMsg);
					}
			}
		}
	}
	
}
