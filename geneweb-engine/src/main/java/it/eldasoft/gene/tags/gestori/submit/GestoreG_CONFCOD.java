        /*
 * Created on: 06-apr-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.submit;


import org.springframework.transaction.TransactionStatus;


import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;

/**
 * Gestore di submit dell'entita' G_CONFCOD
 * 
 * @author Francesco.DiMattei
 */
public class GestoreG_CONFCOD extends AbstractGestoreEntita {

  public long caratteri = 0;
  
  
  public String getEntita() {
    return "G_CONFCOD";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) 
      throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postInsert(DataColumnContainer datiForm) 
      throws GestoreException {   
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    String noment = datiForm.getColumn("G_CONFCOD.NOMENT").getValue().getStringValue();
    String nomcam = datiForm.getColumn("G_CONFCOD.NOMCAM").getValue().getStringValue();
    String codcal = datiForm.getColumn("G_CONFCOD.CODCAL").getValue().getStringValue();
    String chkcodificaattiva = datiForm.getColumn("CHKCODIFICAATTIVA").getValue().getStringValue();
    String pattern_Formato_valido = "[A-Za-z0-9§\\.\\/\\$_\\-\\@ ]*";
    String format = "";
    String codcalReplace = "";
    String nomentNomcam = "";
    long contatori = 0;
    String check = "";
    long lunghezzaCampo = 0;
    
    //i controlli scattano solo in caso di codifica automatica attivata
    if("0".equals(chkcodificaattiva)){
      return;
    }      
       
    //Controllo formato
    format = codcal;
    while (format.length() > 0) {
      //Contatore
      if("<".equals(format.substring(0,1))){
        format = format.substring(1);
        contatori++;
        //Controllo che ci sia la chisura del token
        if(!format.contains(">")){
          throw new GestoreException("Sintassi non corretta", "configCodificaAutomatica.sintassiNonCorretta");
        }
        //Controllo che cominci almeno per 0 o per 9
        if(!"0".equals(format.substring(0, 1)) && !"9".equals(format.substring(0, 1))){
          throw new GestoreException("Sintassi non corretta", "configCodificaAutomatica.sintassiNonCorretta");
        }
        check = format.substring(0, 1);
        //Controllo che siano tutti 0 o tutti 9
        while (!">".equals(format.substring(0, 1))) {
          if(!check.equals(format.substring(0, 1))){
            throw new GestoreException("Sintassi non corretta", "configCodificaAutomatica.sintassiNonCorretta");
          }
          caratteri++;
          format = format.substring(1);
        }
        format = format.substring(1);
      }else if("\"".equals(format.substring(0, 1))){ //Stringa
        format = format.substring(1);
        //Controllo che ci sia la chisura del token
        if(!format.contains("\"")){
          throw new GestoreException("Sintassi non corretta", "configCodificaAutomatica.sintassiNonCorretta");
        }
        caratteri += format.indexOf("\"");
        format = format.substring(format.indexOf("\"") +1);
      }else{
        throw new GestoreException("Sintassi non corretta", "configCodificaAutomatica.sintassiNonCorretta");     
      }
    }
    //Controllo che sia specificato un contatore.
    if(contatori < 1){
      throw new GestoreException("Sintassi non corretta. Specificare un contatore!", "configCodificaAutomatica.specificareUnContatore");
    }
    //Controllo che sia specificato solo un contatore.
    if(contatori > 1){
      throw new GestoreException("Sintassi non corretta. Specificare solo un contatore!", "configCodificaAutomatica.specificareSoloUnContatore");
    }

    
    //Controlla la validità dei caratteri indicati
    codcalReplace = codcal.replaceAll("[<>\"]", "");
    if ( !codcalReplace.matches(pattern_Formato_valido)){
      throw new GestoreException("Sintassi non corretta. Presenti caratteri non accettati. I caratteri specificati nel criterio come prefisso e/o suffisso devono appartenere all'insieme dei caratteri A-Z a-z 0-9 § . / $ _ - @ e spazio", "configCodificaAutomatica.carattNonAccettati");
    }
    
    
    //Controllo che il numero di caratteri non sia superiore alla lunghezza del campo
    nomentNomcam = noment.toUpperCase()+"."+nomcam.toUpperCase();
    Campo campoNomentNomcam = DizionarioCampi.getInstance().getCampoByNomeFisico(
        nomentNomcam);
    lunghezzaCampo = campoNomentNomcam.getLunghezza();
 
    if (caratteri > lunghezzaCampo){
      throw new GestoreException("Sintassi non corretta. Numero di caratteri superiore alla lunghezza del campo!", "configCodificaAutomatica.numCarattSuperioreLunghezzaCampo");
    }
  
    
  }

  public void postUpdate(DataColumnContainer datiForm) 
      throws GestoreException {
  }

}