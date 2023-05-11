package it.eldasoft.gene.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;

public class GestoreW_CONFIG extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "W_CONFIG";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    
    String criptato = datiForm.getString("W_CONFIG.CRIPTATO");

    try {
      if ("1".equals(criptato)) {
        String plaintext = datiForm.getString("PLAINTEXT");
        if (plaintext != null && plaintext.trim().length() > 0) {
          ICriptazioneByte valoreICriptazioneByte = null;
          valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), plaintext.getBytes(),
              ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
          String valore = new String(valoreICriptazioneByte.getDatoCifrato());
          datiForm.setValue("W_CONFIG.VALORE", valore);
        } else {
          datiForm.setValue("W_CONFIG.VALORE", plaintext);
        }
      }
    } catch (CriptazioneException e) {
      throw new GestoreException("Errore durante la criptazione del valore", "configurazione.criptazione.error", e);
    }

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    String chiave = datiForm.getString("W_CONFIG.CHIAVE");
    String valore = datiForm.getString("W_CONFIG.VALORE");
    if (valore == null) valore = new String("");
    ConfigManager.ricaricaProprietaDB(chiave, valore);
    
    String descrEvento = "";
    descrEvento = "Modifica parametro di configurazione '" + chiave + "'";
    String sezione = datiForm.getString("W_CONFIG.SEZIONE");
    sezione = UtilityStringhe.convertiNullInStringaVuota(sezione);
    if(!"".equals(sezione)){
      descrEvento+=" (" + sezione + ")";
    }
    descrEvento+=". Valore assegnato: " + valore;
    LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
    logEvento.setLivEvento(1);
    logEvento.setOggEvento("");
    logEvento.setCodEvento("SET_CONFIG");
    logEvento.setDescr(descrEvento);
    logEvento.setErrmsg("");
    LogEventiUtils.insertLogEventi(logEvento);
    
  }

}
