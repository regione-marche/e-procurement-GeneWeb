package it.eldasoft.gene.utils;

import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.lang3.StringUtils;

public class ConfigurazionePerUffintUtils {

    /**
     * La funzione, data una property e un codein, restituisce il valore associato alla chiave property/codein.
     * Se il codein è nullo, restituisce il valore associato alla chiave property.
     * @param property
     * @param codein
     * @return
     */
    public static String getConfigurazionePerUfficioIntestatario(String property, String codein) {
        return getConfigurazionePerUfficioIntestatario(property,codein,null);
    }

    /** La funzione, data una property e un codein, restituisce il valore associato alla chiave property/codein.
     *  Se la chiave esiste, ma non è valorizzata, restituisce il valore associato alla chiave property.
     *  Se il codein è nullo, restituisce il valore associato alla chiave property.
     * @param property
     * @param codein
     * @return
     */
    public static String getOptConfigurazionePerUfficioIntestatario(String property, String codein) {
        return getOptConfigurazionePerUfficioIntestatario(property,codein,null);
    }

    /**
     * La funzione, data una property e un codein, restituisce il valore associato alla chiave property/codein.
     * Se il codein è nullo, restituisce il valore associato alla chiave property.
     * Il separatore di default è "/". Si lascia la possibilità di specificare un separatore diverso.
     * @param property
     * @param codein
     * @return
     */
    public static String getConfigurazionePerUfficioIntestatario(String property, String codein, String separatore) {
        if(StringUtils.isBlank(codein))
            return ConfigManager.getValore(property);
        else {
            if(separatore == null) separatore = "/";
            return ConfigManager.getValore(property + separatore + codein);
        }
    }

    /** La funzione, data una property e un codein, restituisce il valore associato alla chiave property/codein".
     *  Se la chiave esiste, ma non è valorizzata, restituisce il valore associato alla chiave property.
     *  Se il codein è nullo, restituisce il valore associato alla chiave property.
     *  Il separatore di default è "/". Si lascia la possibilità di specificare un separatore diverso.
     * @param property
     * @param codein
     * @return
     */
    public static String getOptConfigurazionePerUfficioIntestatario(String property, String codein,String separatore) {
        String configurazionePerUffintSpecifico = null;
        if (StringUtils.isNotEmpty(codein)) {
            configurazionePerUffintSpecifico = getConfigurazionePerUfficioIntestatario(property,codein,separatore);
            if (StringUtils.isNotEmpty(configurazionePerUffintSpecifico)) return configurazionePerUffintSpecifico;
        }
        return ConfigManager.getValore(property);
    }
}
