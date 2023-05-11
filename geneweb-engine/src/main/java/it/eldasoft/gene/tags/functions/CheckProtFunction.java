/*
 * Created on 7-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.PageContext;

/**
 * Oggetto per il controllo delle protezioni di un determinato utente
 * @author marco.franceschin
 *
 */
public class CheckProtFunction {

  /**
   * Funzione che verifica le protezioni di un determinato utente
   * @param context PageContext
   * @param key Chieve della protezione TIPO.AZIONE.OGGETTO
   * @return valore trovato (di default è false)
   */
  public static Boolean check(PageContext context, String key) {
    return new Boolean(UtilityTags.checkProtection(context,key,true));
  }
  
  public static Boolean check(PageContext context, String key, String obj) {
    // Se non è settato l'oggetto si può sempre fare
    if(obj==null || obj.length()==0)
      return new Boolean(true);
    return check(context,key+"."+obj);
  }
  /**
   * Funzione che verifica la protezione aggiungendo il schema maschera e pagina al nome dell'oggetto
   * @param context
   * @param key Tipo di oggetto e azione. Es. FUNZ.VIS
   * @param obj Nome dell'oggetto. Esempio FunzioneCopiaLavoro
   * @return True se si puo eseguire l'operazione altrimenti false.
   */
  public static Boolean checkGene(PageContext context, String key, String obj) {
    // Se non è settato l'oggetto si può sempre fare
    if(obj==null || obj.length()==0)
      return new Boolean(true);
    return check(context,key+"."+UtilityTags.getIdRequest(context)+"."+obj);
  }
  
  /**
   * Funzione che verifica la funzionalita delle funzione. Il nome viene creato aggiungendo 
   * tipoFunzione.SCHEMA.MASCHERA.idFunzione
   * @param context Servlet Context
   * @param tipoFunzione Tipo di funzione
   * @param idFunzione Identificativo della funzione
   * @return True se abilitato altrimanti false
   */
  public static Boolean checkProtFunz(PageContext context, String tipoFunzione, String idFunzione){
    if(idFunzione==null || idFunzione.length()==0)
      return new Boolean(true);
    if(tipoFunzione==null || tipoFunzione.length()==0)
      tipoFunzione="ALT";
    return check(context,"FUNZ.VIS."+tipoFunzione+"."+UtilityTags.getIdRequest(context)+"."+idFunzione);
    
  }
  
}
