/*
 * Created on 26/set/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.db.domain.invcom.PKInvioComunicazione;


/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_INVCOM e tabelle figlie.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public interface InvioComunicazioniDao {
  void updateStatoComunicazione(PKInvioComunicazione pk, String stato);

  void insertComunicazione(InvioComunicazione comunicazione);

  void insertDestinatarioComunicazione(DestinatarioComunicazione destinatario);

  void deleteDestinatariComunicazione(PKInvioComunicazione pk);

  ModelloComunicazione getModelloComunicazioneByGenere(int genere);
}
