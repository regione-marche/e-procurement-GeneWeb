/*
 * Created on 09/nov/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

import java.util.Vector;

/**
 * Gestore per il campo login utilizzato con codifica standard ad esempio per
 * USRSYS.SYSNOM.
 *
 * @author Stefano.Sabbadin
 * @since 1.4.4
 */
public class GestoreCampoLogin extends AbstractGestoreCampo {

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {
    return null;
  }

  @Override
  public String getClasseEdit() {
    return null;
  }

  @Override
  public String getClasseVisua() {
    return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String getValore(String valore) {
//    String risultato = null;
//    if (valore != null)
//      try {
//        ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
//            FactoryCriptazioneByte.CODICE_CRIPTAZIONE_LEGACY,
//            valore.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//        risultato = new String(criptatore.getDatoNonCifrato());
//      } catch (CriptazioneException e) {
//        // i parametri sono corretti, per cui non succederà mai alcuna eccezione
//      }
//    return risultato;
    return valore;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {
//    String risultato = null;
//    if (valore != null)
//      try {
//        ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
//            FactoryCriptazioneByte.CODICE_CRIPTAZIONE_LEGACY,
//            valore.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//        risultato = new String(criptatore.getDatoNonCifrato());
//      } catch (CriptazioneException e) {
//        // i parametri sono corretti, per cui non succederà mai alcuna eccezione
//      }
//    return risultato;
    return valore;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  @Override
  protected void initGestore() {
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

}
